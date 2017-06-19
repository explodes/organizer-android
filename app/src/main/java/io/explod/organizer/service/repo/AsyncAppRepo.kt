package io.explod.organizer.service.repo

import android.content.Context
import android.net.Uri
import com.fernandocejas.arrow.optional.Optional
import io.explod.organizer.service.database.Category
import io.explod.organizer.service.database.Item
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.reactivex.Completable
import io.reactivex.Flowable
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

    fun getAllCategoryStats(): Flowable<List<CategoryStats>> = synchronously.getAllCategoryStats()
    fun getCategoryStatsById(categoryId: Long): Flowable<Optional<CategoryStats>> = synchronously.getCategoryStatsById(categoryId)
    fun createCategory(name: String): Single<Category> = asSingle { synchronously.createCategory(name) }
    fun updateCategory(category: Category) = asCompletable { synchronously.updateCategory(category) }
    fun deleteCategory(category: Category) = asCompletable { synchronously.deleteCategory(category) }

    /* Items */

    fun getAllItemsForCategory(categoryId: Long): Flowable<List<Item>> = synchronously.getAllItemsForCategory(categoryId)
    fun getItemById(itemId: Long): Flowable<Optional<Item>> = synchronously.getItemById(itemId)
    fun createItem(categoryId: Long, name: String, rating: Int = -1, photo: Uri? = null): Single<Item> = asSingle { synchronously.createItem(categoryId = categoryId, name = name, rating = rating, photo = photo) }
    fun updateItem(item: Item) = asCompletable { synchronously.updateItem(item) }
    fun deleteItem(item: Item) = asCompletable { synchronously.deleteItem(item) }

    fun downloadPhotoForItem(context: Context, item: Item, source: Uri): Completable = asCompletable { synchronously.downloadPhotoForItem(context, item, source) }

}
