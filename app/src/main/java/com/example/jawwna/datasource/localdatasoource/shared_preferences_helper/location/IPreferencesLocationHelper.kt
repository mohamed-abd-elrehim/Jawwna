package com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location

interface IPreferencesLocationHelper {
    // Save location name
    fun saveLocationName(name: String)

    // Get location name
    fun getLocationName(): String?

    // Clear location name
    fun clearLocationName()

    // Save location latitude
    fun saveLocationLatitude(latitude: Double)

    // Get location latitude
    fun getLocationLatitude(): Double?

    // Clear location latitude
    fun clearLocationLatitude()

    // Save location longitude
    fun saveLocationLongitude(longitude: Double)

    // Get location longitude
    fun getLocationLongitude(): Double?

    // Clear location longitude
    fun clearLocationLongitude()

    // Clear all preferences
    fun clearAllLocation()

    // Check if no location is saved
    fun isLocationSaved(): Boolean

}