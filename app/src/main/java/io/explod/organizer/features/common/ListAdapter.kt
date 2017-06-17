package io.explod.organizer.features.common

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView


abstract class ListAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    var items: List<T>? = null

    abstract fun createDiffCallback(old: List<T>?, new: List<T>?): DiffUtil.Callback

    fun replaceItems(items: List<T>?) {
        val old = this.items
        if (old == null) {
            if (items != null) {
                this.items = items
                notifyDataSetChanged()
            }
            // otherwise we don't need to handle any differences.
        } else {
            if (items == null) {
                this.items = null
                notifyItemRangeRemoved(0, old.size)
            } else {
                this.items = items
                val diffCallback = createDiffCallback(old, items)
                DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this)
            }
        }
    }

    operator fun get(position: Int): T? {
        val users = this.items ?: return null
        if (position >= 0 && position < users.size) return users[position]
        return null
    }


    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

}

abstract class ListDiffCallback<T>(val old: List<T>?, val new: List<T>?) : DiffUtil.Callback() {

    abstract fun isTheSame(old: T, new: T): Boolean

    abstract fun areContentsTheSame(old: T, new: T): Boolean

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = isTheSame(old!![oldItemPosition], new!![newItemPosition])

    override fun getOldListSize(): Int = old?.size ?: 0

    override fun getNewListSize(): Int = new?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = areContentsTheSame(old!![oldItemPosition], new!![newItemPosition])

}