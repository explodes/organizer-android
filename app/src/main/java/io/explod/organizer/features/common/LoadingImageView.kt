package io.explod.organizer.features.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.explod.organizer.R
import io.explod.organizer.extensions.hide
import io.explod.organizer.extensions.show
import kotlinx.android.synthetic.main.view_loading_image.view.*

class LoadingImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr),
        Target {

    val imageView: ImageView
        get() = image

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loading_image, this)
        showLoading()
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        showLoading()
    }

    override fun onBitmapFailed(errorDrawable: Drawable?) {
        showError()
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        showImage()
        image.scaleType = ImageView.ScaleType.FIT_CENTER
        image.setImageBitmap(bitmap)
    }

    fun showLoading() {
        progress_loading.show()
        image.hide()
    }

    fun showImage() {
        progress_loading.hide()
        image.show()
    }

    fun showError() {
        showImage()
        image.scaleType = ImageView.ScaleType.CENTER_INSIDE
        image.setImageResource(R.drawable.ic_broken_image_accent_96dp)
    }


}