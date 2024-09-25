package com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location
import android.content.Context
import android.content.SharedPreferences

class PreferencesLocationHelper(context: Context) : IPreferencesLocationHelper {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("location_preferences", Context.MODE_PRIVATE)

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
        return sharedPreferences.getString(KEY_LOCATION_NAME,"Cairo" )
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
    override fun getLocationLatitude(): Double {
        return sharedPreferences.getString(KEY_LOCATION_LATITUDE, "30.0444")?.toDouble() ?: 30.0444
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
    override fun getLocationLongitude(): Double {
        return sharedPreferences.getString(KEY_LOCATION_LONGITUDE, "31.2357")?.toDouble() ?: 31.2357
    }

    // Clear location longitude
    override fun clearLocationLongitude() {
        sharedPreferences.edit().remove(KEY_LOCATION_LONGITUDE).apply()
    }

    // Clear all preferences
    override fun clearAllLocation() {
        sharedPreferences.edit().clear().apply()
    }

}