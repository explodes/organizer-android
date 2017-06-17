package io.explod.arch.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY created_date DESC")
    fun loadAll(): LiveData<List<Category>>

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

    @Query("SELECT * FROM items ORDER BY created_date DESC")
    fun loadAll(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE category_id = :arg0 ORDER BY created_date DESC")
    fun byCategory(categoryId: Long): LiveData<List<Item>>

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