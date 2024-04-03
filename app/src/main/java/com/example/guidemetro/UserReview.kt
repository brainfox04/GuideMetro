package com.example.guidemetro

data class UserReview(
    val reviewId: String = "",
    val stationNameEn: String = "",
    val stationNameRu: String = "",
    val userId: String = "",
    val text: String = "",
    val rating: Float = 0.0f
) {
    constructor() : this("", "", "", "", "", 0.0f)
}