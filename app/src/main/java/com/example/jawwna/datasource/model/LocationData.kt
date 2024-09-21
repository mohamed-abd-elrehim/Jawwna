package com.example.jawwna.datasource.model

// LocationData.kt
// LocationData.kt
import com.google.android.gms.maps.model.LatLng

object LocationData {
    // Mutable properties for latitude and longitude
    var latitude: Double = 30.0444
    var longitude: Double = 31.2357
    var placeName: String = "Cairo"

    // Method to get the current LatLng
    fun getCurrentLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
    fun getCurrentPlaceName(): String {
        return placeName
    }

    // Method to update latitude and longitude
    fun updateLocation(newLatitude: Double, newLongitude: Double) {
        latitude = newLatitude
        longitude = newLongitude
    }
    fun updatePlaceName(newPlaceName: String) {
        placeName = newPlaceName
    }

    fun getCurrentLocationData(): LocationData {
        return LocationData
    }
}

