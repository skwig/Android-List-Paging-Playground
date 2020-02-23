package sk.skwig.pagingtest.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import sk.skwig.pagingtest.MainListAdapter
import sk.skwig.pagingtest.R
import sk.skwig.pagingtest.ToolbarBehavior
import sk.skwig.pagingtest.bindView



class MainFragment : Fragment() {

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val appbar: AppBarLayout by bindView(R.id.appbar)

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        //
        val recyclerIndicator = view.findViewById<ScrollingPagerIndicator>(R.id.indicator)
        recyclerIndicator.attachToRecyclerView(recyclerView)
    }
}
