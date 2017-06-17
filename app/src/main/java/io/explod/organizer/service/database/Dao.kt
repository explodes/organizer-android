package io.explod.arch.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.support.annotation.VisibleForTesting
import io.explod.organizer.service.database.CategoryStats

@Dao
interface CategoryDao {

    companion object {
        private const val QUERY_LOAD_ALL_DESCENDING_BY_DATE = "SELECT categories.* FROM categories ORDER BY created_date DESC"
        private const val QUERY_CATEGORY_STATS = "SELECT categories.*, " +
                "COUNT(items.id) AS num_items, " +
                "SUM(CASE WHEN items.rating IS NULL OR items.rating NOT BETWEEN 1 AND 5 THEN 0 ELSE 1 END) AS num_rated, " +
                "SUM(CASE WHEN items.rating IS NULL OR items.rating NOT BETWEEN 1 AND 5 THEN 0 ELSE items.rating END) AS total_rating  " +
                "FROM categories " +
                "LEFT OUTER JOIN items ON categories.id = items.category_id " +
                "GROUP BY categories.id, categories.name, categories.created_date " +
                "ORDER BY categories.created_date DESC"
    }

    @Query(QUERY_LOAD_ALL_DESCENDING_BY_DATE)
    fun loadAll(): LiveData<List<Category>>

    @VisibleForTesting
    @Query(QUERY_LOAD_ALL_DESCENDING_BY_DATE)
    fun loadAllAsList(): List<Category>

    @Query(QUERY_CATEGORY_STATS)
    fun loadAllWithStats(): LiveData<List<CategoryStats>>

    @VisibleForTesting
    @Query(QUERY_CATEGORY_STATS)
    fun loadAllWithStatsAsList(): List<CategoryStats>

    @Query("SELECT * FROM categories WHERE id = :arg0")
    fun byId(id: Long): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg categories: Category)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(category: Category): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg categories: Category): Int

    @Query("DELETE FROM categories WHERE id = :arg0")
    fun delete(categoryId: Long): Int

    @Delete
    fun delete(category: Category): Int

    @Delete
    fun delete(vararg categories: Category): Int

}

@Dao
interface ItemDao {

    companion object {
        private const val QUERY_BY_CATEGORY_DESCENDING_BY_DATE = "SELECT * FROM items WHERE category_id = :arg0 ORDER BY created_date DESC"
    }

    @Query(QUERY_BY_CATEGORY_DESCENDING_BY_DATE)
    fun byCategory(categoryId: Long): LiveData<List<Item>>

    @VisibleForTesting
    @Query(QUERY_BY_CATEGORY_DESCENDING_BY_DATE)
    fun byCategoryAsList(categoryId: Long): List<Item>

    @Query("SELECT * FROM items WHERE id = :arg0")
    fun byId(id: Long): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg items: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: Item): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg items: Item): Int

    @Query("DELETE FROM items WHERE id = :arg0")
    fun delete(itemId: Long): Int

    @Delete
    fun delete(item: Item): Int

    @Delete
    fun delete(vararg items: Item): Int

}