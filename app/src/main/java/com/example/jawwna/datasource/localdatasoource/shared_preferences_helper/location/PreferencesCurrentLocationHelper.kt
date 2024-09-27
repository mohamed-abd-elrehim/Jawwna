package com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location

import android.content.Context
import android.content.SharedPreferences

class PreferencesCurrentLocationHelper(context: Context) : IPreferencesLocationHelper {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("location_current_preferences", Context.MODE_PRIVATE)

    // Constants for keys
    private val KEY_LOCATION_LATITUDE = "location_latitude"
    private val KEY_LOCATION_LONGITUDE = "location_longitude"
    private val KEY_LOCATION_NAME = "location_name"

    // Save location name
    override fun saveLocationName(name: String) {
        sharedPreferences.edit().putString(KEY_LOCATION_NAME, name).apply()
    }

    // Get location name
    override fun getLocationName(): String? {
        return sharedPreferences.getString(KEY_LOCATION_NAME, null)
    }

    // Clear location name
    override fun clearLocationName() {
        sharedPreferences.edit().remove(KEY_LOCATION_NAME).apply()
    }

    // Save location latitude
    override fun saveLocationLatitude(latitude: Double) {
        sharedPreferences.edit().putString(KEY_LOCATION_LATITUDE, latitude.toString()).apply()
    }

    // Get location latitude
    override fun getLocationLatitude(): Double? {
        return sharedPreferences.getString(KEY_LOCATION_LATITUDE, null)?.toDoubleOrNull()
    }

    // Clear location latitude
    override fun clearLocationLatitude() {
        sharedPreferences.edit().remove(KEY_LOCATION_LATITUDE).apply()
    }

    // Save location longitude
    override fun saveLocationLongitude(longitude: Double) {
        sharedPreferences.edit().putString(KEY_LOCATION_LONGITUDE, longitude.toString()).apply()
    }

    // Get location longitude
    override fun getLocationLongitude(): Double? {
        return sharedPreferences.getString(KEY_LOCATION_LONGITUDE, null)?.toDoubleOrNull()
    }

    // Clear location longitude
    override fun clearLocationLongitude() {
        sharedPreferences.edit().remove(KEY_LOCATION_LONGITUDE).apply()
    }

    // Clear all preferences
    override fun clearAllLocation() {
        sharedPreferences.edit().clear().apply()
    }

    // Check if no location is saved
   override fun isLocationSaved(): Boolean {
        val name = getLocationName()
        val latitude = getLocationLatitude()
        val longitude = getLocationLongitude()

        return name != null && latitude != null && longitude != null
    }
}
