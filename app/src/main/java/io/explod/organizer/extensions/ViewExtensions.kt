package io.explod.organizer.extensions

import android.app.Activity
import android.app.Dialog
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

fun <T : View> Activity.find(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val t: T = findViewById(res)
        t
    }
}

fun <T : View> Fragment.find(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val t: T? = view?.findViewById(res)
        t ?: throw NullPointerException("Fragment view not initialized for " + this)
    }
}

fun <T : View> View.find(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val t: T = findViewById(res)
        t
    }
}

fun <T : View> RecyclerView.ViewHolder.find(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val t: T = itemView.findViewById(res)
        t
    }
}

fun <T : View> Dialog.find(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val t: T = findViewById(res)
        t
    }
}

inline val ViewGroup.views: Sequence<View>
    get() = (0..childCount - 1).asSequence().map { getChildAt(it) }

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.toggleVisibility(show: Boolean) {
    if (show) {
        show()
    } else {
        hide()
    }
}