package io.explod.organizer.injection

import dagger.Component
import io.explod.organizer.features.category.CategoryDetailFragment
import features.category.CategoryListViewModel
import io.explod.organizer.TrackerInitializer
import io.explod.organizer.features.category.CategoryDetailViewModel
import io.explod.organizer.features.category.CategoryListFragment
import io.explod.organizer.features.home.MainActivity
import io.explod.organizer.features.item.ItemDetailFragment
import io.explod.organizer.features.item.ItemDetailViewModel
import io.explod.organizer.service.repo.AppRepoImpl
import io.explod.organizer.service.repo.AsyncAppRepo
import javax.inject.Singleton


/**
 * Declares the structure of our injection
 */
@Component(modules = arrayOf(
        AppModule::class,
        TrackerModule::class,
        RepoModule::class,
        DatabaseModule::class,
        ImageModule::class
))
@Singleton
interface ObjectComponent : Injector

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
 * Injector interface. Add inject methods here as needed.
 */
interface Injector {
    fun inject(target: MainActivity)
    fun inject(target: TrackerInitializer)
    fun inject(target: AppRepoImpl)
    fun inject(target: AsyncAppRepo)
    fun inject(target: CategoryListViewModel)
    fun inject(target: CategoryListFragment)
    fun inject(target: CategoryDetailViewModel)
    fun inject(target: ItemDetailViewModel)
    fun inject(target: CategoryDetailFragment)
    fun inject(target: ItemDetailFragment)
}