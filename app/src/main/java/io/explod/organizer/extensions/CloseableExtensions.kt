package io.explod.organizer.extensions

import android.util.Log
import java.io.Closeable
import java.io.IOException

private val CLOSEABLE_TAG = Closeable::class.java.simpleName

/**
 * Close this Closeable, and log, but do not raise, any errors.
 * Should only be used after all data is known to be synced.
 */
fun Closeable?.closeCleanly() {
    try {
        this?.close()
    } catch(ex: IOException) {
        Log.w(CLOSEABLE_TAG, "error closing", ex)
    }
}
