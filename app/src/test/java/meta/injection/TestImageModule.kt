package io.explod.organizer.injection

import android.widget.ImageView
import dagger.Module
import dagger.Provides
import io.explod.organizer.R
import io.explod.organizer.service.images.ImageLoader
import javax.inject.Singleton


@Module
class TestImageModule {

    @Provides
    @Singleton
    internal fun providesImageLoader(): ImageLoader {
        return object : ImageLoader {
            override fun loadUri(uri: String, imageView: ImageView) {
                imageView.setImageResource(R.drawable.ic_broken_image_accent_24dp)
            }
        }
    }

}

