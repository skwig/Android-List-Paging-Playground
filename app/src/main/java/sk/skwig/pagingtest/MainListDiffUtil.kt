package sk.skwig.pagingtest

import androidx.recyclerview.widget.DiffUtil

class MainListDiffUtil(
    private val oldListItem: List<ListItemModel>,
    private val newListItem: List<ListItemModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldListItem.size

    override fun getNewListSize() = newListItem.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldListItem[oldItemPosition].itemId == newListItem[newItemPosition].itemId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldListItem[oldItemPosition] == newListItem[newItemPosition]
}