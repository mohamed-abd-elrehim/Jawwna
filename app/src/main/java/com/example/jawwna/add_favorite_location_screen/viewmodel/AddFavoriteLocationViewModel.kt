package com.example.jawwna.add_favorite_location_screen.viewmodel

import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteLocationModel
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.UnitConvertHelper
import com.example.jawwna.helper.WindSpeedUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddFavoriteLocationViewModel (private val repository: IRepository) : ViewModel() {


    private val TAG = "AddFavoriteLocationViewModel"

    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableStateFlow<Int>(0)
    val cardSettingsFieldBackgroundLightModeLiveData: StateFlow<Int> get() = _cardSettingsFieldBackgroundLightMode

    //LiveData Chanege icon
    private val _icon = MutableStateFlow<Int>(0)
    val icon: StateFlow<Int> get() = _icon

    private val _forecastList = MutableStateFlow<MutableList<FavoriteLocationModel>>(mutableListOf())
    val forecastList: StateFlow<MutableList<FavoriteLocationModel>> get() = _forecastList

    fun addFavoriteLocation(location: FavoriteLocationModel) {
        _forecastList.value.add(location) // Add the new location
        _forecastList.value = _forecastList.value // Trigger re-emission of StateFlow
    }


    //LiveDataGetWhterData

    private val _currentWeatherData = MutableStateFlow<ApiResponse<CurrentWeather>>(ApiResponse.Loading)
    val currentWeatherData: StateFlow<ApiResponse<CurrentWeather>> = _currentWeatherData

    //LiveDataGetWhterData
    private val _weatherForecastHourlyData = MutableStateFlow<ApiResponse<ForecastResponse>>(
        ApiResponse.Loading)
    val weatherForecastHourlyData: StateFlow<ApiResponse<ForecastResponse>> = _weatherForecastHourlyData

    //LiveDataGetWhterData
    private val _weatherForecast16DailyData = MutableStateFlow<ApiResponse<WeatherResponse>>(
        ApiResponse.Loading)
    val weatherForecast16DailyData: StateFlow<ApiResponse<WeatherResponse>> = _weatherForecast16DailyData


    // _weatherForecast16DailyRow
    private val _weatherFavoriteRow = MutableStateFlow< MutableList<FavoriteLocationModel>>(
        mutableListOf()
    )
    val weatherFavoriteRow: StateFlow< MutableList<FavoriteLocationModel>> = _weatherFavoriteRow

    private var favoriteWeatherEntity: FavoriteWeatherEntity = FavoriteWeatherEntity("",emptyList(),emptyList(),emptyList(),0.0,0.0)

    fun fetchWeatherForecastHourlyData(apiKey: String) {
        viewModelScope.launch {
            try {
                repository.execute(PreferencesLocationEum.FAVOURITE)
                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()

                _weatherForecastHourlyData.value = ApiResponse.Loading
                repository.getHourlyForecastByLatLon(lat, lon, apiKey, null, null).collect{data->


                    _weatherForecastHourlyData.value = ApiResponse.Success(data)
                    favoriteWeatherEntity.hourlyForecastList= listOf(data)

                }
            } catch (e: Exception) {
                _weatherForecastHourlyData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun fetchCurrentWeatherData(apiKey: String) {
        viewModelScope.launch {
            try {
                repository.execute(PreferencesLocationEum.FAVOURITE)
                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()


                _currentWeatherData.value = ApiResponse.Loading
                val data = repository.getCurrenWeatherByLatLon(lat, lon, apiKey, null, null)
               /* val favoriteLocationModelDataList = mapToFavoriteData(data)
                _weatherFavoriteRow.value=favoriteLocationModelDataList

                Log.d(TAG, "_weatherFavoriteRow: $favoriteLocationModelDataList")
*/

                _currentWeatherData.value = ApiResponse.Success(data)
                repository.setOldTemperatureUnit(TemperatureUnits.metric.toString())
                repository.setOldWindSpeedUnit(WindSpeedUnits.metric.toString())

                favoriteWeatherEntity.currentWeatherList= listOf(data)

            } catch (e: Exception) {
                _currentWeatherData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun featch16DailyWeatherData(apiKey: String) {
        viewModelScope.launch {
            try {
                _weatherForecast16DailyData.value = ApiResponse.Loading
                repository.execute(PreferencesLocationEum.FAVOURITE)

                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()

                repository.getForecastDailyByLatLon(lat, lon, apiKey, null, null).collect{
                        data->

                    favoriteWeatherEntity.dailyForecastList= listOf(data)
                    _weatherForecast16DailyData.value = ApiResponse.Success(data)
                }
            } catch (e: Exception) {
                _weatherForecast16DailyData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun mapToFavoriteData(response: CurrentWeather): MutableList<FavoriteLocationModel> {
        // Assuming there's only one weather entry; otherwise, handle accordingly
        val weather = response.weather.firstOrNull()

        // Create the FavoriteLocationModel object
        val favoriteLocationModel = FavoriteLocationModel(
            placeName = response.name,
            icon = weather?.icon,
            description = weather?.description,
            temp = response.main.temp.let { checkTemperatureUnit(it) } // Convert temperature as needed
        )

        // Return a list containing the favoriteLocationModel
        return mutableListOf(favoriteLocationModel)
    }

    private fun mapToFavoriteDatav2(response: List<FavoriteWeatherEntity>): MutableList<FavoriteLocationModel> {
        return response.flatMap { weatherEntity -> // Iterate through each FavoriteWeatherEntity
            weatherEntity.currentWeatherList.map { weatherList -> // Map each weatherList entry
                FavoriteLocationModel(
                    placeName = weatherEntity.cityName, // Get city name for each entity
                    icon = weatherList.weather.firstOrNull()?.icon, // Safely retrieves the first weather icon
                    description = weatherList.weather.firstOrNull()?.description, // Safely retrieves the description
                    temp = checkTemperatureUnit(weatherList.main.temp) // Applies unit conversion/check to temperature
                )
            }
        }.toMutableList() // Convert to MutableList
    }


    fun getAllFavoriteWeather() {
        viewModelScope.launch {
            repository.getAllFavoriteWeather().collect { favoriteWeatherList ->

                _weatherFavoriteRow.value = mapToFavoriteDatav2(favoriteWeatherList)
            }
        }
    }

    fun deleteFavoriteWeather(favoriteLocationModel: FavoriteLocationModel) {
        viewModelScope.launch {
            val favoriteWeatherEntity = favoriteLocationModel.placeName?.let {
                repository.getFavoriteWeather(
                    it
                )
            }
            if (favoriteWeatherEntity != null) {
                repository.deleteFavoriteWeather(favoriteWeatherEntity)
            }
        }
    }

    fun insertWeatherResponseEntity() {
        if (favoriteWeatherEntity.currentWeatherList.isNotEmpty() &&
            favoriteWeatherEntity.dailyForecastList.isNotEmpty() &&
            favoriteWeatherEntity.hourlyForecastList.isNotEmpty()) {
            viewModelScope.launch {
                repository.execute(PreferencesLocationEum.FAVOURITE)
                favoriteWeatherEntity.cityName = repository.getLocationName().toString()
                favoriteWeatherEntity.latitude = repository.getLocationLatitude()
                favoriteWeatherEntity.longitude = repository.getLocationLongitude()

                // Log the entity before insertion for debugging
                Log.d(TAG, "Inserting FavoriteWeatherEntity: $favoriteWeatherEntity")

                repository.insertFavoriteWeather(favoriteWeatherEntity)
            }
        } else {
            Log.d(TAG, "FavoriteWeatherEntity lists are not fully populated")
        }
    }


    fun checkTemperatureUnit(temp:Double): String {
        return when (repository.getTemperatureUnit()) {
            TemperatureUnits.metric.toString() ->return "${UnitConvertHelper.convertTemperature(temp,repository.getOldTemperatureUnit(),TemperatureUnits.metric)}°C"
            TemperatureUnits.imperial.toString()->return "${UnitConvertHelper.convertTemperature(temp,repository.getOldTemperatureUnit(),TemperatureUnits.imperial)}°F"
            else -> "${UnitConvertHelper.convertTemperature(temp,repository.getOldTemperatureUnit(),TemperatureUnits.standard)}°K"
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
    fun setIcon(packageName: String, nightModeFlags: Int) {
        val colorResId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.drawable.add_to_favorites_night_mode
            Configuration.UI_MODE_NIGHT_NO -> R.drawable.add_to_favorites_light_mode
            else -> R.drawable.add_to_favorites_light_mode
        }
        _icon.value = colorResId
    }

}