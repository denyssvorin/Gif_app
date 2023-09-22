package com.example.natifetesttask.ui.gifmainlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.natifetesttask.R
import com.example.natifetesttask.databinding.FragmentGifsMainListBinding
import com.example.natifetesttask.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GifMainListFragment : Fragment(), GifMainListAdapter.GifClickListener {

    private var _binding: FragmentGifsMainListBinding? = null
    private val binding get() = _binding!!

    private lateinit var gifAdapter: GifMainListAdapter
    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder

    private lateinit var searchView: SearchView
    private val viewModel by viewModels<GifMainListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGifsMainListBinding.inflate(inflater, container, false)
        setupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {

        initGifsMainListAdapter()
        initSwipeToRefresh()
        firstLoadError()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userRepoDbPagingFlow.collect { pagingData ->
                gifAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.gifEventChannel.collect() { event ->
                when (event) {
                    is GifMainListViewModel.GifMainListEvent.NavigateToGifDetailsScreen -> {
                        val action = GifMainListFragmentDirections
                            .actionGifsMainListFragmentToGifDetailsFragment(event.imageUrl)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun initGifsMainListAdapter() {
        binding.apply {

            gifAdapter = GifMainListAdapter(
                this@GifMainListFragment,
                requireContext()
            )

            // in case of loading errors this callback is called when you tap the 'Try Again' button
            val tryAgainAction: TryAgainAction = { gifAdapter.retry() }

            val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)

            // combined adapter which shows both the list of users + footer indicator when loading pages
            val adapterWithLoadState = gifAdapter.withLoadStateFooter(footerAdapter)

            rcView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            rcView.adapter = adapterWithLoadState

            mainLoadStateHolder = DefaultLoadStateAdapter.Holder(
                binding.loadStateView,
                binding.swipeToRefreshLayout,
                tryAgainAction
            )
        }
    }

    private fun initSwipeToRefresh() {
        binding.swipeToRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            stopProgress()
        }

    }

    private fun stopProgress() {
        binding.swipeToRefreshLayout.isRefreshing = false
    }

    private fun firstLoadError() {
        firstLoadRetry()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.apiRequestError.collect { value ->
                if (viewModel.firstLaunchValue.value == 1) {
                    binding.buttonFirstTryAgain.isVisible = value
                    binding.textViewFirstLoadError.isVisible = value
                }
            }
        }
    }

    private fun firstLoadRetry() {
        binding.buttonFirstTryAgain.setOnClickListener {
            viewModel.refresh()
        }
    }



    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                val searchIcon = menu.findItem(R.id.action_search)
                searchIcon.isVisible = true
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                val search = menu.findItem(R.id.action_search)
                searchView = search.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery != null) {
                    if (pendingQuery.isNotEmpty()) {
                        search.expandActionView()
                        searchView.setQuery(pendingQuery, false)
                    }
                }
                searchView.onQueryTextChanged { newValue ->
                    viewModel.searchQuery.value = newValue
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onClick(item: String) {
        viewModel.onGifClick(item)
    }
}