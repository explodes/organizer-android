package io.explod.organizer

import android.app.Application
import android.util.Log
import io.explod.organizer.injection.AppModule
import io.explod.organizer.injection.DaggerObjectComponent
import io.explod.organizer.injection.ObjectComponent
import io.explod.organizer.injection.ObjectGraph
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

abstract class App : Application() {

    companion object {
        /**
         * Global App instance
         */
        lateinit var app: App

        private lateinit var tracker: Tracker
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        // Prepare Dagger
        ObjectGraph.setObjectComponent(buildObjectComponent())

        // Initialize Tracking
        tracker = TrackerInitializer().initialize(this)
        tracker.event("appStartup")
    }

    /**
     * Create the App instances ObjectComponent to be used for injection
     */
    abstract fun buildObjectComponent(): ObjectComponent

}

/**
 * The "real" (non-testing) implementation of our App
 */
class AppImpl : App() {

    override fun buildObjectComponent(): ObjectComponent {
        return DaggerObjectComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

}

/**
 * Helper class that initializes the Tracker
 */
class TrackerInitializer {

    companion object {
        private val TAG = TrackerInitializer::class.java.simpleName
    }

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
                        { Log.e(TAG, "error initializing tracker", it) }
                )
        return tracker
    }
}
