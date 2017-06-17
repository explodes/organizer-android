package io.explod.organizer.features.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import com.trello.rxlifecycle2.components.support.RxFragment

abstract class BaseFragment : RxFragment(), LifecycleRegistryOwner {

    @Suppress("LeakingThis")
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

}
