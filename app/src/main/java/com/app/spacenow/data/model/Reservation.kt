package com.app.spacenow.data.model

import java.util.Date

data class Reservation(
    val id: String,
    val spaceId: String,
    val spaceName: String,
    val userId: String,
    val dateTime: Date,
    val status: ReservationStatus = ReservationStatus.CONFIRMED
)

enum class ReservationStatus {
    CONFIRMED,
    CANCELLED,
    PENDING
}