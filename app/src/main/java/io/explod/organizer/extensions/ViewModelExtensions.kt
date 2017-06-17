package io.explod.organizer.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlin.reflect.KClass


fun <T : ViewModel> FragmentActivity.getModel(klass: KClass<T>): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(klass.java)
    }
}

fun <T : ViewModel> Fragment.getModel(klass: KClass<T>): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(klass.java)
    }
}