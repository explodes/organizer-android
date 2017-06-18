package io.explod.arch.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.support.annotation.VisibleForTesting
import io.explod.organizer.service.database.CategoryStats

@Dao
interface CategoryDao {

    companion object {
        private const val QUERY_CATEGORY_STATS = "SELECT categories.*, " +
                "COUNT(items.id) AS num_items, " +
                "SUM(CASE WHEN items.rating IS NULL OR items.rating NOT BETWEEN 1 AND 5 THEN 0 ELSE 1 END) AS num_rated, " +
                "SUM(CASE WHEN items.rating IS NULL OR items.rating NOT BETWEEN 1 AND 5 THEN 0 ELSE items.rating END) AS total_rating  " +
                "FROM categories " +
                "LEFT OUTER JOIN items ON categories.id = items.category_id " +
                "GROUP BY categories.id, categories.name, categories.created_date " +
                "ORDER BY categories.created_date DESC"
        private const val QUERY_CATEGORY_BY_ID = "SELECT categories.*, " +
                "COUNT(items.id) AS num_items, " +
                "SUM(CASE WHEN items.rating IS NULL OR items.rating NOT BETWEEN 1 AND 5 THEN 0 ELSE 1 END) AS num_rated, " +
                "SUM(CASE WHEN items.rating IS NULL OR items.rating NOT BETWEEN 1 AND 5 THEN 0 ELSE items.rating END) AS total_rating  " +
                "FROM categories " +
                "LEFT OUTER JOIN items ON categories.id = items.category_id " +
                "WHERE categories.id = :arg0 " +
                "GROUP BY categories.id, categories.name, categories.created_date"
    }

    @Query(QUERY_CATEGORY_STATS)
    fun loadAllStats(): LiveData<List<CategoryStats>>

    @VisibleForTesting
    @Query(QUERY_CATEGORY_STATS)
    fun loadAllStatsDirect(): List<CategoryStats>

    @Query(QUERY_CATEGORY_BY_ID)
    fun statsById(id: Long): LiveData<CategoryStats>

    @VisibleForTesting
    @Query(QUERY_CATEGORY_BY_ID)
    fun statsByIdDirect(id: Long): CategoryStats

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(category: Category): Int

    @Delete
    fun delete(category: Category): Int

}

@Dao
interface ItemDao {

    companion object {
        private const val QUERY_BY_CATEGORY_DESCENDING_BY_DATE = "SELECT * FROM items WHERE category_id = :arg0 ORDER BY created_date DESC"
        private const val QUERY_BY_ID = "SELECT * FROM items WHERE id = :arg0"
    }

    @Query(QUERY_BY_CATEGORY_DESCENDING_BY_DATE)
    fun byCategory(categoryId: Long): LiveData<List<Item>>

    @Query(QUERY_BY_CATEGORY_DESCENDING_BY_DATE)
    fun byCategoryDirect(categoryId: Long): List<Item>

    @VisibleForTesting
    @Query(QUERY_BY_CATEGORY_DESCENDING_BY_DATE)
    fun byCategoryAsList(categoryId: Long): List<Item>

    @Query(QUERY_BY_ID)
    fun byId(id: Long): LiveData<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: Item): Int

    @Delete
    fun delete(item: Item): Int

}