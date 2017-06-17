package io.explod.organizer.injection

import dagger.Component
import io.explod.organizer.TrackerInitializer
import io.explod.organizer.features.home.CategoryListFragment
import io.explod.organizer.features.home.CategoryViewModel
import io.explod.organizer.features.home.MainActivity
import io.explod.organizer.service.repo.AppRepoImpl
import io.explod.organizer.service.repo.AsyncAppRepo
import javax.inject.Singleton


object ObjectGraph {

    private lateinit var objectComponent: ObjectComponent

    /**
     * Get the Injector to inject dependencies into your instance
     */
    val injector: Injector
        get() = objectComponent

    /**
     * Set which ObjectComponent to use for injection.
     * Each BaseRoboTest will receive a new ObjectGraph.
     */
    fun setObjectComponent(component: ObjectComponent) {
        objectComponent = component
    }

}

/**
 * Declares the structure of our injection
 */
@Component(modules = arrayOf(
        AppModule::class,
        TrackerModule::class,
        RepoModule::class,
        DatabaseModule::class
))
@Singleton
interface ObjectComponent : Injector

/**
 * Injector interface. Add inject methods here as needed.
 */
interface Injector {
    fun inject(target: MainActivity)
    fun inject(target: TrackerInitializer)
    fun inject(target: AppRepoImpl)
    fun inject(target: AsyncAppRepo)
    fun inject(target: CategoryViewModel)
    fun inject(target: CategoryListFragment)
}