package io.explod.organizer.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.explod.organizer.R
import io.explod.organizer.extensions.getModel
import io.explod.organizer.features.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_category_list.*


class CategoryListFragment : BaseFragment() {

    companion object {

        fun new(): CategoryListFragment = CategoryListFragment()

        private val TAG = CategoryListFragment::class.java.simpleName

    }

    val categoriesModel by getModel(CategoryViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_create_category.setOnClickListener { onCreateCategoryClick() }
    }

    fun onCreateCategoryClick() {

    }

}