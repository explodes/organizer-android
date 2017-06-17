package io.explod.organizer.extensions

import android.os.Bundle
import android.support.v4.app.Fragment
import io.explod.organizer.features.home.MainActivity

private val EMPTY_BUNDLE = Bundle()

val Fragment.mainActivity: MainActivity?
    get() {
        val activity = activity
        if (activity is MainActivity) {
            return activity
        }
        return null
    }

val Fragment.args: Bundle
    get() {
        val args = arguments
        if (args != null) return args
        return EMPTY_BUNDLE
    }

