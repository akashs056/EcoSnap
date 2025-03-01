package com.example.ecosnap.models

data class Event(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val participants: String,
    val points: String,
    val organizer: String
)