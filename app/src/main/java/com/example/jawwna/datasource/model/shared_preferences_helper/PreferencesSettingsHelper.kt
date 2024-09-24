package com.example.jawwna.datasource.model.shared_preferences_helper


import android.content.Context
import android.content.SharedPreferences
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.WindSpeedUnits

class PreferencesSettingsHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)

    // Constants for keys
    private val KEY_TEMPERATURE_UNIT = "temperature_unit"
    private val KEY_WIND_SPEED_UNIT = "wind_speed_unit"
    private val KEY_LANGUAGE = "language"
    private val KEY_THEME = "theme"
    private val KEY_NOTIFICATIONS = "notifications"
    private val KEY_GET_LOCATION_MODE = "get_location_mode"


    private val KEY_OLD_TEMPERATURE_UNIT = "old_temperature_unit"
    private val KEY_OLD_WIND_SPEED_UNIT = "old_wind_speed_unit"



    // Save getLocationMode
    fun saveGetLocationMode(mode: String) {
        sharedPreferences.edit().putString(KEY_GET_LOCATION_MODE, mode).apply()
    }
    // Get getLocationMode
    fun getGetLocationMode(): String? {
        return sharedPreferences.getString(KEY_GET_LOCATION_MODE, "MAP")
    }
    //clear getLocationMode
    fun clearGetLocationMode() {
        sharedPreferences.edit().remove(KEY_GET_LOCATION_MODE).apply()
    }



    // Save temperature unit
    fun saveTemperatureUnit(unit: String) {
        val oldUnit = getTemperatureUnit()
        sharedPreferences.edit().putString(KEY_OLD_TEMPERATURE_UNIT, oldUnit).apply() // Save old unit
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, unit).apply()
    }

    // Get temperature unit
    fun getTemperatureUnit(): String? {
        return sharedPreferences.getString(KEY_TEMPERATURE_UNIT, TemperatureUnits.standard.toString()) // Default value
    }
    //Get old temperature unit
    fun getOldTemperatureUnit(): String? {
        return sharedPreferences.getString(KEY_OLD_TEMPERATURE_UNIT, TemperatureUnits.standard.toString())
    }
    //clear temperature unit
    fun clearTemperatureUnit() {
        sharedPreferences.edit().remove(KEY_TEMPERATURE_UNIT).apply()
    }
    // Get old wind speed unit
    fun getOldWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_OLD_WIND_SPEED_UNIT, WindSpeedUnits.metric.toString())
    }



    // Save wind speed unit
    fun saveWindSpeedUnit(unit: String) {
        val oldUnit = getWindSpeedUnit()
        sharedPreferences.edit().putString(KEY_OLD_WIND_SPEED_UNIT, oldUnit).apply() // Save old unit
        sharedPreferences.edit().putString(KEY_WIND_SPEED_UNIT, unit).apply()
    }

    // Get wind speed unit
    fun getWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_WIND_SPEED_UNIT, WindSpeedUnits.metric.toString()) // Default value
    }
    //clear wind speed unit
    fun clearWindSpeedUnit() {
        sharedPreferences.edit().remove(KEY_WIND_SPEED_UNIT).apply()
    }

    // Save app language
    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply()
    }

    // Get app language
    fun getLanguage(): String? {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") // Default value
    }
    //clear language
    fun clearLanguage() {
        sharedPreferences.edit().remove(KEY_LANGUAGE).apply()
    }

    // Save theme selection
    fun saveTheme(theme: String) {
        sharedPreferences.edit().putString(KEY_THEME, theme).apply()
    }

    // Get theme selection
    fun getTheme(): String? {
        return sharedPreferences.getString(KEY_THEME, "Light") // Default value
    }
    //clear theme
    fun clearTheme() {
        sharedPreferences.edit().remove(KEY_THEME).apply()
    }

    // Save notifications preference
    fun saveNotifications(status: String) {
        sharedPreferences.edit().putString(KEY_NOTIFICATIONS, status).apply()
    }

    // Get notifications preference
    fun getNotifications(): String? {
        return sharedPreferences.getString(KEY_NOTIFICATIONS, "disabled") // Default value
    }
    //clear notifications
    fun clearNotifications() {
        sharedPreferences.edit().remove(KEY_NOTIFICATIONS).apply()
    }

    // Clear all preferences
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    //reset Settings
    fun resetSettings(){
        sharedPreferences.edit().putString(KEY_THEME, "Light").apply()
        sharedPreferences.edit().putString(KEY_LANGUAGE, "en").apply()
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, TemperatureUnits.standard.toString()).apply()
        sharedPreferences.edit().putString(KEY_WIND_SPEED_UNIT, WindSpeedUnits.metric.toString()).apply()
        sharedPreferences.edit().putString(KEY_NOTIFICATIONS, "disabled").apply()
        sharedPreferences.edit().putString(KEY_GET_LOCATION_MODE, "MAP").apply()



    }

}
