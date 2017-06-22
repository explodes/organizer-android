/*
 * Copyright (c) 2017 SalesRabbit, Inc. All rights reserved.
 */

package meta

import io.explod.arch.data.AppDatabase
import io.explod.organizer.App
import io.explod.organizer.BuildConfig
import io.explod.organizer.injection.AppModule
import meta.injection.DaggerTestObjectComponent
import meta.injection.TestObjectComponent
import meta.injection.TestObjectGraph.testInjector
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import javax.inject.Inject


/**
 * Base class for unit tests that use Android components.

 * Created by evan on 4/13/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, application = TestApp::class, sdk = intArrayOf(25))
abstract class BaseRoboTest {

    /**
     * Direct database access for most of the unit tests
     */
    val db: AppDatabase
        get() = injection.db

    private val injection = BaseRoboTestInjection()

    /**
     * Set up mockito
     */
    @Before
    fun setUpDexCache() {
        System.setProperty("dexmaker.dexcache", RuntimeEnvironment.application.cacheDir.path)
    }

    /**
     * Wait for the Main looper to catch up on all of its pending events.

     * Anything queued on the Main Looper will be executed.
     */
    fun waitForMainLooper() {
        val context = RuntimeEnvironment.application
        val scheduler = shadowOf(context.mainLooper).scheduler
        while (scheduler.advanceToLastPostedRunnable()) {
        }
    }

}

/**
 * Injection helper made a separate class to avoid confusing Dagger
 * about ambiguous inject methods.
 */
class BaseRoboTestInjection {

    @Inject
    lateinit var db: AppDatabase // Robolectric will automatically close the database

    init {
        inject()
    }

    fun inject() {
        testInjector.inject(this)
    }

}

class TestApp : App() {

    override fun buildObjectComponent(): TestObjectComponent {
        return DaggerTestObjectComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

}