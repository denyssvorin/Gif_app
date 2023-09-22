package com.example.natifetesttask.data.repos

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.natifetesttask.data.ApiResponseData
import com.example.natifetesttask.data.GifApiPageLoader
import com.example.natifetesttask.data.api.GifApi
import com.example.natifetesttask.data.GifPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GifRepositoryImpl @Inject constructor(
    private val api: GifApi,
    private val ioDispatcher: CoroutineDispatcher
) : GifRepository {

    private val _apiRequestErrorFlow = GifPagingSource.apiRequestErrorFlow
    val apiRequestErrorFlow: StateFlow<Boolean> = _apiRequestErrorFlow

    private val _firstLaunchValue = GifPagingSource.firstLaunchValue
    val firstLaunchValue = _firstLaunchValue

    override fun getPagedGifs(searchQuery: String): Flow<PagingData<ApiResponseData.DataEntity>> {
        val apiLoader: GifApiPageLoader = { limit, offset ->
            if (searchQuery.isEmpty()) {
                getDefaultGifs(limit = limit, offset = offset)
            } else {
                getGifs(limit = limit, offset = offset, searchQuery = searchQuery)
            }
        }
        return Pager(
            config = PagingConfig(
                pageSize = DEF_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GifPagingSource(
                    apiLoader = apiLoader,
                    pageSize = DEF_PAGE_SIZE
                )
            }
        ).flow
    }

    private suspend fun getGifs(limit: Int, offset: Int, searchQuery: String): ApiResponseData
            = withContext(ioDispatcher) {

        delay(500)

        val list = api.getGifs(API_KEY, searchQuery, limit, offset)

        return@withContext list
    }
    private suspend fun getDefaultGifs(limit: Int, offset: Int): ApiResponseData
            = withContext(ioDispatcher) {

        delay(500)

        val list = api.getDefaultGifs(API_KEY, limit, offset)

        return@withContext list
    }

    companion object {
        const val API_KEY = "EFjEV5SKuZzZWwXgkV7nb8AHeU6yeoCu"
        const val DEF_PAGE_SIZE = 20
    }
}