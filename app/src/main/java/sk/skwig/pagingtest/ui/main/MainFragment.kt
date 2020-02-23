package sk.skwig.pagingtest.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import sk.skwig.pagingtest.*


class MainFragment : Fragment() {

    private val tabsRecyclerView: RecyclerView by bindView(R.id.paging_indicator_recycler_view)
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val appbar: AppBarLayout by bindView(R.id.appbar)

    private val tabItemWidth: Float by bindDimen(R.dimen.tab_item_width)
    private val tabColor: Int by bindColor(R.color.tab_unselected_color)
    private val tabSelectedColor: Int by bindColor(R.color.tab_selected_color)

    private val filterLayoutPadding: Float by bindDimen(R.dimen.filter_layout_padding)

    private var totalTabsScroll = 0

    private lateinit var mainListAdapter: MainListAdapter

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Appbar behavior init
        (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        // RecyclerView Init
        mainListAdapter = MainListAdapter(requireContext())
        recyclerView.adapter = mainListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                Log.d("matej", "MainFragment.onScrollStateChanged() called with: newState = [$newState]")
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && isInIdleState()) {
//                    val newPosition = findCompletelyVisiblePosition()
//                    if (newPosition != RecyclerView.NO_POSITION) {
//                        indicator.setDotCount(attachedAdapter.getItemCount())
//                        if (newPosition < attachedAdapter.getItemCount()) {
//                            indicator.setCurrentPosition(newPosition)
//                        }
//                    }
//                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.d("matej", "MainFragment.onScrolled() called with: dx = [$dx], dy = [$dy]")
                updateCurrentOffset()
            }
        })

        //
//        val recyclerIndicator = view.findViewById<ScrollingPagerIndicator>(R.id.indicator)
//        recyclerIndicator.attachToRecyclerView(recyclerView)


        val items = mainListAdapter.data.distinctBy { it.groupId }.map {
            val initialOffset = 25

            val day = (initialOffset + it.groupId) % 30
            val month = when ((initialOffset + it.groupId) / 30) {
                0 -> "JAN"
                1 -> "FEB"
                2 -> "MAR"
                3 -> "APR"
                else -> TODO()
            }

            TabItem(SimpleDate(day, month))
        }

        tabsRecyclerView.updatePadding(right = (requireContext().screenWidth - tabItemWidth - filterLayoutPadding).toInt())
        tabsRecyclerView.adapter = FiltersTabsAdapter(requireContext(), items) { clickedPosition ->
            // smoothScroll = true will call the onPageScrolled callback which will smoothly
            // animate (transform) the tabs accordingly

//            viewPager.setCurrentItem(clickedPosition, true)
        }
        tabsRecyclerView.layoutManager = NoScrollHorizontalLayoutManager(requireContext())

        // Sync Tabs And Pager
        tabsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                totalTabsScroll += dx
            }
        })

