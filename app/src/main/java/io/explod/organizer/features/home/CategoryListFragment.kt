package io.explod.organizer.features.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.explod.arch.data.Category
import io.explod.organizer.R
import io.explod.organizer.extensions.*
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.common.ListAdapter
import io.explod.organizer.features.common.ListDiffCallback
import io.explod.organizer.injection.ObjectGraph.injector
import io.explod.organizer.service.tracking.Tracker
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.stub_category_list.*
import kotlinx.android.synthetic.main.stub_category_list_empty.*
import javax.inject.Inject


class CategoryListFragment : BaseFragment(), CategoryAdapter.Listener {

    companion object {

        fun new(): CategoryListFragment = CategoryListFragment()

        private val TAG = CategoryListFragment::class.java.simpleName

    }

    @Inject
    lateinit var tracker: Tracker

    val categoriesModel by getModel(CategoryViewModel::class)
    val categoriesAdapter by lazy(LazyThreadSafetyMode.NONE) { CategoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
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
        categoriesModel.categories.observe(this, Observer<List<Category>> { onCategories(it) })
    }

    override fun onCategoryClick(category: Category) {
        // todo(evan): handle click
        tracker.event("categoryListCategoryClick", mapOf("name" to category.name))
        mainActivity?.showSnackbar("TODO: handle click")
    }

    fun onCategories(categories: List<Category>?) {
        if (recycler_categories == null || stub_categories_empty == null) return

        val hasCategories = categories?.isNotEmpty() ?: false
        stub_categories_empty.toggleVisibility(!hasCategories)
        recycler_categories.toggleVisibility(hasCategories)

        categoriesAdapter.replaceItems(categories)
    }

    fun onCreateCategoryClick() {
        mainActivity?.showCreateCategoryDialog()
    }

}

class CategoryAdapter : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>() {

    interface Listener {
        fun onCategoryClick(category: Category)
    }

    var listener: Listener? = null

    init {
        setHasStableIds(true)
    }

    override fun createDiffCallback(old: List<Category>?, new: List<Category>?): ListDiffCallback<Category> {
        return CategoryDiffCallback(old, new)
    }

    override fun getItemId(position: Int): Long {
        return this[position]?.id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = this[position] ?: return

        holder.name.text = category.name
        holder.itemCount.text = "3 items" // todo(evan): create CategoryWithCount
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

class CategoryDiffCallback(old: List<Category>?, new: List<Category>?) : ListDiffCallback<Category>(old, new) {
    override fun isTheSame(old: Category, new: Category): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: Category, new: Category): Boolean {
        return old.createdDate == new.createdDate &&
                old.name == new.name
    }

}