package io.explod.organizer.injection

import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import io.explod.organizer.App
import io.explod.organizer.service.database.AppDatabase
import javax.inject.Singleton


@Module
class DatabaseModule {

    companion object {
        private const val DB_NAME = "app.db"
    }

    @Provides
    @Singleton
    internal fun providesDatabase(app: App): AppDatabase = Room.databaseBuilder(app, AppDatabase::class.java, DB_NAME).build()

}

