package io.explod.organizer.features.category

import android.arch.lifecycle.ViewModel
import io.explod.organizer.extensions.observeOnMain
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.Category
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Single

/**
 * CategoryListViewModel is responsible for loading a Category.
 *
 * It also provides the means to create a new Category.
 */
class CategoryListViewModel : ViewModel() {

    @javax.inject.Inject
    lateinit var repo: AsyncAppRepo

    val categories by lazy(LazyThreadSafetyMode.NONE) { repo.getAllCategoryStats().observeOnMain() }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    fun createCategory(name: String): Single<Category> = repo.createCategory(name).observeOnMain()

}
