package com.example.jawwna.homescreen.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.shared_preferences_helper.PreferencesLocationHelper
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.datasource.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val application: Application, private val repository: IRepository) :ViewModel() {

    private val preferencesLocationHelper = PreferencesLocationHelper(application)
    private val TAG = "HomeViewModel"


    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableLiveData<Int>()
    val cardSettingsFieldBackgroundLightModeLiveData: LiveData<Int> get() = _cardSettingsFieldBackgroundLightMode

    // LiveData to observe theme mode changes
    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode



//LiveDataGetWhterData
    private val _weatherData = MutableLiveData<CurrentWeather>()
    val weatherData: LiveData<CurrentWeather> get() = _weatherData

    fun fetchWeatherData(apiKey: String) {

        val latitude = preferencesLocationHelper.getLocationLatitude()
        val longitude = preferencesLocationHelper.getLocationLongitude()
        Log.i(TAG, "fetchWeatherData: $latitude $longitude")

        viewModelScope.launch {
          _weatherData.postValue(repository.getCurrenWeatherByLatLon(latitude, longitude,apiKey))
        }
    }

    fun setCardSettingsFieldBackgroundLightMode(packageName: String, nightModeFlags: Int) {
        val colorResId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.drawable.card_settings_field_background_night_mode
            Configuration.UI_MODE_NIGHT_NO -> R.drawable.card_settings_field_background_light_mode
            else -> R.drawable.card_settings_field_background_light_mode
        }
        _cardSettingsFieldBackgroundLightMode.value = colorResId


    }


    fun checkThemeMode(resources: Resources) {
        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        _isDarkMode.value = isDark
    }

    fun getTextColor(isDarkMode: Boolean): Int {
        return if (isDarkMode) android.R.color.holo_blue_bright else android.R.color.holo_blue_dark

    }

}
