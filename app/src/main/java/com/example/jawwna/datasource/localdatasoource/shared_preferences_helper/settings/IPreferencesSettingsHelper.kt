package com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings

interface IPreferencesSettingsHelper {
    // Save getLocationMode
    fun saveGetLocationMode(mode: String)

    // Get getLocationMode
    fun getGetLocationMode(): String?

    //clear getLocationMode
    fun clearGetLocationMode()
    fun setOldTemperatureUnit(unit: String)
    fun setOldWindSpeedUnit(unit: String)

    // Save temperature unit
    fun saveTemperatureUnit(unit: String)

    // Get temperature unit
    fun getTemperatureUnit(): String?

    //Get old temperature unit
    fun getOldTemperatureUnit(): String?

    //clear temperature unit
    fun clearTemperatureUnit()

    // Get old wind speed unit
    fun getOldWindSpeedUnit(): String?

    // Save wind speed unit
    fun saveWindSpeedUnit(unit: String)

    // Get wind speed unit
    fun getWindSpeedUnit(): String?

    //clear wind speed unit
    fun clearWindSpeedUnit()

    // Save app language
    fun saveLanguage(language: String)

    // Get app language
    fun getLanguage(): String?

    //clear language
    fun clearLanguage()

    // Save theme selection
    fun saveTheme(theme: String)

    // Get theme selection
    fun getTheme(): String?

    //clear theme
    fun clearTheme()

    // Save notifications preference
    fun saveNotifications(status: String)

    // Get notifications preference
    fun getNotifications(): String?

    //clear notifications
    fun clearNotifications()

    // Clear all preferences
    fun clearAllSettings()

    //reset Settings
    fun resetSettings()
}