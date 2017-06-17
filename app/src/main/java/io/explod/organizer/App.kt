package io.explod.organizer

import android.app.Application
import android.util.Log
import io.explod.organizer.injection.AppModule
import io.explod.organizer.injection.DaggerObjectComponent
import io.explod.organizer.injection.ObjectComponent
import io.explod.organizer.injection.ObjectGraph
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.tracking.LevelV
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

abstract class App : Application() {

    companion object {
        val TAG: String = App::class.java.simpleName
        lateinit var app: App
        lateinit var tracker: Tracker
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        // Prepare Dagger 2
        ObjectGraph.setObjectComponent(buildObjectComponent())

        // Initialize Tracking
        tracker = TrackerInitializer().initialize(this)

        tracker.event("appStartup")
        tracker.log(LevelV, TAG, "testing")
        tracker.log(LevelV, TAG, "testing exception", Exception())
        tracker.recordException(LevelV, Exception("testing"))

    }

    /**
     * Create the App instances ObjectComponent to be used for injection
     */
    abstract fun buildObjectComponent(): ObjectComponent


}

class AppImpl : App() {

    override fun buildObjectComponent(): ObjectComponent {
        return DaggerObjectComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

}

class TrackerInitializer {

    @Inject
    lateinit var tracker: Tracker

    init {
        inject()
    }

    fun inject() {
        injector.inject(this)
    }

    /**
     * Initializes the Tracker on the io scheduler
     */
    fun initialize(app: App): Tracker {
        tracker.initialize(app)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {},
                        { Log.e(App.TAG, "error initializing tracker", it) }
                )
        return tracker
    }
}
