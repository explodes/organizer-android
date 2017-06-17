package io.explod.organizer

import io.explod.organizer.service.tracking.Tracker
import meta.BaseRoboTest
import meta.injection.TestObjectGraph.testInjector
import org.junit.Assert.assertNotNull
import org.junit.Test
import javax.inject.Inject

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BaseRoboTestTest : BaseRoboTest() {

    @Test
    fun testCodeIsInjectable() {
        val tester = InjectTest()

        assertNotNull(tester.tracker)
    }

}

class InjectTest {

    @Inject
    lateinit var tracker: Tracker

    init {
        inject()
    }

    fun inject() {
        testInjector.inject(this)
    }

}