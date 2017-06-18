package io.explod.organizer.service.images

import com.squareup.picasso.Picasso
import io.explod.organizer.features.common.LoadingImageView


interface ImageLoader {
    fun loadPath(path: String, view: LoadingImageView)
}

class PicassoImageLoader(val picasso: Picasso) : ImageLoader {

    override fun loadPath(path: String, view: LoadingImageView) {
        picasso.load(path).into(view)
    }

}

