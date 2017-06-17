package io.explod.arch.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters


@Database(
        version = 1,
        entities = arrayOf(Category::class, Item::class)
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categories(): CategoryDao

    abstract fun items(): ItemDao

}
