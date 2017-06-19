package io.explod.organizer.features.item

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.net.Uri
import io.explod.organizer.extensions.observeOnMain
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.Item
import io.explod.organizer.service.repo.AsyncAppRepo
import io.reactivex.Completable

/**
 * ItemDetailViewModel is responsible for loading an Item.
 *
 * It also provides the means to save, delete, and download photos.
 */
class ItemDetailViewModel(val itemId: Long) : ViewModel() {

    @javax.inject.Inject
    lateinit var repo: AsyncAppRepo

    val item by lazy(LazyThreadSafetyMode.NONE) { repo.getItemById(itemId).observeOnMain() }

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    fun saveItem(item: Item): Completable = repo.updateItem(item).observeOnMain()

    fun deleteItem(item: Item): Completable = repo.deleteItem(item).observeOnMain()

    fun downloadPhotoForItem(context: Context, item: Item, source: Uri) = repo.downloadPhotoForItem(context, item, source).observeOnMain()

    /**
     * Factory used to create a new ItemDetailViewModel for an Item with a given ID
     */
    class Factory(val itemId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
            @Suppress("UNCHECKED_CAST")
            return ItemDetailViewModel(itemId) as T
        }
    }

}