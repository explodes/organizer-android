package io.explod.organizer.injection

import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import io.explod.arch.data.AppDatabase
import io.explod.organizer.App
import javax.inject.Singleton


@Module
class DatabaseModule {

    companion object {
        private val DB_NAME = "app.db"
    }

    @Provides
    @Singleton
    internal fun providesDatabase(app: App): AppDatabase = Room.databaseBuilder(app, AppDatabase::class.java, DB_NAME).build()

}

