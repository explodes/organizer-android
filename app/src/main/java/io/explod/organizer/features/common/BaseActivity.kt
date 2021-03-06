package io.explod.organizer.features.common

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 * BaseActivity is responsible for providing Rx lifecycle awareness and LiveData lifecycle awareness
 */
abstract class BaseActivity : RxAppCompatActivity(), LifecycleRegistryOwner {

    @Suppress("LeakingThis")
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

}
