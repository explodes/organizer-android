package io.explod.organizer.features.home

import android.arch.lifecycle.ViewModel
import io.explod.arch.data.Category
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Single
import javax.inject.Inject

class CategoryListViewModel : ViewModel() {

    @Inject
    lateinit var repo: AsyncAppRepo

    val categories by lazy(LazyThreadSafetyMode.NONE) { repo.getAllCategories() }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    fun createCategory(name: String): Single<Category> = repo.createCategory(name)

}
