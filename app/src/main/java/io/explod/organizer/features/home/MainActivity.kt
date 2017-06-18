package io.explod.organizer.features.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.Space
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import io.explod.organizer.R
import io.explod.organizer.extensions.getModel
import io.explod.organizer.extensions.getResourceNameOrUnknown
import io.explod.organizer.extensions.showSnackbar
import io.explod.organizer.features.common.BaseActivity
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.common.EditTextDialog
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.tracking.LevelE
import io.explod.organizer.service.tracking.LoggedException
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * MainActivity starts by showing showing the category list.
 *
 * MainActivity is also responsible for building and controlling the actions in the NavDrawer.
 */
class MainActivity : BaseActivity() {

    @Inject
    lateinit var tracker: Tracker

    val categoryModel by getModel(CategoryListViewModel::class)

    val navManager = NavViewManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                tracker.event("navDrawerOpened")
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                tracker.event("navDrawerClosed")
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(navManager)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CategoryListFragment.new())
                    .commit()
        }

        categoryModel.categories.observe(this, Observer<List<CategoryStats>> { onCategories(it) })
    }

    fun onCategories(categories: List<CategoryStats>?) {
        navManager.rebuildMenuCategories(categories)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            tracker.event("backPress", mapOf("closeDrawer" to "true"))
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            tracker.event("backPress", mapOf("closeDrawer" to "false"))
            super.onBackPressed()
        }
    }

    fun showCreateCategoryDialog() {
        val categoryModel = this.categoryModel
        EditTextDialog(this)
                .setTitle(R.string.home_dialog_title_create_category)
                .setOnTextChangedListener(object : EditTextDialog.OnTextChangedListener {
                    override fun onTextChanged(newText: String) {
                        if (!TextUtils.isEmpty(newText)) {
                            categoryModel.createCategory(newText)
                                    .compose(bindToLifecycle())
                                    .subscribeBy(onError = { tracker.recordException(LevelE, LoggedException("Unable to create category", it)) })
                        } else {
                            showSnackbar(R.string.home_error_category_name_empty, length = Snackbar.LENGTH_LONG, actionRes = R.string.home_error_category_name_empty_retry_action, action = {
                                showCreateCategoryDialog()
                            })
                        }
                    }
                })
                .show()
    }

    fun pushFragment(frag: BaseFragment) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out, R.anim.slide_in_right, android.R.anim.fade_out)
                .replace(R.id.container, frag)
                .addToBackStack(null)
                .commit()
    }

    inner class NavViewManager : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {

            if (item.itemId == 0) {
                handleCategoryMenuClick(item)
            } else {
                handleStandardMenuItem(item)
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }

        fun handleCategoryMenuClick(item: MenuItem) {
            tracker.event("navCategoryItemClick", mapOf("name" to "category", "title" to item.title))
            val tag = item.actionView?.tag ?: return
            if (tag is CategoryStats) {
                pushFragment(CategoryDetailFragment.new(tag.category.id))
            }
        }

        fun handleStandardMenuItem(item: MenuItem) {
            tracker.event("navStandardItemClick", mapOf("name" to getResourceNameOrUnknown(item.itemId), "title" to item.title))

            when (item.itemId) {
                R.id.nav_create_category -> {
                    showCreateCategoryDialog()
                }
                R.id.nav_share -> {
                    // todo(evan): open up share view
                    showSnackbar("todo: share somehow?")
                }
                R.id.nav_settings -> {
                    // todo(evan): find useful preferences and open up SettingsActivity
                    showSnackbar("todo: open up preferences")
                }
            }
        }


        fun rebuildMenuCategories(categories: List<CategoryStats>?) {
            val group = nav_view?.menu?.findItem(R.id.nav_group_categories_submenu) ?: return
            val menu = group.subMenu ?: return

            menu.clear()

            if (categories == null) {
                group.isVisible = false
            } else {
                group.isVisible = true
                categories.forEachIndexed { index, category ->
                    val item = menu.add(0, 0, index, category.category.name)
                    val view = Space(this@MainActivity)
                    view.tag = category
                    item.actionView = view
                }
            }

        }
    }

}

