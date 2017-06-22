package io.explod.organizer

import android.support.test.runner.AndroidJUnit4
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.home.MainActivity
import io.explod.organizer.service.images.ImageLoader
import io.explod.organizer.service.images.PicassoImageLoader
import meta.injection.UiTestObjectGraph.uiTestInjector
import meta.rules.MainFragmentRule
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MainFragmentRuleTest {

    class TestFragment : BaseFragment()

    @get:Rule
    val fragRule = MainFragmentRule { TestFragment() }

    @Inject
    lateinit var imageLoader: ImageLoader

    @Before
    fun setUp() {
        uiTestInjector.inject(this)
    }

    @Test
    fun fragRule_fragIsTestFragment() {
        assertTrue(fragRule.frag is TestFragment)
    }

    @Test
    fun fragRule_activityIsMainActivity() {
        assertTrue(fragRule.activity is MainActivity)
    }

    @Test
    fun uiTestInjector_shouldNotInjectRealDependencies() {
        // the test injector should be set up and able to
        // inject our test-version of dependencies
        if (imageLoader is PicassoImageLoader) {
            Assert.fail("Got PicassoImageLoader when we shouldn't have")
        }

    }


}

