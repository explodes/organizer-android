package io.explod.organizer.service.images

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import io.explod.organizer.R


interface ImageLoader {
    fun loadUri(uri: String, imageView: ImageView)
}

class PicassoImageLoader(val picasso: Picasso) : ImageLoader {

    override fun loadUri(uri: String, imageView: ImageView) {
        val parsed = Uri.parse(uri)
        picasso.load(parsed)
                .defaults()
                .into(imageView)
    }

}

private fun RequestCreator.defaults(): RequestCreator {
    return this
            .placeholder(R.drawable.ic_cloud_download_accent_24dp)
            .error(R.drawable.ic_broken_image_accent_24dp)
            .fit()
            .centerInside()
}
