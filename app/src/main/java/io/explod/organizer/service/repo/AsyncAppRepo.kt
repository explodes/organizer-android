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
 * Rx wrapper around [AppRepo]'s otherwise synchronous functions.
 *
 * Uses the IO scheduler
 */
@SchedulerSupport(SchedulerSupport.IO)
class AsyncAppRepo {

    @Inject
    lateinit var synchronous: AppRepo

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    private fun <T> asSingle(cb: () -> T): Single<T> = Single.fromCallable(cb).subscribeOn(Schedulers.io())
    private fun asCompletable(cb: () -> Unit): Completable = Completable.fromAction(cb).subscribeOn(Schedulers.io())

    /* Categories */

    fun getAllCategories(): LiveData<List<Category>> = synchronous.getAllCategories()
    fun getCategoryById(categoryId: Long): Single<Optional<Category>> = asSingle { Optional.fromNullable(synchronous.getCategoryById(categoryId)) }
    fun createCategory(name: String): Single<Category> = asSingle { synchronous.createCategory(name) }
    fun updateCategory(category: Category) = asCompletable { synchronous.updateCategory(category) }
    fun deleteCategory(categoryId: Long) = asCompletable { synchronous.deleteCategory(categoryId) }

    /* Items */

    fun getAllItemsForCategory(categoryId: Long): LiveData<List<Item>> = synchronous.getAllItemsForCategory(categoryId)
    fun getItemById(itemId: Long): Single<Optional<Item>> = asSingle { Optional.fromNullable(synchronous.getItemById(itemId)) }
    fun createItem(categoryId: Long, name: String, rating: Int = -1, photo: Uri? = null): Single<Item> = asSingle { synchronous.createItem(categoryId = categoryId, name = name, rating = rating, photo = photo) }
    fun updateItem(item: Item) = asCompletable { synchronous.updateItem(item) }
    fun deleteItem(itemId: Long) = asCompletable { synchronous.deleteItem(itemId) }

}
