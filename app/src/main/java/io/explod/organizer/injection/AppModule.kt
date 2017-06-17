package io.explod.organizer.injection

import dagger.Module
import dagger.Provides
import io.explod.organizer.App
import javax.inject.Singleton


@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    internal fun providesApp(): App = app

}

