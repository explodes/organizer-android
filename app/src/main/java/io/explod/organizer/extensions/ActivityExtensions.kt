package io.explod.organizer.extensions

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import io.explod.organizer.features.home.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

fun MainActivity.showSnackbar(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_SHORT, @StringRes actionRes: Int = 0, action: ((View) -> Unit)? = null, view: View? = this.coordinator) {
    if (view != null) {
        val snackbar = Snackbar.make(coordinator, messageRes, length)
        if (actionRes != 0 && action != null) {
            snackbar.setAction(actionRes, action)
        }
        snackbar.show()
    }
}

fun MainActivity.showSnackbar(message: String, length: Int = Snackbar.LENGTH_SHORT, @StringRes actionRes: Int = 0, action: ((View) -> Unit)? = null, view: View? = this.coordinator) {
    if (view != null) {
        val snackbar = Snackbar.make(coordinator, message, length)
        if (actionRes != 0 && action != null) {
            snackbar.setAction(actionRes, action)
        }
        snackbar.show()
    }
}

fun MainActivity.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun MainActivity.toastLong(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun MainActivity.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}