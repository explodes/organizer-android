package io.explod.organizer.injection

import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import io.explod.organizer.App
import io.explod.organizer.service.images.ImageLoader
import io.explod.organizer.service.images.PicassoImageLoader
import io.explod.organizer.service.tracking.LevelW
import io.explod.organizer.service.tracking.Tracker
import javax.inject.Singleton


@Module
class ImageModule {

    companion object {
        private const val PICASSO_LOG_TAG = "Picasso"
    }

    @Provides
    @Singleton
    internal fun providesImageLoader(app: App, tracker: Tracker): ImageLoader {
        val picasso = Picasso.Builder(app)
                .listener { _, uri, exception ->
                    val message = if (exception == null) "Unknown Error" else exception.message
                    val uriMessage = uri?.toString() ?: "no-uri-specified"
                    tracker.log(LevelW, PICASSO_LOG_TAG, message + ": " + uriMessage)
                }
                .build()
        return PicassoImageLoader(picasso)
    }

}