//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                // Scroll tabs as viewpager is scrolled
//                val dx = (position + positionOffset) * tabItemWidth - totalTabsScroll
//                tabsRecyclerView.scrollBy(dx.toInt(), 0)
//
//                // This acts like a page transformer for tabsRecyclerView. Ideally we should do this in the
//                // onScrollListener for the RecyclerView but that requires extra math. positionOffset
//                // is all we need so let's use that to apply transformation to the tabs
//
//                val currentTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position) ?: return
//                val nextTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position + 1)
//
//                val defaultScale: Float = FiltersTabsAdapter.defaultScale
//                val maxScale: Float = FiltersTabsAdapter.maxScale
//
//                currentTabView.setScale(defaultScale + (1 - positionOffset) * (maxScale - defaultScale))
//                nextTabView?.setScale(defaultScale + positionOffset * (maxScale - defaultScale))
//
//                currentTabView.findViewById<View>(R.id.tab_pill).backgroundTintList = ColorStateList.valueOf(blendColors(tabColor, tabSelectedColor, 1 - positionOffset))
//                nextTabView?.findViewById<View>(R.id.tab_pill)?.backgroundTintList = ColorStateList.valueOf(blendColors(tabColor, tabSelectedColor, positionOffset))
//            }
//        })
    }

    private fun updateCurrentOffset() {
        val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition().takeIf { it != RecyclerView.NO_POSITION }
            ?: return


        val current = mainListAdapter.data[position]
        val next = mainListAdapter.data.getOrNull(position + 1)

        if (next?.groupId == null || next.groupId == current.groupId) {
            onPageScrolled(current.groupId, 0f)
            return
        }

        val currentChild = recyclerView.findViewHolderForAdapterPosition(position)!!.itemView
        val nextChild = recyclerView.findViewHolderForAdapterPosition(position + 1)!!.itemView
        Log.d("matej", "MainFragment.updateCurrentOffset() called")
//        val nextChild =  (recyclerView.layoutManager as LinearLayoutManager).getChildAt(position + 1)!!
//
        val offset = 1 - (nextChild.y / currentChild.height.toFloat())

//        val itemCount = mainListAdapter.itemCount // attachedAdapter.getItemCount()

        // In case there is an infinite pager
//        if (position >= itemCount && itemCount != 0) {
//            position = position % itemCount
//        }

//        if (offset >= 0 && offset <= 1 && position < itemCount) {
        onPageScrolled(current.groupId, offset)
//        }
    }

    private fun findFirstVisibleView(): View? {
        val pos = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        return recyclerView.getChildAt(pos)
    }

    // parametre su scopovane na tabky, nie na cely list
    private fun onPageScrolled(position: Int, positionOffset: Float) {
        Log.d("matej", "MainFragment.onPageScrolled() called with: position = [$position], positionOffset = [$positionOffset]")
        // Scroll tabs as viewpager is scrolled
        val dx = (position + positionOffset) * tabItemWidth - totalTabsScroll
        tabsRecyclerView.scrollBy(dx.toInt(), 0)

        // This acts like a page transformer for tabsRecyclerView. Ideally we should do this in the
        // onScrollListener for the RecyclerView but that requires extra math. positionOffset
        // is all we need so let's use that to apply transformation to the tabs

        val currentTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position) ?: return
        val previousTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position - 1)
        val nextTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position + 1)

//        val defaultScale: Float = FiltersTabsAdapter.defaultScale
//        val maxScale: Float = FiltersTabsAdapter.maxScale
////
//        currentTabView.setScale(defaultScale + (1 - positionOffset) * (maxScale - defaultScale))
//        nextTabView?.setScale(defaultScale + positionOffset * (maxScale - defaultScale))

        currentTabView.findViewById<View>(R.id.month).alpha = 1 - positionOffset
        previousTabView?.findViewById<View>(R.id.month)?.alpha = positionOffset
        nextTabView?.findViewById<View>(R.id.month)?.alpha = positionOffset

        currentTabView.findViewById<View>(R.id.tab_pill).alpha = 1 - positionOffset
        previousTabView?.findViewById<View>(R.id.tab_pill)?.alpha = positionOffset
        nextTabView?.findViewById<View>(R.id.tab_pill)?.alpha = positionOffset

//        currentTabView.findViewById<View>(R.id.tab_pill).backgroundTintList = ColorStateList.valueOf(blendColors(tabColor, tabSelectedColor, 1 - positionOffset))
//        nextTabView?.findViewById<View>(R.id.tab_pill)?.backgroundTintList = ColorStateList.valueOf(blendColors(tabColor, tabSelectedColor, positionOffset))

//        currentTabView.findViewById<View>(R.id.tab_pill).backgroundTintList = ColorStateList.valueOf(tabColor)
//        nextTabView?.findViewById<View>(R.id.tab_pill)?.backgroundTintList = ColorStateList.valueOf(tabSelectedColor)
    }
}
