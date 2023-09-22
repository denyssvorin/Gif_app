package com.example.natifetesttask.ui.gifmainlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.natifetesttask.data.ApiResponseData
import com.example.natifetesttask.data.GifPagingSource
import com.example.natifetesttask.data.repos.GifRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifMainListViewModel @Inject constructor(
    private val repository: GifRepositoryImpl
) : ViewModel() {

    private val _apiRequestErrorFlow = repository.apiRequestErrorFlow
    val apiRequestError: StateFlow<Boolean> = _apiRequestErrorFlow

    private val _firstLaunchValue = repository.firstLaunchValue
    val firstLaunchValue = _firstLaunchValue

    val searchQuery = MutableLiveData("")

    val userRepoDbPagingFlow: Flow<PagingData<ApiResponseData.DataEntity>> = searchQuery.asFlow()
        .flatMapLatest {
            repository.getPagedGifs(it)
        }
        // use cacheIn operator for flows returned by Pager. Otherwise exception may be thrown
        // when refreshing/invalidating or subscribing to the flow more than once.
        .cachedIn(viewModelScope)


    fun refresh() {
        searchQuery.postValue(searchQuery.value)
    }

    private val _gifEventChannel = Channel<GifMainListEvent>()
    val gifEventChannel = _gifEventChannel.receiveAsFlow()

    fun onGifClick(item: String) = viewModelScope.launch {
        _gifEventChannel.send(GifMainListEvent.NavigateToGifDetailsScreen(item))
    }

    sealed class GifMainListEvent {
        data class NavigateToGifDetailsScreen(val imageUrl: String): GifMainListEvent()
    }
}