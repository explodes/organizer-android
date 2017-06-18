package io.explod.organizer.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlin.reflect.KClass


/**
 * Lazy load a ViewModel by class
 */
fun <T : ViewModel> FragmentActivity.getModel(klass: KClass<T>): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(klass.java)
    }
}

/**
 * Lazy load a ViewModel by class
 */
fun <T : ViewModel> Fragment.getModel(klass: KClass<T>): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(klass.java)
    }
}

/**
 * Lazy load a ViewModel by class and create one with the given factory
 */
fun <T : ViewModel> Fragment.getModelWithFactory(klass: KClass<T>, factory: () -> ViewModelProvider.Factory): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory()).get(klass.java)
    }
}