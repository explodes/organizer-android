package io.explod.organizer.features.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.explod.arch.data.Category
import io.explod.arch.data.Item
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


/**
 * CategoryItem is a zipped combination of CategoryStats to an optional Item
 */
data class CategoryItem(val stats: CategoryStats, val item: Item?)

/**
 * CategoryDetailViewModel is responsible for loading a Category and all of items.
 *
 * It also provides the means to create a new Item and to delete a Category.
 */
class CategoryDetailViewModel(val categoryId: Long) : ViewModel() {

    @Inject
    lateinit var repo: AsyncAppRepo

    val categoryItems: LiveData<List<CategoryItem>> by lazy(LazyThreadSafetyMode.NONE) { CategoryItemMediator(categoryId) }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    fun createItem(categoryId: Long, name: String): Single<Item> = repo.createItem(categoryId, name)

    fun deleteCategory(category: Category): Completable = repo.deleteCategory(category)

    /**
     * Factory used to create a new CategoryDetailViewModel for a Category with a given ID
     */
    class Factory(val categoryId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
            @Suppress("UNCHECKED_CAST")
            return CategoryDetailViewModel(categoryId) as T
        }

    }

    /**
     * CategoryItemMediator is a mediator between CategoryStat and Item LiveData.
     * Results are zipped into a list of CategoryItems with the first entry being a Category-only
     * CategoryItem.
     */
    inner class CategoryItemMediator(val categoryId: Long) : MediatorLiveData<List<CategoryItem>>() {

        var stats: CategoryStats? = null
        var items: List<Item>? = null

        init {
            addSource(repo.getCategoryById(categoryId)) {
                this.stats = it
                updateValue()
            }
            addSource(repo.getAllItemsForCategory(categoryId)) {
                this.items = it
                updateValue()
            }
        }

        /**
         * Zip our results into a list of CategoryItems with the first entry being a Category-only
         * CategoryItem.
         * If we are missing either a Category or Items, nothing is zipped and this LiveData's value
         * is null.
         */
        @Synchronized
        private fun updateValue() {
            val items: List<Item>? = this.items
            val stats: CategoryStats? = this.stats

            if (items == null || stats == null) {
                postValue(null)
            } else {
                val zipped = ArrayList<CategoryItem>(items.size + 1)
                zipped.add(CategoryItem(stats, null))
                items.forEach {
                    zipped.add(CategoryItem(stats, it))
                }
                postValue(zipped)
            }
        }
    }
}
