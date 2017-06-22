package io.explod.organizer

import android.support.test.runner.AndroidJUnit4
import io.explod.organizer.service.images.ImageLoader
import io.explod.organizer.service.images.PicassoImageLoader
import meta.injection.UiTestObjectGraph.uiTestInjector
import meta.rules.InjectionRule
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class InjectionRuleTest {

    @get:Rule
    val injectionRule = InjectionRule()

    @Inject
    lateinit var imageLoader: ImageLoader

    @Before
    fun setUp() {
        uiTestInjector.inject(this)
    }

    @Test

    fun uiTestInjector_shouldNotInjectRealDependencies() {
        // the test injector should be set up and able to
        // inject our test-version of dependencies
        if (imageLoader is PicassoImageLoader) {
            fail("Got PicassoImageLoader when we shouldn't have")
        }

    }
}
