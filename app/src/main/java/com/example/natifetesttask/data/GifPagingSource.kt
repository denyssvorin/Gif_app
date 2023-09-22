package com.example.natifetesttask.data

import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.IOException
import java.lang.Exception

typealias GifApiPageLoader = suspend (limit: Int, offset: Int) -> ApiResponseData

class GifPagingSource(
    private val apiLoader: GifApiPageLoader,
    private val pageSize: Int
) : PagingSource<Int, ApiResponseData.DataEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ApiResponseData.DataEntity> {
        val pageIndex = params.key ?: 1
        _firstLaunchValue.value = pageIndex

        val offset = (pageIndex - 1) * pageSize
        val limit = if (offset == 0) {
            params.loadSize / 3
        } else {
            params.loadSize
        }

        return try {
            val apiResponse = apiLoader.invoke(limit, offset).data

            _apiRequestErrorFlow.value = false
            LoadResult.Page(
                data = apiResponse,
                prevKey = if (pageIndex == 1) null else pageIndex.minus(1),
                nextKey = if (apiResponse.size == limit) pageIndex + (limit / pageSize) else null)

        } catch (e: IOException) {
            _apiRequestErrorFlow.value = true
            LoadResult.Error(e)
        } catch (e: Exception) {
            _apiRequestErrorFlow.value = true
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, ApiResponseData.DataEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private val _apiRequestErrorFlow = MutableStateFlow(false)
        val apiRequestErrorFlow: MutableStateFlow<Boolean> = _apiRequestErrorFlow

        private val _firstLaunchValue = MutableStateFlow(1)
        val firstLaunchValue: StateFlow<Int> = _firstLaunchValue
    }
}