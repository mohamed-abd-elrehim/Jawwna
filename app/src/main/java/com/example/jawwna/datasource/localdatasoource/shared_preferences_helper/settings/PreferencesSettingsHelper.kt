package com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings


import android.content.Context
import android.content.SharedPreferences
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.WindSpeedUnits

class PreferencesSettingsHelper(context: Context) : IPreferencesSettingsHelper {
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
    override fun saveGetLocationMode(mode: String) {
        sharedPreferences.edit().putString(KEY_GET_LOCATION_MODE, mode).apply()
    }
    // Get getLocationMode
    override fun getGetLocationMode(): String? {
        return sharedPreferences.getString(KEY_GET_LOCATION_MODE, "MAP")
    }
    //clear getLocationMode
    override fun clearGetLocationMode() {
        sharedPreferences.edit().remove(KEY_GET_LOCATION_MODE).apply()
    }

    override fun setOldTemperatureUnit(unit: String) {
        sharedPreferences.edit().putString(KEY_OLD_TEMPERATURE_UNIT, unit).apply()


    }
    override fun setOldWindSpeedUnit(unit: String) {
        sharedPreferences.edit().putString(KEY_OLD_WIND_SPEED_UNIT, unit).apply()
    }


    // Save temperature unit
    override fun saveTemperatureUnit(unit: String) {
        val oldUnit = getTemperatureUnit()
        sharedPreferences.edit().putString(KEY_OLD_TEMPERATURE_UNIT, oldUnit).apply() // Save old unit
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, unit).apply()
    }

    // Get temperature unit
    override fun getTemperatureUnit(): String? {
        return sharedPreferences.getString(KEY_TEMPERATURE_UNIT, TemperatureUnits.standard.toString()) // Default value
    }
    //Get old temperature unit
    override fun getOldTemperatureUnit(): String? {
        return sharedPreferences.getString(KEY_OLD_TEMPERATURE_UNIT, TemperatureUnits.standard.toString())
    }
    //clear temperature unit
    override fun clearTemperatureUnit() {
        sharedPreferences.edit().remove(KEY_TEMPERATURE_UNIT).apply()
    }
    // Get old wind speed unit
    override fun getOldWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_OLD_WIND_SPEED_UNIT, WindSpeedUnits.metric.toString())
    }



    // Save wind speed unit
    override fun saveWindSpeedUnit(unit: String) {
        val oldUnit = getWindSpeedUnit()
        sharedPreferences.edit().putString(KEY_OLD_WIND_SPEED_UNIT, oldUnit).apply() // Save old unit
        sharedPreferences.edit().putString(KEY_WIND_SPEED_UNIT, unit).apply()
    }

    // Get wind speed unit
    override fun getWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_WIND_SPEED_UNIT, WindSpeedUnits.metric.toString()) // Default value
    }
    //clear wind speed unit
    override fun clearWindSpeedUnit() {
        sharedPreferences.edit().remove(KEY_WIND_SPEED_UNIT).apply()
    }

    // Save app language
    override fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply()
    }

    // Get app language
    override fun getLanguage(): String? {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") // Default value
    }
    //clear language
    override fun clearLanguage() {
        sharedPreferences.edit().remove(KEY_LANGUAGE).apply()
    }

    // Save theme selection
    override fun saveTheme(theme: String) {
        sharedPreferences.edit().putString(KEY_THEME, theme).apply()
    }

    // Get theme selection
    override fun getTheme(): String? {
        return sharedPreferences.getString(KEY_THEME, "Light") // Default value
    }
    //clear theme
    override fun clearTheme() {
        sharedPreferences.edit().remove(KEY_THEME).apply()
    }

    // Save notifications preference
    override fun saveNotifications(status: String) {
        sharedPreferences.edit().putString(KEY_NOTIFICATIONS, status).apply()
    }

    // Get notifications preference
    override fun getNotifications(): String? {
        return sharedPreferences.getString(KEY_NOTIFICATIONS, "disabled") // Default value
    }
    //clear notifications
    override fun clearNotifications() {
        sharedPreferences.edit().remove(KEY_NOTIFICATIONS).apply()
    }

    // Clear all preferences
    override fun clearAllSettings() {
        sharedPreferences.edit().clear().apply()
    }

    //reset Settings
    override fun resetSettings(){
        sharedPreferences.edit().putString(KEY_THEME, "Light").apply()
        sharedPreferences.edit().putString(KEY_LANGUAGE, "en").apply()
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, TemperatureUnits.standard.toString()).apply()
        sharedPreferences.edit().putString(KEY_WIND_SPEED_UNIT, WindSpeedUnits.metric.toString()).apply()
        sharedPreferences.edit().putString(KEY_NOTIFICATIONS, "disabled").apply()
        sharedPreferences.edit().putString(KEY_GET_LOCATION_MODE, "MAP").apply()



    }

}
