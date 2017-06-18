package io.explod.organizer.features.home

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.net.Uri
import io.explod.arch.data.Item
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Completable
import javax.inject.Inject


class ItemDetailViewModel(val itemId: Long) : ViewModel() {

    @Inject
    lateinit var repo: AsyncAppRepo

    val item by lazy(LazyThreadSafetyMode.NONE) { repo.getItemById(itemId) }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    fun saveItem(item: Item): Completable = repo.updateItem(item)

    fun deleteItem(itemId: Long): Completable = repo.deleteItem(itemId)

    fun downloadPhotoForItem(context: Context, item: Item, source: Uri) = repo.downloadPhotoForItem(context, item, source)

    class Factory(val itemId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
            @Suppress("UNCHECKED_CAST")
            return ItemDetailViewModel(itemId) as T
        }
    }

}