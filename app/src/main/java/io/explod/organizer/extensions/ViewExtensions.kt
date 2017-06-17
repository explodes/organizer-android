package io.explod.organizer.extensions

import android.view.View
import android.view.ViewGroup

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
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}