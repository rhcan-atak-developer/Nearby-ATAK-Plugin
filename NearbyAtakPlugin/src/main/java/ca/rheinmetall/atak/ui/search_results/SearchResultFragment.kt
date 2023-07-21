package ca.rheinmetall.atak.ui.search_results

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import ca.rheinmetall.atak.DistanceFormatter
import ca.rheinmetall.atak.R
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.dagger.ViewModelFactory
import ca.rheinmetall.atak.databinding.SearchResultFragmentBinding
import ca.rheinmetall.atak.ui.PointOfInterestViewModel
import javax.inject.Inject

class SearchResultFragment @Inject constructor(
    @PluginContext private val pluginContext: Context,
    private val viewModelFactory: ViewModelFactory,
    private val distanceFormatter: DistanceFormatter
) : Fragment() {
    private lateinit var binding: SearchResultFragmentBinding
    private lateinit var viewModel: SearchResultViewModel
    private lateinit var adapter: SearchResultAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SearchResultFragmentBinding.inflate(LayoutInflater.from(pluginContext), container, false)
        val dividerItemDecoration = DividerItemDecoration(pluginContext, DividerItemDecoration.VERTICAL)
        binding.searchResultRecyclerView.addItemDecoration(dividerItemDecoration)
        adapter = SearchResultAdapter(pluginContext, distanceFormatter) { changeSelectedStatus(it) }
        binding.searchResultRecyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchResultViewModel::class.java]
        binding.addAllButton.setOnClickListener { viewModel.addAllResult()}
        binding.addSelectedButton.setOnClickListener { viewModel.addSelectedResult()}
        viewModel.results.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() {
        viewModel.onDestroyView()
        super.onDestroyView()
    }

    private fun changeSelectedStatus(it: SelectableSearchResult) {
        it.selected = !it.selected
        viewModel.triggerUpdate()
    }
}