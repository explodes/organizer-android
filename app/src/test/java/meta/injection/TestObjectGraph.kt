package meta.injection

import dagger.Component
import io.explod.organizer.BaseRoboTestTest
import io.explod.organizer.injection.*
import meta.BaseRoboTestInjection
import javax.inject.Singleton

/**
 * Declares the structure of our injection for unit tests.
 * Anything provided in {@link ObjectComponent} must also be
 * made available here but mock implementations can be provided.
 */
@Component(modules = arrayOf(
        AppModule::class,
        RepoModule::class,
        TestTrackerModule::class,
        TestDatabaseModule::class,
        TestImageModule::class
))
@Singleton
interface TestObjectComponent : ObjectComponent, TestInjector

/**
 * ObjectGraph used to provide dependencies in test code
 */
object TestObjectGraph {

    val testInjector: TestInjector
        get() = ObjectGraph.injector as TestInjector

}

/**
 * Injector interface for injecting into test classes.
 * Add inject methods here as needed.
 */
interface TestInjector : Injector {
    fun inject(target: BaseRoboTestInjection)
    fun inject(target: BaseRoboTestTest)
}