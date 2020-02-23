package sk.skwig.pagingtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

const val TABS = 100

data class SimpleDate(val day: Int, val month: String)

data class TabItem(val date: SimpleDate)

class FiltersTabsAdapter(context: Context, val data: List<TabItem>, private val listener: (TabItem) -> Unit) : RecyclerView.Adapter<FiltersTabsAdapter.FiltersTabsViewHolder>() {

    private val toggleAnimDuration = 100L // TODO: context.resources.getInteger(R.integer.toggleAnimDuration).toLong()

    private val tabColor: Int by bindColor(context, R.color.tab_unselected_color)

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersTabsViewHolder =
        FiltersTabsViewHolder(inflater.inflate(R.layout.item_paging_indicator, parent, false))

    override fun onBindViewHolder(holder: FiltersTabsViewHolder, position: Int) {
        val previousItem = data.getOrNull(position - 1)
        val item = data[position]

        holder.month.text = item.date.month
        holder.day.text = item.date.day.toString()
        holder.month.alpha = if (previousItem?.date?.month != item.date.month) 1f else 0f
        holder.pill.alpha = 0f

        holder.itemView.setOnClickListener { listener(data[holder.adapterPosition]) }
    }

    class FiltersTabsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pill: View by bindView(R.id.tab_pill)
        val month: TextView by bindView(R.id.month)
        val day: TextView by bindView(R.id.day)
//        val badge: View by bindView(R.id.tab_badge)
    }

    companion object {
        const val defaultScale = 0.9f
        const val maxScale = 1.15f
    }
}
