package com.learprogramming.artbook.framework.repository.data

data class ImageResultResponse(
    val hits: List<ImageResultDto>,
    val total: Int,
    val totalHits: Int
)
