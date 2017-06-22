package meta.injection

import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import io.explod.arch.data.AppDatabase
import io.explod.organizer.App
import javax.inject.Singleton


@Module
class UiTestDatabaseModule {

    @Provides
    @Singleton
    internal fun providesDatabase(app: App): AppDatabase {
        return Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java).build()
    }

}

