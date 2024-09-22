package com.example.jawwna.settingsfragment.viewmodel


import android.app.Application
import android.content.res.Configuration
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jawwna.R
import com.example.jawwna.datasource.model.shared_preferences_helper.PreferencesLocationHelper
import com.example.jawwna.datasource.model.shared_preferences_helper.PreferencesSettingsHelper

class SettingsViewModel(application: Application) : ViewModel() {
    private val preferencesSettingsHelper: PreferencesSettingsHelper =
        PreferencesSettingsHelper(application)

    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableLiveData<Int>()
    val cardSettingsFieldBackgroundLightModeLiveData: LiveData<Int> get() = _cardSettingsFieldBackgroundLightMode

    // LiveData properties to observe settings
    private val _temperatureUnit = MutableLiveData<String?>()
    val temperatureUnit: LiveData<String?> get() = _temperatureUnit
    private val _windSpeedUnit = MutableLiveData<String?>()
    val windSpeedUnit: LiveData<String?> get() = _windSpeedUnit
    private val _language = MutableLiveData<String?>()
    val language: LiveData<String?> get() = _language
    private val _theme = MutableLiveData<String?>()
    val theme: LiveData<String?> get() = _theme
    private val _notificationsStatus = MutableLiveData<String?>()
    val notificationsStatus: LiveData<String?> get() = _notificationsStatus
    private val _getLocationMode = MutableLiveData<String?>()
    val getLocationMode: LiveData<String?> get() = _getLocationMode

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _temperatureUnit.value = preferencesSettingsHelper.getTemperatureUnit()
        _windSpeedUnit.value = preferencesSettingsHelper.getWindSpeedUnit()
        _language.value = preferencesSettingsHelper.getLanguage()
        _theme.value = preferencesSettingsHelper.getTheme()
        _notificationsStatus.value = preferencesSettingsHelper.getNotifications()
        _getLocationMode.value = preferencesSettingsHelper.getGetLocationMode()
    }




    fun setCardSettingsFieldBackgroundLightMode(packageName: String, nightModeFlags: Int) {
        val colorResId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.drawable.card_settings_field_background_night_mode
            Configuration.UI_MODE_NIGHT_NO -> R.drawable.card_settings_field_background_light_mode
            else -> R.drawable.card_settings_field_background_light_mode
        }
        _cardSettingsFieldBackgroundLightMode.value = colorResId

    }

    fun saveSettings(locationMode: String?, temperatureUnit: String?, windSpeedUnit: String?, language: String?, theme: String?, notificationsStatus: String?) {
        if(locationMode != null) {
            preferencesSettingsHelper.saveGetLocationMode(locationMode)
            _getLocationMode.value = locationMode
        }
        if(temperatureUnit != null) {
            preferencesSettingsHelper.saveTemperatureUnit(temperatureUnit)
            _temperatureUnit.value = temperatureUnit
        }
        if(windSpeedUnit != null) {
            preferencesSettingsHelper.saveWindSpeedUnit(windSpeedUnit)
            _windSpeedUnit.value = windSpeedUnit
        }
        if(language != null) {
            preferencesSettingsHelper.saveLanguage(language)
            _language.value = language
        }
        if(theme != null) {
            preferencesSettingsHelper.saveTheme(theme)
            _theme.value = theme
        }
        if(notificationsStatus != null) {
            preferencesSettingsHelper.saveNotifications(notificationsStatus)
            _notificationsStatus.value = notificationsStatus
        }
    }
    fun resetSettings() {
        preferencesSettingsHelper.resetSettings()
        loadSettings()
    }

}
