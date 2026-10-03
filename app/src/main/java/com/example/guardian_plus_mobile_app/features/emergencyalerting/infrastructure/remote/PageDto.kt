package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** The platform's PageResource: one page of items plus the paging totals. */
data class PageDto<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)
