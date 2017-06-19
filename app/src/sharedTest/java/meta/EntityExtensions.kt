package meta

import android.net.Uri
import android.support.annotation.IntRange
import io.explod.organizer.service.database.Category
import io.explod.organizer.service.database.Item
import java.util.*

private var testDate: Long = 0

/**
 * Create a new uniquely-dated Item
 */
fun Item.Companion.newWithTestDate(categoryId: Long, name: String, @IntRange(from = -1, to = 5) rating: Int = -1, photo: Uri? = null): Item {
    val item = Item.new(categoryId, name, rating = rating, photo = photo)
    item.createdDate = Date(++testDate)
    return item
}

/**
 * Create a new uniquely-dated Category
 */
fun Category.Companion.newWithTestDate(name: String): Category {
    val cat = Category.new(name)
    cat.createdDate = Date(++testDate)
    return cat
}