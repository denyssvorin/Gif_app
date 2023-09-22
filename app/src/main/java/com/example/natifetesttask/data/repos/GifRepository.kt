package com.example.natifetesttask.data.repos

import androidx.paging.PagingData
import com.example.natifetesttask.data.ApiResponseData
import kotlinx.coroutines.flow.Flow

interface GifRepository {
    fun getPagedGifs(searchQuery: String): Flow<PagingData<ApiResponseData.DataEntity>>
}