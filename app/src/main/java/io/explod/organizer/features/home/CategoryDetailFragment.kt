package io.explod.organizer.features.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import io.explod.arch.data.Category
import io.explod.arch.data.Item
import io.explod.organizer.R
import io.explod.organizer.extensions.*
import io.explod.organizer.features.common.*
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.tracking.LevelE
import io.explod.organizer.service.tracking.LoggedException
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_category_detail.*
import javax.inject.Inject

class CategoryDetailFragment : BaseFragment(), CategoryItemAdapter.Listener {

    companion object {

        fun new(categoryId: Long): CategoryDetailFragment {
            val frag = CategoryDetailFragment()
            val args = Bundle()
            args.putLong(ARG_CATEGORY_ID, categoryId)
            frag.arguments = args
            return frag
        }

        private const val ARG_CATEGORY_ID = "categoryId"
    }

    @Inject
    lateinit var tracker: Tracker

    val categoryDetailModel by getModelWithFactory(CategoryDetailViewModel::class, { CategoryDetailViewModel.Factory(args.getLong(ARG_CATEGORY_ID)) })

    val categoryItemsAdapter by lazy(LazyThreadSafetyMode.NONE) { CategoryItemAdapter() }

    var stats: CategoryStats? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_category_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_create_item.setOnClickListener {
            tracker.event("categoryDetailFabCreateItemClick", mapOf("numItems" to categoryItemsAdapter.itemCount - 1))
            showCreateItemDialog()
        }

        categoryItemsAdapter.listener = this

