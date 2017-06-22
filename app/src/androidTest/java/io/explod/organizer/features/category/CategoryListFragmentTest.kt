package io.explod.organizer.features.category

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import io.explod.organizer.R
import meta.rules.MainFragmentRule
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryListFragmentTest {

    @get:Rule
    val fragRule = MainFragmentRule { CategoryListFragment.new() }

    @Throws(Exception::class)
    @Test
    fun fabCreateCategory_click_enterText_OK_createsCategory() {
        // This fragment features a floating action button that when clicked
        // opens a dialog asking for the name of a new category.
        // When the name is entered and OK is clicked on the dialog,
        // the category should be created and displayed on the page.
        onView(withId(R.id.fab_create_category)).perform(click())

        onView(withId(R.id.text_input)).perform(typeTextIntoFocusedView("Hello, World!"))

        onView(withText("OK")).perform(click())

        onView(allOf(withText("Hello, World!"), withId(R.id.text_name))).check(matches(isDisplayed()))
    }

}