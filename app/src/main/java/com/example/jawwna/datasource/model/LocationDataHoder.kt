package com.example.jawwna.datasource.model

import java.io.Serializable

data class LocationDataHolder(
    val latitude: Double,
    val longitude: Double,
    val locationName: String? = null
): Serializable