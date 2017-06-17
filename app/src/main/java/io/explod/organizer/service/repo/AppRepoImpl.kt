package io.explod.organizer.service.repo

import android.arch.lifecycle.LiveData
import android.net.Uri
import io.explod.arch.data.AppDatabase
import io.explod.arch.data.Category
import io.explod.arch.data.Item
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AppRepoImpl : AppRepo {

    @Inject
    lateinit var db: AppDatabase

    val scheduler = Schedulers.io()

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    override fun getAllCategories(): LiveData<List<Category>> {
        return db.categories().loadAll()
    }

    override fun getAllCategoriesWithStats(): LiveData<List<CategoryStats>> {
        return db.categories().loadAllWithStats()
    }

    override fun getCategoryById(categoryId: Long): Category? {
        return db.categories().byId(categoryId)
    }

    override fun createCategory(name: String): Category {
        val category = Category.new(name)
        category.id = db.categories().insert(category)
        return category
    }

    override fun updateCategory(category: Category) {
        db.categories().update(category)
    }

    override fun deleteCategory(categoryId: Long) {
        db.categories().delete(categoryId)
    }

    override fun getAllItemsForCategory(categoryId: Long): LiveData<List<Item>> {
        return db.items().byCategory(categoryId)
    }

    override fun getItemById(itemId: Long): Item? {
        return db.items().byId(itemId)
    }

    override fun createItem(categoryId: Long, name: String, rating: Int, photo: Uri?): Item {
        val item = Item.new(categoryId, name, rating = rating, photo = photo)
        item.id = db.items().insert(item)
        return item
    }

    override fun updateItem(item: Item) {
        db.items().update(item)
    }

    override fun deleteItem(itemId: Long) {
        db.items().delete(itemId)
    }

}