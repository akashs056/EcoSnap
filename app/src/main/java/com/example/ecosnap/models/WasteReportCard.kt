package com.example.ecosnap.models

data class WasteReportCard(
    val user: String,
    val imageUrl: String,
    val location: String,
    val description: String,
    val status: String,
    val type: String,
    val createdAt: String,
)
