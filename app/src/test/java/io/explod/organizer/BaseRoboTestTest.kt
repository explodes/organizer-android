package io.explod.organizer

import io.explod.organizer.service.images.ImageLoader
import io.explod.organizer.service.images.PicassoImageLoader
import meta.BaseRoboTest
import meta.injection.TestObjectGraph.testInjector
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BaseRoboTestTest : BaseRoboTest() {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Before
    fun setUp() {
        testInjector.inject(this)
    }

    @Test
    fun testInjector_shouldNotInjectRealDependencies() {
        // the test injector should be set up and able to
        // inject our test-version of dependencies
        if (imageLoader is PicassoImageLoader) {
            Assert.fail("Got PicassoImageLoader when we shouldn't have")
        }

    }

}