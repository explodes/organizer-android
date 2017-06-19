package meta.injection

import dagger.Component
import io.explod.organizer.InjectTest
import io.explod.organizer.injection.*
import io.explod.organizer.injection.ObjectGraph.injector
import meta.BaseRoboTest
import javax.inject.Singleton

/**
 * ObjectGraph used to provide dependencies in test code
 */
object TestObjectGraph {

    val testInjector: TestInjector
        get() = injector as TestInjector

}

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
 * Injector interface for injecting into test class. Add inject methods here as needed.
 */
interface TestInjector : Injector {
    fun inject(target: InjectTest)
    fun inject(target: BaseRoboTest)
}