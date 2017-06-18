package io.explod.organizer.features.home

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import io.explod.arch.data.Item
import io.explod.arch.data.hasPhoto
import io.explod.organizer.R
import io.explod.organizer.extensions.args
import io.explod.organizer.extensions.getModelWithFactory
import io.explod.organizer.extensions.mainActivity
import io.explod.organizer.extensions.showSnackbar
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.common.ConfirmationDialog
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.images.ImageLoader
import io.explod.organizer.service.tracking.LevelE
import io.explod.organizer.service.tracking.LevelW
import io.explod.organizer.service.tracking.LoggedException
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.BackpressureStrategy
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_item_detail.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ItemDetailFragment : BaseFragment() {

    companion object {
        fun new(itemId: Long): ItemDetailFragment {
            val frag = ItemDetailFragment()
            val args = Bundle()
            args.putLong(ARG_ITEM_ID, itemId)
            frag.arguments = args
            return frag
        }

        private val TAG = ItemDetailFragment::class.java.simpleName

        private const val ARG_ITEM_ID = "itemId"

        private const val DEBOUNCE_NAME_CHANGE_MILLIS = 2_400L

        private const val REQUEST_SELECT_IMAGE = 0x01
    }

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var imageLoader: ImageLoader

    val itemDetailModel by getModelWithFactory(ItemDetailViewModel::class, { ItemDetailViewModel.Factory(args.getLong(ARG_ITEM_ID)) })

    var nameChangeTracker = EditingTextWatcher()

    var descriptionChangeTracker = EditingTextWatcher()

    var item: Item? = null

    var loadedPhotoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rating_item.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) onRating(rating)
        }

        text_name.addTextChangedListener(nameChangeTracker)
        text_description.addTextChangedListener(descriptionChangeTracker)

        image_photo.setOnClickListener {
            selectPhotoForItem()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        itemDetailModel.item.observe(this, Observer<Item> {
            onItem(it)
        })
    }

    override fun onResume() {
        super.onResume()
        nameChangeTracker.observeTextChange("itemDetailChangeName") {
            onNameChanged(it)
        }
        descriptionChangeTracker.observeTextChange("itemDetailChangeDescription") {
            onDescriptionChanged(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mainActivity?.menuInflater
        inflater.inflate(R.menu.item_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete_item -> {
                tracker.event("itemDetailMenuDeleteItem")
                onDeleteItemClick()
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_SELECT_IMAGE) {
            onImageSelected(intent, resultCode == Activity.RESULT_OK)
        }
    }

    fun onImageSelected(intent: Intent?, ok: Boolean) {
        val context = this.context ?: return
        val item = this.item ?: return
        if (!ok || intent == null || intent.data == null) {
            snackbarWithSelectPhotoRetry(R.string.item_detail_select_image_cancelled)
        } else {
            image_photo?.showLoading()
            itemDetailModel.downloadPhotoForItem(context, item, intent.data)
                    .compose(bindToLifecycle<Any>())
                    .subscribeBy(onError = {
                        tracker.log(LevelW, TAG, "error downloading photo", it)
                        snackbarWithSelectPhotoRetry(R.string.item_detail_select_image_download_error)
                    })
        }

    }

    fun snackbarWithSelectPhotoRetry(@StringRes message: Int) {
        mainActivity?.showSnackbar(message, actionRes = R.string.item_detail_select_image_retry, action = {
            tracker.event("itemDetailRetrySelectImage")
            selectPhotoForItem()
        })
    }

    fun onItem(item: Item?) {
        if (item == null) return
        this.item = item

        setTextIfNotEditing(text_name, nameChangeTracker, item.name)
        setTextIfNotEditing(text_description, descriptionChangeTracker, item.description)

        rating_item?.rating = when (item.rating) {
            -1 -> 0f
            else -> item.rating.toFloat()
        }

        loadPhoto(item)
    }

    fun setTextIfNotEditing(textView: TextView?, textWatcher: EditingTextWatcher, newText: String) {
        if (textWatcher.isEditing) return
        if (textView == null) return
        if (newText != textView.text.toString()) {
            textView.text = newText
        }
    }

    fun onRating(rating: Float) {
        tracker.event("itemDetailChangeRating", mapOf("rating" to rating.toInt()))
        val item = this.item ?: return
        item.rating = rating.toInt()
        saveItem(item)
    }

    fun onNameChanged(name: String) {
        val item = this.item ?: return
        val textName = text_name ?: return
        val res = context?.resources ?: return
        if (name.isBlank()) {
            val message = res.getText(R.string.item_detail_item_missing_name)
            textName.error = message
            textName.requestFocus()
        } else {
            nameChangeTracker.isEditing = false
            textName.setError(null, null)
            item.name = name
            saveItem(item)
        }
    }

    fun onDescriptionChanged(name: String) {
        val item = this.item ?: return
        descriptionChangeTracker.isEditing = false
        item.description = name
        saveItem(item)
    }

    fun saveItem(item: Item) {
        itemDetailModel.saveItem(item)
                .compose(bindToLifecycle<Any>())
                .subscribeBy(onError = { tracker.recordException(LevelE, LoggedException("Unable to save item", it)) })
    }

    fun onDeleteItemClick() {
        val item = this.item ?: return
        val context = this.context ?: return

        val confirmation = if (item.hasPhoto()) {
            R.string.item_detail_delete_item_confirmation_with_photo
        } else {
            R.string.item_detail_delete_item_confirmation
        }

        ConfirmationDialog.show(context, R.string.item_detail_delete_item, confirmation, {
            tracker.event("itemDetailDeleteItemConfirm", mapOf("hasPhoto" to item.hasPhoto()))
            deleteItem(item)
            fragmentManager?.popBackStack()
        }, { tracker.event("itemDetailDeleteItemCancel", mapOf("hasPhoto" to item.hasPhoto())) })
    }

    fun deleteItem(item: Item) {
        itemDetailModel.deleteItem(item)
                .compose(bindToLifecycle<Any>())
                .subscribeBy(onError = { tracker.recordException(LevelE, LoggedException("Unable to delete item", it)) })
    }

    fun loadPhoto(item: Item) {
        if (loadedPhotoUri == item.photoUri) return
        val photoView = image_photo ?: return
        if (item.hasPhoto()) {
            loadedPhotoUri = item.photoUri
            imageLoader.loadPath(item.photoUri, photoView)
        } else {
            photoView.showImage()
            photoView.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            photoView.imageView.setImageResource(R.drawable.ic_add_photo_accent_128dp)
        }
    }

    fun selectPhotoForItem() {
        if (item == null) return

        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, REQUEST_SELECT_IMAGE)

    }


    inner class EditingTextWatcher : TextWatcher {

        var isEditing = false

        private val trackingSubject = PublishSubject.create<String>()

        fun observeTextChange(event: String, listener: (text: String) -> Unit) {
            trackingSubject
                    .toFlowable(BackpressureStrategy.DROP)
                    .toObservable()
                    .debounce(DEBOUNCE_NAME_CHANGE_MILLIS, TimeUnit.MILLISECONDS)
                    .compose(bindToLifecycle())
                    .subscribe({
                        tracker.event(event, mapOf("length" to it.length))
                    })
            trackingSubject.toFlowable(BackpressureStrategy.DROP)
                    .compose(bindToLifecycle())
                    .subscribe(listener)
        }

        override fun afterTextChanged(s: Editable?) {
            trackingSubject.onNext(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isEditing = true
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
}

