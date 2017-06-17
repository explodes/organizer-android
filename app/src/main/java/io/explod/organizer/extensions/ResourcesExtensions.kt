package io.explod.organizer.extensions

import android.content.Context
import android.content.res.Resources
import android.support.annotation.IdRes

private fun unknownString(@IdRes res: Int): String {
    return "unknown-0x${Integer.toHexString(res)}"
}

fun Context?.getResourceNameOrUnknown(@IdRes res: Int): String {
    if (this == null) {
        return unknownString(res)
    }
    return resources.getResourceNameOrUnknown(res)
}

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