        recycler_items.layoutManager = LinearLayoutManager(context)
        recycler_items.adapter = categoryItemsAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoryDetailModel.categoryItems.observe(this, Observer<List<CategoryItem>> {
            onCategoryItems(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mainActivity?.menuInflater
        inflater.inflate(R.menu.category_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete_category -> {
                tracker.event("categoryDetailMenuDeleteCategory")
                onDeleteCategoryClick()
                return true
            }
        }
        return false
    }

    override fun onItemClick(item: Item) {
        tracker.event("categoryDetailItemClick", mapOf("name" to item.name))
        mainActivity?.pushFragment(ItemDetailFragment.new(item.id))
    }

    fun onCategoryItems(items: List<CategoryItem>?) {
        if (recycler_items == null) return
        categoryItemsAdapter.replaceItems(items)
        if (items != null && items.isNotEmpty()) {
            stats = items[0].stats
        }
    }

    fun onDeleteCategoryClick() {
        val stats = this.stats ?: return
        val context = this.context ?: return
        ConfirmationDialog.show(context, R.string.category_detail_delete_category, R.string.category_detail_delete_category_confirmation, {
            tracker.event("categoryDetailDeleteCategoryConfirm")
            deleteCategory(stats.category)
            fragmentManager?.popBackStack()
        }, { tracker.event("categoryDetailDeleteCategoryCancel") })
    }

    fun deleteCategory(category: Category) {
        categoryDetailModel.deleteCategory(category)
                .compose(bindToLifecycle<Any>())
                .subscribeBy(onError = { tracker.recordException(LevelE, LoggedException("Unable to delete category", it)) })
    }

    fun showCreateItemDialog() {
        val context = this.context ?: return
        val stats = this.stats ?: return
        EditTextDialog(context)
                .setTitle(R.string.category_detail_create_item)
                .setOnTextChangedListener(object : EditTextDialog.OnTextChangedListener {
                    override fun onTextChanged(newText: String) {
                        if (!TextUtils.isEmpty(newText)) {
                            categoryDetailModel.createItem(stats.category.id, newText)
                                    .compose(bindToLifecycle())
                                    .subscribeBy(onError = { tracker.recordException(LevelE, LoggedException("Unable to create item", it)) })
                        } else {
                            mainActivity?.showSnackbar(R.string.category_detail_item_name_empty, length = Snackbar.LENGTH_LONG, actionRes = R.string.category_detail_item_empty_retry_action, action = {
                                showCreateItemDialog()
                            })
                        }
                    }
                })
                .show()
    }

}

class CategoryItemAdapter : ListAdapter<CategoryItem, CategoryItemAdapter.CategoryItemViewHolder>() {

    interface Listener {
        fun onItemClick(item: Item)
    }

    companion object {
        private const val TYPE_INVALID = -1
        private const val TYPE_CATEGORY = 0
        private const val TYPE_ITEM = 1

        private const val ID_MASK_CATEGORY = 0x800000000000000L
        private const val ID_MASK_ITEM = 0x4000000000000000L
    }

    var listener: Listener? = null

    init {
        setHasStableIds(true)
    }

    override fun createDiffCallback(old: List<CategoryItem>?, new: List<CategoryItem>?): DiffUtil.Callback {
        return CategoryItemDiffCallback(old, new)
    }

    override fun getItemId(position: Int): Long {
        val ci = this[position] ?: return 0L
        return when (getItemViewType(position)) {
            TYPE_CATEGORY -> ci.stats.category.id.or(ID_MASK_CATEGORY)
            TYPE_ITEM -> (ci.item?.id ?: 0L).or(ID_MASK_ITEM)
            else -> 0L
        }
    }

    override fun getItemViewType(position: Int): Int {
        val ci = this[position] ?: return TYPE_INVALID
        if (ci.item != null) return TYPE_ITEM
        return TYPE_CATEGORY
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CategoryItemViewHolder? {
        return when (viewType) {
            TYPE_CATEGORY -> {
                val context = parent?.context ?: return null
                val view = LayoutInflater.from(context).inflate(R.layout.card_category_detail_category, parent, false)
                return CategoryViewHolder(view)
            }
            TYPE_ITEM -> {
                val context = parent?.context ?: return null
                val view = LayoutInflater.from(context).inflate(R.layout.card_category_detail_item, parent, false)
                return ItemViewHolder(view)
            }
            else -> null
        }
    }

    override fun onBindViewHolder(unknownHolder: CategoryItemViewHolder?, position: Int) {
        if (unknownHolder == null) return
        val ci = this[position] ?: return

        when (getItemViewType(position)) {
            TYPE_CATEGORY -> {
                val stats = ci.stats
                val holder = unknownHolder as CategoryViewHolder
                onBindCategoryViewHolder(stats, holder)
            }
            TYPE_ITEM -> {
                val item = ci.item ?: return
                val holder = unknownHolder as ItemViewHolder
                onBindItemViewHolder(item, holder)
            }
        }
    }

    fun onBindCategoryViewHolder(stats: CategoryStats, holder: CategoryItemAdapter.CategoryViewHolder) {
        val res = holder.itemView.context?.resources

        holder.name.text = stats.category.name

        holder.itemCount.text = when {
            res == null || stats.numItems == 0 -> ""
            else -> res.getQuantityString(R.plurals.category_list_num_items, stats.numItems, stats.numItems)
        }
    }


    fun onBindItemViewHolder(item: Item, holder: CategoryItemAdapter.ItemViewHolder) {
        val res = holder.itemView.context?.resources

        holder.name.text = item.name

        holder.rating.text = when {
            res == null || item.rating == -1 -> ""
            else -> res.getQuantityString(R.plurals.category_detail_rating, item.rating, item.rating)
        }
    }

    fun notifyClick(position: Int) {
        val listener = this.listener ?: return
        val ci = this[position] ?: return
        val item = ci.item ?: return
        listener.onItemClick(item)
    }

    abstract inner class CategoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CategoryViewHolder(view: View) : CategoryItemViewHolder(view) {

        val name by find<TextView>(R.id.text_name)
        val itemCount by find<TextView>(R.id.text_item_count)

    }

    inner class ItemViewHolder(view: View) : CategoryItemViewHolder(view), View.OnClickListener {

        val name by find<TextView>(R.id.text_name)
        val rating by find<TextView>(R.id.text_rating)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            notifyClick(adapterPosition)
        }
    }
}

class CategoryItemDiffCallback(old: List<CategoryItem>?, new: List<CategoryItem>?) : ListDiffCallback<CategoryItem>(old, new) {

    override fun areItemsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
        return old.stats.category.id == new.stats.category.id &&
                ((old.item == null && new.item == null) || (old.item != null && new.item != null && old.item.id == new.item.id))
    }

    override fun areContentsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
        return categoriesHaveSameContent(old.stats, new.stats) &&
                itemsHaveSameContent(old.item, new.item)
    }

    fun categoriesHaveSameContent(old: CategoryStats, new: CategoryStats): Boolean {
        return old.category.createdDate == new.category.createdDate &&
                old.category.name == new.category.name &&
                old.numItems == new.numItems &&
                old.averageRating == new.averageRating
    }

    fun itemsHaveSameContent(old: Item?, new: Item?): Boolean {
        if (old == null && new == null) return true
        if (old == null || new == null) return false
        return old.categoryId == new.categoryId &&
                old.createdDate == new.createdDate &&
                old.name == new.name &&
                old.photoUri == new.photoUri &&
                old.rating == new.rating
    }

}