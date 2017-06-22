package meta.rules

import android.support.test.rule.ActivityTestRule
import io.explod.organizer.R
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.home.MainActivity
import meta.awaitUi
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


/**
 * Rule that will replace the container view with the fragment specified
 * by the factory. It will ensure Injection is reset BEFORE the MainActivity is launched.
 */
class MainFragmentRule<out T : BaseFragment>(fragmentFactory: () -> T) : TestRule {

    /**
     * Rule responsible for created a Fragment inside of a MainActivity
     */
    private val fragRule = CoreMainFragmentRule(fragmentFactory)

    /**
     * Inner ActivityTestRule
     */
    val activityRule: ActivityTestRule<MainActivity>
        get() = fragRule

    /**
     * Retrieve the fragment built by this TestRule
     */
    val frag: T
        get() = fragRule.frag!!

    /**
     * Shorthand for rule.activityRule.activity
     */
    val activity: MainActivity
        get() = fragRule.activity!!

    private val chain = RuleChain.emptyRuleChain()
            .around(InjectionRule())
            .around(fragRule)

    override fun apply(base: Statement?, description: Description?): Statement {
        // just run our inner chain
        return chain.apply(base, description)
    }

}

private class CoreMainFragmentRule<T : BaseFragment>(val fragmentFactory: () -> T) : ActivityTestRule<MainActivity>(MainActivity::class.java) {

    var frag: T? = null

    override fun afterActivityLaunched() {
        frag = fragmentFactory()
        awaitUi {
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, frag)
                    .commitNow()
        }
    }

}