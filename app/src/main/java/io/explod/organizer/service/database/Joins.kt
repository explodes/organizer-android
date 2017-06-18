package io.explod.organizer.service.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import io.explod.arch.data.Category

class CategoryStats {

    @Embedded
    lateinit var category: Category

    @ColumnInfo(name = "num_items")
    var numItems: Int = 0

    @ColumnInfo(name = "num_rated")
    var numRated: Int = 0

    @ColumnInfo(name = "total_rating")
    var totalRating: Int = 0

    val averageRating: Float
        get() = when (numRated) {
            0 -> 0f
            else -> totalRating.toFloat() / numRated.toFloat()
        }

    override fun toString(): String {
        return "CategoryStats(category=$category, numItems=$numItems, numRated=$numRated, totalRating=$totalRating)"
    }
}
