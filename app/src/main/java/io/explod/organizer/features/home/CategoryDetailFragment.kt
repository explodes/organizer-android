package io.explod.organizer.features.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.explod.arch.data.Item
import io.explod.organizer.App.Companion.tracker
import io.explod.organizer.R
import io.explod.organizer.extensions.*
import io.explod.organizer.features.common.BaseFragment
import io.explod.organizer.features.common.ListAdapter
import io.explod.organizer.features.common.ListDiffCallback
import io.explod.organizer.service.database.CategoryStats
import kotlinx.android.synthetic.main.fragment_category_detail.*

class CategoryDetailFragment : BaseFragment(), CategoryItemAdapter.Listener {

    companion object {

        fun new(categoryId: Long): CategoryDetailFragment {
            val frag = CategoryDetailFragment()
            val args = Bundle()
            args.putLong(ARG_CATEGORY_ID, categoryId)
            frag.arguments = args
            return frag
        }

        private val ARG_CATEGORY_ID = "categoryId"
    }

    val categoryDetailModel by getModelWithFactory(CategoryDetailViewModel::class, { CategoryDetailViewModel.Factory(args.getLong(ARG_CATEGORY_ID)) })

    val categoryItemsAdapter by lazy(LazyThreadSafetyMode.NONE) { CategoryItemAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_create_item.setOnClickListener {
            tracker.event("categoryDetailFabCreateItemClick", mapOf("numItems" to categoryItemsAdapter.itemCount - 1))
            onCreateItemClick()
        }

        categoryItemsAdapter.listener = this

        recycler_items.layoutManager = LinearLayoutManager(context)
        recycler_items.adapter = categoryItemsAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoryDetailModel.categoryItems.observe(this, Observer<List<CategoryItem>> {
            onCategoryItems(it)
        })
    }

    override fun onItemClick(item: Item) {
        // todo(evan): show item details
        mainActivity?.showSnackbar("todo: show item details")
    }

    fun onCreateItemClick() {
        // todo(evan): create item on click
        mainActivity?.showSnackbar("todo: create item")
    }

    fun onCategoryItems(items: List<CategoryItem>?) {
        if (recycler_items == null) return
        categoryItemsAdapter.replaceItems(items)
    }

}

class CategoryItemAdapter : ListAdapter<CategoryItem, CategoryItemAdapter.CategoryItemViewHolder>() {

    interface Listener {
        fun onItemClick(item: Item)
    }

    companion object {
        private val TYPE_INVALID = -1
        private val TYPE_CATEGORY = 0
        private val TYPE_ITEM = 1
    }

    var listener: Listener? = null

    init {
        setHasStableIds(true)
    }

    override fun createDiffCallback(old: List<CategoryItem>?, new: List<CategoryItem>?): DiffUtil.Callback {
        return CategoryItemDiffCallback(old, new)
    }

    override fun getItemId(position: Int): Long {
        val ci = this[position] ?: return 0
        return ci.item?.id ?: ci.stats.category.id
    }

    override fun getItemViewType(position: Int): Int {
        val ci = this[position] ?: return TYPE_INVALID
        if (ci.item != null) return TYPE_ITEM
        return TYPE_CATEGORY
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CategoryItemViewHolder? {
        return when (viewType) {
            TYPE_CATEGORY -> {
                val context = parent?.context ?: return null
                val view = LayoutInflater.from(context).inflate(R.layout.card_category_detail_category, parent, false)
                return CategoryViewHolder(view)
            }
            TYPE_ITEM -> {
                val context = parent?.context ?: return null
                val view = LayoutInflater.from(context).inflate(R.layout.card_category_detail_item, parent, false)
                return ItemViewHolder(view)
            }
            else -> null
        }
    }

    override fun onBindViewHolder(unknownHolder: CategoryItemViewHolder?, position: Int) {
        if (unknownHolder == null) return
        val ci = this[position] ?: return

        when (getItemViewType(position)) {
            TYPE_CATEGORY -> {
                val stats = ci.stats
                val holder = unknownHolder as CategoryViewHolder
                onBindCategoryViewHolder(stats, holder)
            }
            TYPE_ITEM -> {
                val item = ci.item ?: return
                val holder = unknownHolder as ItemViewHolder
                onBindItemViewHolder(item, holder)
            }
        }
    }

    fun onBindCategoryViewHolder(stats: CategoryStats, holder: CategoryItemAdapter.CategoryViewHolder) {
        val res = holder.itemView.context?.resources

        holder.name.text = stats.category.name

        holder.itemCount.text = when {
            res == null || stats.numItems == 0 -> ""
            else -> res.getQuantityString(R.plurals.category_list_num_items, stats.numItems, stats.numItems)
        }
    }


    fun onBindItemViewHolder(item: Item, holder: CategoryItemAdapter.ItemViewHolder) {
        val res = holder.itemView.context?.resources

        holder.name.text = item.name

        holder.rating.text = when {
            res == null || item.rating == -1 -> ""
            else -> res.getQuantityString(R.plurals.category_list_num_items, item.rating, item.rating)
        }
    }

    fun notifyClick(position: Int) {
        val listener = this.listener ?: return
        val ci = this[position] ?: return
        val item = ci.item ?: return
        listener.onItemClick(item)
    }

    abstract inner class CategoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CategoryViewHolder(view: View) : CategoryItemViewHolder(view) {

        val name by find<TextView>(R.id.text_name)
        val itemCount by find<TextView>(R.id.text_item_count)

    }

    inner class ItemViewHolder(view: View) : CategoryItemViewHolder(view), View.OnClickListener {

        val name by find<TextView>(R.id.text_name)
        val rating by find<TextView>(R.id.text_rating)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            notifyClick(adapterPosition)
        }
    }
}

class CategoryItemDiffCallback(old: List<CategoryItem>?, new: List<CategoryItem>?) : ListDiffCallback<CategoryItem>(old, new) {

    override fun isTheSame(old: CategoryItem, new: CategoryItem): Boolean {
        return old.stats.category.id == new.stats.category.id &&
                ((old.item == null && new.item == null) || (old.item != null && new.item != null && old.item.id == new.item.id))
    }

    override fun areContentsTheSame(old: CategoryItem, new: CategoryItem): Boolean {
        return categoriesHaveSameContent(old.stats, new.stats) &&
                itemsHaveSameContent(old.item, new.item)
    }

    fun categoriesHaveSameContent(old: CategoryStats, new: CategoryStats): Boolean {
        return old.category.createdDate == new.category.createdDate &&
                old.category.name == new.category.name &&
                old.numItems == new.numItems &&
                old.averageRating == new.averageRating
    }

    fun itemsHaveSameContent(old: Item?, new: Item?): Boolean {
        if (old == null && new == null) return true
        if (old == null || new == null) return false
        return old.categoryId == new.categoryId &&
                old.createdDate == new.createdDate &&
                old.name == new.name &&
                old.photoUri == new.photoUri &&
                old.rating == new.rating
    }

}