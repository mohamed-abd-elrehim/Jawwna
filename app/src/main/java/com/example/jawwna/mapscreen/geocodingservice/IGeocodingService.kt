package com.example.jawwna.mapscreen.geocodingservice

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface IGeocodingService {
    fun searchPlace(query: String): Flow<LatLng?>
    fun getCountryNameFromLatLong(latitude: Double, longitude: Double): String?
}