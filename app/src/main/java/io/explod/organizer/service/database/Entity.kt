package io.explod.arch.data

import android.arch.persistence.room.*
import android.net.Uri
import android.support.annotation.IntRange
import java.util.*

@Entity(tableName = "categories")
class Category {

    companion object {
        fun new(name: String): Category {
            val category = Category()
            category.name = name
            return category
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id = 0L

    var name = ""

    @ColumnInfo(name = "created_date")
    var createdDate = Date()

}

@Entity(tableName = "items",
        foreignKeys = arrayOf(
                ForeignKey(entity = Category::class, parentColumns = arrayOf("id"), childColumns = arrayOf("category_id"))
        ),
        indices = arrayOf(Index("category_id"))
)
class Item {

    companion object {
        fun new(categoryId: Long, name: String, @IntRange(from = -1, to = 5) rating: Int = -1, photo: Uri? = null): Item {
            val item = Item()
            item.categoryId = categoryId
            item.name = name
            item.rating = rating
            item.photoUri = photo?.toString() ?: ""
            return item
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id = 0L

    @ColumnInfo(name = "category_id")
    var categoryId = 0L

    var name = ""

    var rating = -1
        @IntRange(from = -1, to = 5) set


    @ColumnInfo(name = "photo_uri")
    var photoUri = ""

    @ColumnInfo(name = "created_date")
    var createdDate = Date()

}