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
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject


/**
 * Base class for unit tests that use Android components.

 * Created by evan on 4/13/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, application = TestApp::class, sdk = intArrayOf(25))
abstract class BaseRoboTest {

    companion object {

        val DEFAULT_TIMEOUT = 5_000L

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

        /**
         * Offload work to a new thread, but block until complete.
         *
         * A great use-case is when you are testing Room stuff, and
         * are not allowed to query on the main thread. Adding
         * [android.arch.persistence.room.RoomDatabase.Builder.allowMainThreadQueries]
         * will let the tests run without crashing, but the app may be misusing
         * Room and the tests will not reflect that.
         */
        fun <T> offload(timeoutMillis: Long = DEFAULT_TIMEOUT, work: () -> T): T? {
            val success = arrayOf(false)
            val latch = CountDownLatch(1)
            val thread = OffloadButWaitThread(work, {
                success[0] = true
                latch.countDown()
            })
            thread.start()
            latch.await(timeoutMillis, TimeUnit.MILLISECONDS)
            if (!success[0]) {
                throw TimeoutException("Unable to perform work in a reasonable time frame")
            }
            return thread.result
        }

        /**
         * Shorthand for [offload] when you do not need a result
         */
        fun offloadWork(timeoutMillis: Long = DEFAULT_TIMEOUT, work: () -> Any) {
            offload(timeoutMillis, work)
        }
    }

    @Inject
    lateinit var db: AppDatabase

    @Before
    fun injectDependencies() {
        testInjector.inject(this)
    }

    /**
     * Set up mockito
     */
    @Before
    fun setUpDexCache() {
        System.setProperty("dexmaker.dexcache", RuntimeEnvironment.application.cacheDir.path)
    }

    @After
    fun closeDatabase() {
        db.close()
    }
}

class TestApp : App() {

    override fun buildObjectComponent(): TestObjectComponent {
        return DaggerTestObjectComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

}

private class OffloadButWaitThread<T>(val work: () -> T, val done: () -> Unit) : Thread(nextName) {

    companion object {
        private val NAME = "offload-work"
        private var sCount = 0L
        private val nextName: String
            get() = "$NAME-${++sCount}"
    }

    var result: T? = null

    override fun run() {
        result = work()
        done()
    }

}