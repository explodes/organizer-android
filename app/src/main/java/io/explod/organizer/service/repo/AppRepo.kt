package io.explod.organizer.service.repo

import android.arch.lifecycle.LiveData
import android.net.Uri
import io.explod.arch.data.Category
import io.explod.arch.data.Item

interface AppRepo {

    /* Categories */

    /**
     * Get all Categories, sorted by createdDate descending
     */
    fun getAllCategories(): LiveData<List<Category>>

    /**
     * Look for a Category by id
     */
    fun getCategoryById(categoryId: Long): Category?

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
    fun deleteCategory(categoryId: Long)

    /* Items */

    /**
     * Get all Items for a category, sorted by createdDate descending
     */
    fun getAllItemsForCategory(categoryId: Long): LiveData<List<Item>>

    /**
     * Look for an Item by id
     */
    fun getItemById(itemId: Long): Item?

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
    fun deleteItem(itemId: Long)

}

