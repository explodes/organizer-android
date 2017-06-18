package io.explod.organizer.extensions

import android.content.Context
import android.content.res.Resources
import android.support.annotation.IdRes


/**
 * Get the name of an ID
 */
fun Context?.getResourceNameOrUnknown(@IdRes res: Int): String {
    if (this == null) {
        return unknownString(res)
    }
    return resources.getResourceNameOrUnknown(res)
}

/**
 * Get the name of an ID
 */
fun Resources?.getResourceNameOrUnknown(@IdRes res: Int): String {
    if (this == null) {
        return unknownString(res)
    }
    try {
        return getResourceName(res)
    } catch (ex: Resources.NotFoundException) {
        return unknownString(res)
    }
}

/**
 * Creates a string describing an unidentifiable resource
 */
private fun unknownString(@IdRes res: Int): String {
    return "unknown-0x${Integer.toHexString(res)}"
}
