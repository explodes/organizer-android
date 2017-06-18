package io.explod.organizer.features.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.explod.arch.data.Item
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


data class CategoryItem(val stats: CategoryStats, val item: Item?)

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

    fun deleteCategory(categoryId: Long): Completable = repo.deleteCategory(categoryId)

    class Factory(val categoryId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
            @Suppress("UNCHECKED_CAST")
            return CategoryDetailViewModel(categoryId) as T
        }

    }

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

        @Synchronized
        fun updateValue() {
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
