package com.example.jawwna.mapscreen.viewmodel
// MapViewModel.kt
import android.app.Application
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.shared_preferences_helper.PreferencesLocationHelper
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


    class MapViewModel(private val application: Application,private val repository: IRepository) : ViewModel() {
        private val preferencesLocationHelper: PreferencesLocationHelper = PreferencesLocationHelper(application)

        private val TAG = "MapViewModel"

    // LiveData for location
    private val geocoder = Geocoder(application)

    // Initialize with default location from LocationData singleton
    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> get() = _location

    // LiveData for place name
    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> get() = _placeName

    // LiveData for map mode
    private val _mapMode = MutableLiveData<Int>()
    val mapMode: LiveData<Int> get() = _mapMode

    // LiveData to hold the card settings field background color
        private val _cardSettingsFieldBackgroundLightMode = MutableLiveData<Int>()
        val cardSettingsFieldBackgroundLightModeLiveData: LiveData<Int> get() = _cardSettingsFieldBackgroundLightMode

        //LiveData Chanege icon
        private val _icon = MutableLiveData<Int>()
        val icon: LiveData<Int> get() = _icon

        //LiveDataGetWhterData
        private val _weatherForecastDailyData = MutableStateFlow<ApiResponse<ForecastResponse>>(ApiResponse.Loading)
        val weatherForecastDailyData: StateFlow<ApiResponse<ForecastResponse>> = _weatherForecastDailyData

        //LiveDataGetWhterData
        private val _weatherForecastHourlyData = MutableStateFlow<ApiResponse< ForecastResponse>>(ApiResponse.Loading)
        val weatherForecastHourlyData: StateFlow<ApiResponse<ForecastResponse>> = _weatherForecastHourlyData

        private val _currentWeatherData = MutableStateFlow<ApiResponse<CurrentWeather>>(ApiResponse.Loading)
        val currentWeatherData: StateFlow<ApiResponse<CurrentWeather>> = _currentWeatherData

        private val _weatherForecast16DailyData = MutableStateFlow<ApiResponse<WeatherResponse>>(ApiResponse.Loading)
        val weatherForecast16DailyData: StateFlow<ApiResponse<WeatherResponse>> = _weatherForecast16DailyData


        // Initialize with default location
        init {
            val defaultName = preferencesLocationHelper.getLocationName()
            val defaultLatitude = preferencesLocationHelper.getLocationLatitude()
            val defaultLongitude = preferencesLocationHelper.getLocationLongitude()
            // Update the LocationData singleton
            _location.value = LatLng(defaultLatitude, defaultLongitude)
            _placeName.value = defaultName ?: "Unknown Place"
        }

        fun fetchWeatherForecastDailyData(apiKey: String, lat: Double, lon: Double) {
            viewModelScope.launch {
                try {
                    _weatherForecastDailyData.value = ApiResponse.Loading

                     repository.getForecastByLatLon(lat, lon, apiKey, null, null).collect{data ->
                         _weatherForecastDailyData.value = ApiResponse.Success(data)
                     }
                } catch (e: Exception) {
                    _weatherForecastDailyData.value = ApiResponse.Error(e.message ?: "Unknown error")
                }
            }
        }

        fun fetchWeatherForecastHourlyData(apiKey: String, lat: Double, lon: Double) {
            viewModelScope.launch {
                try {
                    _weatherForecastHourlyData.value = ApiResponse.Loading
                    repository.getHourlyForecastByLatLon(lat, lon, apiKey, null, null).collect{data->
                        _weatherForecastHourlyData.value = ApiResponse.Success(data)

                    }
                } catch (e: Exception) {
                    _weatherForecastHourlyData.value = ApiResponse.Error(e.message ?: "Unknown error")
                }
            }
        }
        fun fetchCurrentWeatherData(apiKey: String, lat: Double, lon: Double) {
            viewModelScope.launch {
                try {
                    _currentWeatherData.value = ApiResponse.Loading
                    val data = repository.getCurrenWeatherByLatLon(lat, lon, apiKey, null, null)
                    _currentWeatherData.value = ApiResponse.Success(data)
                } catch (e: Exception) {
                    _currentWeatherData.value = ApiResponse.Error(e.message ?: "Unknown error")
                }
            }
        }

        fun featch16DailyWeatherData(apiKey: String, lat: Double, lon: Double) {
            viewModelScope.launch {
                try {
                    _weatherForecast16DailyData.value = ApiResponse.Loading
                   repository.getForecastDailyByLatLon(lat, lon, apiKey, null, null).collect{
                       data->
                       _weatherForecast16DailyData.value = ApiResponse.Success(data)
                   }
                } catch (e: Exception) {
                    _weatherForecast16DailyData.value = ApiResponse.Error(e.message ?: "Unknown error")
                }
            }
        }
    // Method to update the location
    fun updateLocation(latitude: Double, longitude: Double) {
        // Update the LocationData singleton

        _location.value = LatLng(latitude, longitude)
        // Update the LiveData with the default location
        // Update the LiveData with the new location
        //Feactch place name
        fetchPlaceName(latitude, longitude)

    }

    @Suppress("DEPRECATION")
    private fun fetchPlaceName(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0].getAddressLine(0) ?: "Unknown Place"
                    _placeName.postValue(address)
                } else {
                    _placeName.postValue("Unknown Place")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _placeName.postValue("Unknown Place")
            }
        }
    }


    fun getMapStyle(packageName: String,nightModeFlags: Int) {
        val mode = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.map_style_night
            Configuration.UI_MODE_NIGHT_NO ->R.raw.map_style_light
            else -> R.raw.map_style_light
        }
        _mapMode.value = mode
    }


        fun setCardSettingsFieldBackgroundLightMode(packageName: String, nightModeFlags: Int) {
            val colorResId = when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> R.drawable.card_settings_field_background_night_mode
                Configuration.UI_MODE_NIGHT_NO -> R.drawable.card_settings_field_background_light_mode
                else -> R.drawable.card_settings_field_background_light_mode
            }
            _cardSettingsFieldBackgroundLightMode.value = colorResId

        }
        fun setIcon(packageName: String, nightModeFlags: Int) {
            val colorResId = when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> R.drawable.save_location_night_mode
                Configuration.UI_MODE_NIGHT_NO -> R.drawable.save_location_light_mode
                else -> R.drawable.save_location_light_mode
            }
            _icon.value = colorResId
        }

        // Method to save location data
        fun saveLocationData(placeName: String, latitude: Double, longitude: Double) {
            // Here you can save the data to shared preferences or any other storage as needed
            preferencesLocationHelper.saveLocationName(placeName)
            preferencesLocationHelper.saveLocationLatitude(latitude)
            preferencesLocationHelper.saveLocationLongitude(longitude)

        }

}

