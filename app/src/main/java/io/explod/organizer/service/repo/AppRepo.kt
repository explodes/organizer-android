package io.explod.organizer.service.repo

import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.Uri
import io.explod.arch.data.Category
import io.explod.arch.data.Item
import io.explod.organizer.service.database.CategoryStats
import java.io.IOException

/**
 * AppRepo is the interface between our Application and its underlying data.
 *
 * This allows coordination between multiple data source (such as the database and photos on disk)
 * to keep things in a consistent state.
 */
interface AppRepo {

    /* Categories */

    /**
     * Get all Categories, with stats, sorted by createdDate descending
     */
    fun getAllCategoryStats(): LiveData<List<CategoryStats>>

    /**
     * Look for a Category by id
     */
    fun getCategoryStatsById(categoryId: Long): LiveData<CategoryStats>

    /**
     * Create a new Category
     */
    fun createCategory(name: String): Category

    /**
     * Persist changes to a Category
     */
    fun updateCategory(category: Category)

    /**
     * Delete a Category
     */
    fun deleteCategory(category: Category)

    /* Items */

    /**
     * Get all Items for a category, sorted by createdDate descending
     */
    fun getAllItemsForCategory(categoryId: Long): LiveData<List<Item>>

    /**
     * Look for an Item by id
     */
    fun getItemById(itemId: Long): LiveData<Item>

    /**
     * Create a new Item
     */
    fun createItem(categoryId: Long, name: String, rating: Int = -1, photo: Uri? = null): Item

    /**
     * Persist changes to an Item
     */
    fun updateItem(item: Item)

    /**
     * Delete an Item
     */
    fun deleteItem(item: Item)

    /**
     * Download a photo and save the new Uri to the Item
     */
    @Throws(IOException::class)
    fun downloadPhotoForItem(context: Context, item: Item, source: Uri)

}

