package io.explod.organizer.extensions

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View


/**
 * Lookup for views in ViewHolders where Kotlin Android Extensions fails to cover ground for
 */
fun <T : View> RecyclerView.ViewHolder.find(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val t: T = itemView.findViewById(res)
        t
    }
}

/**
 * Hide a view
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Show a view
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Set view visibility
 */
fun View.toggleVisibility(show: Boolean) {
    if (show) {
        show()
    } else {
        hide()
    }
}