package com.example.natifetesttask.data

data class ApiResponseData(
    val data: List<DataEntity>
) {
    data class DataEntity(
        val url: String,
        val images: ImageEntity
    ) {
        data class ImageEntity(
            val original: Gif
        ) {
            data class Gif(
                val url: String,
                val height: Int,
                val width: Int
            )
        }
    }
}

