package io.explod.organizer.features.home

import android.arch.lifecycle.ViewModel
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.repo.AsyncAppRepo
import javax.inject.Inject

class CategoryViewModel : ViewModel() {

    @Inject
    lateinit var repo: AsyncAppRepo

    val categories by lazy(LazyThreadSafetyMode.NONE) { repo.getAllCategories() }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }


}
