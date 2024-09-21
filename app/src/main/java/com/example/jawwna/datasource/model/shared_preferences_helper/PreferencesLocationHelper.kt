package com.example.jawwna.datasource.model.shared_preferences_helper

import android.content.Context
import android.content.SharedPreferences

class PreferencesLocationHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("location_preferences", Context.MODE_PRIVATE)

    // Constants for keys
    private val KEY_LOCATION_LATITUDE = "location_latitude"
    private val KEY_LOCATION_LONGITUDE = "location_longitude"
    private val KEY_LOCATION_NAME = "location_name"

    // Save location name
    fun saveLocationName(name: String) {
        sharedPreferences.edit().putString(KEY_LOCATION_NAME, name).apply()
    }

    // Get location name
    fun getLocationName(): String? {
        return sharedPreferences.getString(KEY_LOCATION_NAME,"Cairo" )
    }

    // Clear location name
    fun clearLocationName() {
        sharedPreferences.edit().remove(KEY_LOCATION_NAME).apply()
    }

    // Save location latitude
    fun saveLocationLatitude(latitude: Double) {
        sharedPreferences.edit().putString(KEY_LOCATION_LATITUDE, latitude.toString()).apply()
    }

    // Get location latitude
    fun getLocationLatitude(): Double {
        return sharedPreferences.getString(KEY_LOCATION_LATITUDE, "30.0444")?.toDouble() ?: 30.0444
    }

    // Clear location latitude
    fun clearLocationLatitude() {
        sharedPreferences.edit().remove(KEY_LOCATION_LATITUDE).apply()
    }

    // Save location longitude
    fun saveLocationLongitude(longitude: Double) {
        sharedPreferences.edit().putString(KEY_LOCATION_LONGITUDE, longitude.toString()).apply()
    }

    // Get location longitude
    fun getLocationLongitude(): Double {
        return sharedPreferences.getString(KEY_LOCATION_LONGITUDE, "31.2357")?.toDouble() ?: 31.2357
    }

    // Clear location longitude
    fun clearLocationLongitude() {
        sharedPreferences.edit().remove(KEY_LOCATION_LONGITUDE).apply()
    }

    // Clear all preferences
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

}
