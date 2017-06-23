package meta.rules

import android.support.test.InstrumentationRegistry
import io.explod.organizer.App
import io.explod.organizer.injection.AppModule
import io.explod.organizer.injection.ObjectComponent
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.injection.ObjectGraph.setObjectComponent
import io.explod.organizer.service.database.AppDatabase
import meta.injection.DaggerUiTestObjectComponent
import meta.injection.UiTestObjectComponent
import meta.injection.UiTestObjectGraph.uiTestInjector
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import javax.inject.Inject

class InjectionRule : TestRule {

    private fun buildObjectComponent(): UiTestObjectComponent {
        val app = InstrumentationRegistry.getTargetContext().applicationContext as App
        return DaggerUiTestObjectComponent.builder()
                .appModule(AppModule(app))
                .build()

    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val old = injector
                val new = buildObjectComponent()
                setObjectComponent(new)
                AppDatabaseClearer.clearDatabase()
                try {
                    base.evaluate()
                } finally {
                    AppDatabaseClearer.clearDatabase()
                    setObjectComponent(old as ObjectComponent)
                }
            }
        }
    }

}

class AppDatabaseClearer {

    companion object {
        fun clearDatabase() {
            AppDatabaseClearer().go()
        }
    }

    @Inject
    lateinit var db: AppDatabase

    init {
        uiTestInjector.inject(this)
    }

    private fun go() {
        db.categories().clear()
        db.items().clear() // implied by categories.clear()
    }

}