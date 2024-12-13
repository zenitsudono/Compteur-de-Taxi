package com.example.taxi.models

data class Driver(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val licenseType: String = "",
    val baseFare: Double = 2.5,
    val perKmRate: Double = 1.5,
    val perMinuteRate: Double = 0.5,
    val rating: Float = 0.0f,
    val phoneNumber: String = "",
    val carModel: String = "",
    val carPlateNumber: String = ""
)
