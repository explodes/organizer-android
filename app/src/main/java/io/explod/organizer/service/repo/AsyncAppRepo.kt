package io.explod.organizer.service.repo

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.fernandocejas.arrow.optional.Optional
import io.explod.arch.data.Category
import io.explod.arch.data.Item
import io.explod.organizer.injection.ObjectGraph.injector
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Rx wrapper around [AppRepo]'s otherwise synchronously functions.
 *
 * Uses the IO scheduler
 */
@SchedulerSupport(SchedulerSupport.IO)
class AsyncAppRepo {

    @Inject
    lateinit var synchronously: AppRepo

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    private fun <T> asSingle(cb: () -> T): Single<T> = Single.fromCallable(cb).subscribeOn(Schedulers.io())
    private fun asCompletable(cb: () -> Unit): Completable = Completable.fromAction(cb).subscribeOn(Schedulers.io())

    /* Categories */

    fun getAllCategories(): LiveData<List<Category>> = synchronously.getAllCategories()
    fun getCategoryById(categoryId: Long): Single<Optional<Category>> = asSingle { Optional.fromNullable(synchronously.getCategoryById(categoryId)) }
    fun createCategory(name: String): Single<Category> = asSingle { synchronously.createCategory(name) }
    fun updateCategory(category: Category) = asCompletable { synchronously.updateCategory(category) }
    fun deleteCategory(categoryId: Long) = asCompletable { synchronously.deleteCategory(categoryId) }

    /* Items */

    fun getAllItemsForCategory(categoryId: Long): LiveData<List<Item>> = synchronously.getAllItemsForCategory(categoryId)
    fun getItemById(itemId: Long): Single<Optional<Item>> = asSingle { Optional.fromNullable(synchronously.getItemById(itemId)) }
    fun createItem(categoryId: Long, name: String, rating: Int = -1, photo: Uri? = null): Single<Item> = asSingle { synchronously.createItem(categoryId = categoryId, name = name, rating = rating, photo = photo) }
    fun updateItem(item: Item) = asCompletable { synchronously.updateItem(item) }
    fun deleteItem(itemId: Long) = asCompletable { synchronously.deleteItem(itemId) }

}
