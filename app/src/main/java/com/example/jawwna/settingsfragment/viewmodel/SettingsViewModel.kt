package com.example.jawwna.settingsfragment.viewmodel


import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jawwna.R
import com.example.jawwna.datasource.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val iRepository: IRepository) : ViewModel() {

    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableStateFlow<Int>(0)
    val cardSettingsFieldBackgroundLightModeLiveData: StateFlow<Int> get() = _cardSettingsFieldBackgroundLightMode
    var currentLanguage:String? ="en"

    // LiveData properties to observe settings
    private val _temperatureUnit = MutableStateFlow<String?>("")
    val temperatureUnit: StateFlow<String?> get() = _temperatureUnit
    private val _windSpeedUnit = MutableStateFlow<String?>("")
    val windSpeedUnit: StateFlow<String?> get() = _windSpeedUnit
    private val _language = MutableStateFlow<String?>("")
    val language: StateFlow<String?> get() = _language
    private val _theme = MutableStateFlow<String?>("")
    val theme: StateFlow<String?> get() = _theme

    private val _getLocationMode = MutableStateFlow<String?>("")
    val getLocationMode: StateFlow<String?> get() = _getLocationMode

    init {
        loadSettings()
        currentLanguage = iRepository.getLanguage()
    }

    private fun loadSettings() {
        _temperatureUnit.value = iRepository.getTemperatureUnit()
        _windSpeedUnit.value = iRepository.getWindSpeedUnit()
        _language.value = iRepository.getLanguage()
        _theme.value = iRepository.getTheme()
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
    }

    fun isCurrentLanguage( language: String):Boolean {
        if(currentLanguage ==language ) {
            return true
        }else  {
            return false
        }

    }

    fun resetSettings():Boolean {
        iRepository.resetSettings()
        loadSettings()
        if (currentLanguage!= iRepository.getLanguage()) {
            return true
        }else {
            return false
        }
    }

}
