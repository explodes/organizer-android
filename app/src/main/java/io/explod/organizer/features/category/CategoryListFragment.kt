package io.explod.organizer.features.category

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import features.category.CategoryListViewModel
import io.explod.organizer.R
import io.explod.organizer.extensions.find
import io.explod.organizer.extensions.getModel
import io.explod.organizer.extensions.mainActivity
import io.explod.organizer.extensions.toggleVisibility
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.common.ListAdapter
import io.explod.organizer.features.common.ListDiffCallback
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.database.CategoryStats
import io.explod.organizer.service.tracking.LevelE
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.stub_category_list.*
import kotlinx.android.synthetic.main.stub_category_list_empty.*
import javax.inject.Inject

/**
 * CategoryListFragment has the responsibility of listing available Categories.
 *
 * There is also a FloatingActionButton that allows the user to add a new Category.
 */
class CategoryListFragment : BaseFragment(), CategoryAdapter.Listener {

    companion object {

        fun new(): CategoryListFragment = CategoryListFragment()

        private val TAG = CategoryListFragment::class.java.simpleName

    }

    @Inject
    lateinit var tracker: Tracker

    val categoriesModel by getModel(CategoryListViewModel::class)

    val categoriesAdapter by lazy(LazyThreadSafetyMode.NONE) { CategoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_create_category.setOnClickListener {
            tracker.event("categoryListFabCreateCategoryClick", mapOf("numCategories" to categoriesAdapter.itemCount))
            onCreateCategoryClick()
        }

        categoriesAdapter.listener = this

        recycler_categories.layoutManager = LinearLayoutManager(context)
        recycler_categories.adapter = categoriesAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoriesModel.categories
                .compose(bindToLifecycle())
                .subscribeBy(
                        onNext = { onCategories(it) },
                        onError = { tracker.log(LevelE, TAG, "Unable to observe categories", it) }
                )
    }

    override fun onCategoryClick(category: CategoryStats) {
        tracker.event("categoryListCategoryClick", mapOf("name" to category.category.name))
        mainActivity?.pushFragment(CategoryDetailFragment.new(category.category.id))
    }

    /**
     * Called when receive new Categories from our ViewModel.
     *
     * Updates the adapter with our Category list
     */
    fun onCategories(categories: List<CategoryStats>?) {
        if (recycler_categories == null || stub_categories_empty == null) return

        val hasCategories = categories?.isNotEmpty() ?: false
        stub_categories_empty.toggleVisibility(!hasCategories)
        recycler_categories.toggleVisibility(hasCategories)

        categoriesAdapter.replaceItems(categories)
    }

    /**
     * Called when the user asks to create a new Category.
     *
     * The work is delegated to the MainActivity where the functionality to create a new Category
     * already exists.
     */
    fun onCreateCategoryClick() {
        mainActivity?.showCreateCategoryDialog()
    }

}

class CategoryAdapter : ListAdapter<CategoryStats, CategoryAdapter.CategoryViewHolder>() {

    interface Listener {
        fun onCategoryClick(category: CategoryStats)
    }

    var listener: Listener? = null

    init {
        setHasStableIds(true)
    }

    override fun createDiffCallback(old: List<CategoryStats>?, new: List<CategoryStats>?): ListDiffCallback<CategoryStats> {
        return CategoryDiffCallback(old, new)
    }

    override fun getItemId(position: Int): Long {
        return this[position]?.category?.id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder?, position: Int) {
        if (holder == null) return
        val category = this[position] ?: return

        holder.name.text = category.category.name
        val res = holder.itemView.context?.resources
        holder.itemCount.text = when {
            res == null || category.numItems == 0 -> ""
            else -> res.getQuantityString(R.plurals.category_list_num_items, category.numItems, category.numItems)
        }
    }

    fun notifyClick(position: Int) {
        val listener = this.listener ?: return
        val category = this[position] ?: return
        listener.onCategoryClick(category)
    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val name by find<TextView>(R.id.text_name)
        val itemCount by find<TextView>(R.id.text_item_count)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            notifyClick(adapterPosition)
        }

    }
}

class CategoryDiffCallback(old: List<CategoryStats>?, new: List<CategoryStats>?) : ListDiffCallback<CategoryStats>(old, new) {

    override fun areItemsTheSame(old: CategoryStats, new: CategoryStats): Boolean {
        return old.category.id == new.category.id
    }

    override fun areContentsTheSame(old: CategoryStats, new: CategoryStats): Boolean {
        return old.category.createdDate == new.category.createdDate &&
                old.category.name == new.category.name &&
                old.numItems == new.numItems &&
                old.averageRating == new.averageRating
    }

}