package io.explod.organizer.features.home

import android.arch.lifecycle.Observer
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.explod.arch.data.Category
import io.explod.organizer.R
import io.explod.organizer.extensions.getModel
import io.explod.organizer.extensions.showSnackbar
import io.explod.organizer.features.common.BaseActivity
import io.explod.organizer.features.common.EditTextDialog
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.tracking.LevelW
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

    companion object {
        private val FRAGTAG_CATEGORY_LIST = "categoryList"
    }

    @Inject
    lateinit var tracker: Tracker

    val categoryModel by getModel(CategoryViewModel::class)

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
                    .replace(R.id.container, CategoryListFragment.new(), FRAGTAG_CATEGORY_LIST)
                    .commit()
        }

        categoryModel.categories.observe(this, Observer<List<Category>> { onCategories(it) })
    }

    fun onCategories(categories: List<Category>?) {
        navManager.rebuildMenuCategories(categories)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            tracker.event("backPressWithDrawerOpen")
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            tracker.event("backPressWithDrawerClosed")
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
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
                                    .subscribeBy(onError = { tracker.recordException(LevelW, Exception("unable to create category", it)) })
                        } else {
                            showSnackbar(R.string.home_error_category_name_empty, length = Snackbar.LENGTH_LONG, actionRes = R.string.home_error_category_name_empty_retry_action, action = {
                                showCreateCategoryDialog()
                            })
                        }
                    }
                })
                .show()
    }

    fun trackWithResourceName(event: String, @IdRes res: Int) {
        var idName: String
        try {
            idName = resources.getResourceName(res)
        } catch (ex: Resources.NotFoundException) {
            idName = "unknown-0x${Integer.toHexString(res)}"
        }
        tracker.event(event, mapOf("name" to idName))
    }

    inner class NavViewManager : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            handleStandardMenuItem(item.itemId)
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }

        fun handleStandardMenuItem(@IdRes itemId: Int) {

            trackWithResourceName("navStandardItemClick", itemId)

            when (itemId) {
                R.id.nav_create_category -> {
                    showCreateCategoryDialog()
                }
                R.id.nav_share -> {
                }
                R.id.nav_settings -> {
                }
            }
        }

        fun rebuildMenuCategories(categories: List<Category>?) {
            // todo(evan): re-create the category menu contents
        }
    }

}