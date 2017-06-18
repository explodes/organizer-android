package io.explod.organizer.service.images

import com.squareup.picasso.Picasso
import io.explod.organizer.features.common.LoadingImageView


/**
 * ImageLoader is responsible for loading images into ImageViews
 */
interface ImageLoader {
    fun loadPath(uri: String, view: LoadingImageView)
}

/**
 * PicassoImageLoader is an ImageLoader implementation that uses picasso to load images,
 * potentially from the internet, into a ImageViews
 */
class PicassoImageLoader(val picasso: Picasso) : ImageLoader {

    override fun loadPath(uri: String, view: LoadingImageView) {
        picasso.load(uri).into(view)
    }

}

