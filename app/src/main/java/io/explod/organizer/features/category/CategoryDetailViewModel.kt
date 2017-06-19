package io.explod.organizer.features.category

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.explod.organizer.extensions.observeOnMain
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.Category
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.database.Item
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction


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

    @javax.inject.Inject
    lateinit var repo: AsyncAppRepo

    val categoryItems: Flowable<List<CategoryItem>> by lazy(LazyThreadSafetyMode.NONE) { CategoryItemMediator(categoryId).toFlowable().observeOnMain() }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    fun createItem(categoryId: Long, name: String): Single<Item> = repo.createItem(categoryId, name).observeOnMain()

    fun deleteCategory(category: Category): Completable = repo.deleteCategory(category).observeOnMain()

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
     * CategoryItemMediator zips a Category and its Items into a list of CategoryItems with the
     * first entry being a Category-only CategoryItem.
     */
    inner class CategoryItemMediator(val categoryId: Long) {

        /**
         * Zip our results into a list of CategoryItems with the first entry being a Category-only
         * CategoryItem.
         * If we are missing either a Category or Items, nothing is zipped and this LiveData's value
         * is null.
         */
        internal fun toFlowable(): Flowable<List<CategoryItem>> {
            return Flowable.combineLatest(
                    repo.getCategoryStatsById(categoryId)
                            .filter { it.isPresent }
                            .map { it.get() },
                    repo.getAllItemsForCategory(categoryId),
                    BiFunction<CategoryStats, List<Item>, List<CategoryItem>> { stats, items -> zip(stats, items) }
            )
        }

        private fun zip(stats: CategoryStats, items: List<Item>): List<CategoryItem> {
            val zipped = ArrayList<CategoryItem>(items.size + 1)
            zipped.add(CategoryItem(stats, null))
            items.forEach {
                zipped.add(CategoryItem(stats, it))
            }
            return zipped
        }
    }
}
