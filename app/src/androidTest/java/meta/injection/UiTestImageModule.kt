package meta.injection

import dagger.Module
import dagger.Provides
import io.explod.organizer.features.common.LoadingImageView
import io.explod.organizer.service.images.ImageLoader
import javax.inject.Singleton


@Module
class UiTestImageModule {

    @Provides
    @Singleton
    internal fun providesImageLoader(): ImageLoader {
        return object : ImageLoader {
            override fun loadPath(uri: String, view: LoadingImageView) {
                view.showError()
            }
        }
    }

}

