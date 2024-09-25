package com.example.jawwna.settingsfragment.viewmodel


import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jawwna.R
import com.example.jawwna.datasource.repository.IRepository

class SettingsViewModel(private val iRepository: IRepository) : ViewModel() {

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
        _temperatureUnit.value = iRepository.getTemperatureUnit()
        _windSpeedUnit.value = iRepository.getWindSpeedUnit()
        _language.value = iRepository.getLanguage()
        _theme.value = iRepository.getTheme()
        _notificationsStatus.value = iRepository.getNotifications()
        _getLocationMode.value = iRepository.getGetLocationMode()
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
            iRepository.saveGetLocationMode(locationMode)
            _getLocationMode.value = locationMode
        }
        if(temperatureUnit != null) {
            iRepository.saveTemperatureUnit(temperatureUnit)
            _temperatureUnit.value = temperatureUnit
        }
        if(windSpeedUnit != null) {
            iRepository.saveWindSpeedUnit(windSpeedUnit)
            _windSpeedUnit.value = windSpeedUnit
        }
        if(language != null) {
            iRepository.saveLanguage(language)
            _language.value = language
        }
        if(theme != null) {
            iRepository.saveTheme(theme)
            _theme.value = theme
        }
        if(notificationsStatus != null) {
            iRepository.saveNotifications(notificationsStatus)
            _notificationsStatus.value = notificationsStatus
        }
    }
    fun resetSettings() {
        iRepository.resetSettings()
        loadSettings()
    }

}
