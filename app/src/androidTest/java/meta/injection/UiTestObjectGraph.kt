package meta.injection

import dagger.Component
import io.explod.organizer.InjectionRuleTest
import io.explod.organizer.MainFragmentRuleTest
import io.explod.organizer.features.category.CategoryListFragmentTest
import io.explod.organizer.injection.AppModule
import io.explod.organizer.injection.Injector
import io.explod.organizer.injection.ObjectComponent
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.injection.RepoModule
import meta.rules.AppDatabaseClearer
import meta.rules.InjectionRule
import javax.inject.Singleton

/**
 * Declares the structure of our injection for unit tests.
 * Anything provided in {@link ObjectComponent} must also be
 * made available here but mock implementations can be provided.
 */
@Component(modules = arrayOf(
        AppModule::class,
        RepoModule::class,
        UiTestTrackerModule::class,
        UiTestDatabaseModule::class,
        UiTestImageModule::class
))
@Singleton
interface UiTestObjectComponent : ObjectComponent, UiTestInjector

/**
 * ObjectGraph used to provide dependencies in test code
 */
object UiTestObjectGraph {

    val uiTestInjector: UiTestInjector
        get() = injector as UiTestInjector

}

/**
 * Injector interface for injecting into test classes.
 * Add inject methods here as needed.
 */
interface UiTestInjector : Injector {
    fun inject(target: InjectionRule)
    fun inject(target: InjectionRuleTest)
    fun inject(target: MainFragmentRuleTest)
    fun inject(target: CategoryListFragmentTest)
    fun inject(target: AppDatabaseClearer)
}