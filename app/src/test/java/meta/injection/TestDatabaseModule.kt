package meta.injection

import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import io.explod.organizer.App
import io.explod.organizer.service.database.AppDatabase
import javax.inject.Singleton


@Module
class TestDatabaseModule {

    @Provides
    @Singleton
    internal fun providesDatabase(app: App): AppDatabase {
        return Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java).build()
    }

}

