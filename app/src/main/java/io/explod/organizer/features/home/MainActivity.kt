package io.explod.organizer.features.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import io.explod.arch.data.Category
import io.explod.organizer.R
import io.explod.organizer.extensions.getModel
import io.explod.organizer.extensions.hide
import io.explod.organizer.extensions.show
import io.explod.organizer.features.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * MainActivity starts by showing (very, very briefly) a ProgressBar in the middle of the screen.
 *
 * When the [CategoryViewModel] has loaded its first set of data, the CategoryListFragment is
 * brought into view.
 *
 * MainActivity is also responsible for building and controlling the actions in the NavDrawer.
 */
class MainActivity : BaseActivity() {

    val categoriesModel by getModel(CategoryViewModel::class)

    val navManager = NavViewManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(navManager)

        if (savedInstanceState == null) {
            showProgressBar()
            categoriesModel.categories.observe(this, Observer<List<Category>> { onInitialCategories(it) })
        }
    }

    fun showProgressBar() {
        progress_loading.show()
        container.hide()
    }

    fun showContent() {
        progress_loading.hide()
        container.show()
    }

    fun onInitialCategories(categories: List<Category>?) {
        showContent()
        navManager.rebuildMenuCategories(categories)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .add(R.id.container, CategoryListFragment.new(), null)
                .commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
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


    inner class NavViewManager : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            // Handle navigation view item clicks here.
            handleStandardMenuItem(item.itemId)
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }

        fun handleStandardMenuItem(@IdRes itemId: Int) {
            when (itemId) {
                R.id.nav_create_category -> {
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