package io.explod.organizer.service.repo

import android.arch.lifecycle.LiveData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.explod.arch.data.AppDatabase
import io.explod.arch.data.Category
import io.explod.arch.data.Item
import io.explod.arch.data.hasPhoto
import io.explod.organizer.extensions.closeCleanly
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.tracking.LevelW
import io.explod.organizer.service.tracking.Tracker
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * AppRepoImpl is an implementation of AppRepo that performs coordination between the internet,
 * photos on disk, and the AppDatabase
 */
class AppRepoImpl : AppRepo {

    companion object {
        private val TAG = AppRepoImpl::class.java.simpleName
        private const val PHOTO_LOCATION = "photos"
        private const val SCHEME_FILE = "file://"
        private const val RESERVED_CHARS = "|\\?*<\":>+[]/'"
    }

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var tracker: Tracker

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    override fun getAllCategories(): LiveData<List<CategoryStats>> {
        return db.categories().loadAllWithStats()
    }

    override fun getCategoryById(categoryId: Long): LiveData<CategoryStats> {
        return db.categories().byId(categoryId)
    }

    override fun createCategory(name: String): Category {
        val category = Category.new(name)
        category.id = db.categories().insert(category)
        return category
    }

    override fun updateCategory(category: Category) {
        db.categories().update(category)
    }

    override fun deleteCategory(category: Category) {
        db.items().byCategoryDirect(category.id).forEach {
            it.deletePhotoIfExists()
        }
        db.categories().delete(category)
    }

    override fun getAllItemsForCategory(categoryId: Long): LiveData<List<Item>> {
        return db.items().byCategory(categoryId)
    }

    override fun getItemById(itemId: Long): LiveData<Item> {
        return db.items().byId(itemId)
    }

    override fun createItem(categoryId: Long, name: String, rating: Int, photo: Uri?): Item {
        val item = Item.new(categoryId, name, rating = rating, photo = photo)
        item.id = db.items().insert(item)
        return item
    }

    override fun updateItem(item: Item) {
        db.items().update(item)
    }

    override fun deleteItem(item: Item) {
        item.deletePhotoIfExists()
        db.items().delete(item)
    }

    @Throws(IOException::class)
    override fun downloadPhotoForItem(context: Context, item: Item, source: Uri) {
        val fileName = "${item.id}-${item.name}-${source.lastPathSegment}".filter { it !in RESERVED_CHARS }
        val tempFile = File(context.filesDir.absolutePath + File.pathSeparator + PHOTO_LOCATION + File.pathSeparator + fileName)
        val webpFile = File("${tempFile.absolutePath}.webp")
        try {
            copyUriToFile(context, source, tempFile)
            convertToWebp(tempFile, webpFile)
        } finally {
            tempFile.delete()
        }
        item.deletePhotoIfExists()
        item.photoUri = "$SCHEME_FILE${webpFile.absolutePath}"
        updateItem(item)
    }

    /**
     * Helper function to delete an Item's photo if it is a "file://" type that exists
     */
    private fun Item.deletePhotoIfExists() {
        if (hasPhoto() && photoUri.startsWith(SCHEME_FILE)) {
            val file = File(photoUri.substring(SCHEME_FILE.length))
            if (file.delete()) {
                photoUri = ""
            } else {
                tracker.log(LevelW, TAG, "Unable to delete old photo: ${file.absolutePath}")
            }
        }
    }

}

@Throws(IOException::class)
private fun copyUriToFile(context: Context, input: Uri, output: File) {
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(input)
        outputStream = output.outputStream()
        copyStream(inputStream, outputStream)
    } finally {
        inputStream.closeCleanly()
        outputStream.closeCleanly()
    }
}

@Throws(IOException::class)
private fun copyStream(input: InputStream, output: OutputStream) {
    val buf = ByteArray(32768) // 32k
    var bytesRead: Int
    while (true) {
        bytesRead = input.read(buf)
        if (bytesRead == -1) break
        output.write(buf, 0, bytesRead)
    }
    output.flush()
}

@Throws(IOException::class)
private fun convertToWebp(input: File, output: File) {
    val bm = BitmapFactory.decodeFile(input.absolutePath)
    var outputStream: OutputStream? = null
    try {
        outputStream = output.outputStream()
        bm.compress(Bitmap.CompressFormat.WEBP, 90, outputStream)
        outputStream.flush()
    } finally {
        outputStream.closeCleanly()
    }
}