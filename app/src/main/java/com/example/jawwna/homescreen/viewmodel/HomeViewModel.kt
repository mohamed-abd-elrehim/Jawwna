package com.example.jawwna.homescreen.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.BuildConfig
import com.example.jawwna.R
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesCurrentLocationHelper
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.DailyForecastData
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.HourlyForecastData
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.PreferencesSettingsHelper
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.LocationDataHolder
import com.example.jawwna.datasource.model.TemperatureResult
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.model.WindResult
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.UnitConvertHelper
import com.example.jawwna.helper.WindSpeedUnits
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel( private val repository: IRepository) :ViewModel() {

    private val TAG = "HomeViewModel"


    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableStateFlow<Int>(0)
    val cardSettingsFieldBackgroundLightModeLiveData: StateFlow<Int> get() = _cardSettingsFieldBackgroundLightMode

    // LiveData to observe theme mode changes
    private val _isDarkMode = MutableStateFlow<Boolean>(false)
    val isDarkMode: StateFlow<Boolean> get() = _isDarkMode



    //LiveDataGetWhterData

    private val _currentWeatherData = MutableStateFlow<ApiResponse<CurrentWeather>>(ApiResponse.Loading)
    val currentWeatherData: StateFlow<ApiResponse<CurrentWeather>> = _currentWeatherData

    //LiveDataGetWhterData
    private val _weatherForecastHourlyData = MutableStateFlow<ApiResponse< ForecastResponse>>(
        ApiResponse.Loading)
    val weatherForecastHourlyData: StateFlow<ApiResponse<ForecastResponse>> = _weatherForecastHourlyData

    //LiveDataGetWhterData
    private val _weatherForecast16DailyData = MutableStateFlow<ApiResponse<WeatherResponse>>(
        ApiResponse.Loading)
    val weatherForecast16DailyData: StateFlow<ApiResponse<WeatherResponse>> = _weatherForecast16DailyData

    // _weatherForecast16DailyRow
    private val _weatherForecast16DailyRow = MutableStateFlow<List<DailyForecastData>>(
        emptyList()
    )
    val weatherForecast16DailyRow: StateFlow<List<DailyForecastData>> = _weatherForecast16DailyRow



    //_weatherForecastHourlyRow
    private val _weatherForecastHourlyRow = MutableStateFlow<List<HourlyForecastData>>(
        emptyList()
    )
    val weatherForecastHourlyRow: StateFlow<List<HourlyForecastData>> = _weatherForecastHourlyRow
    private var weatherResponseEntity: WeatherResponseEntity = WeatherResponseEntity("",emptyList(),emptyList(),emptyList(),0.0,0.0)
    private val _preferencesLocationEum = MutableStateFlow<PreferencesLocationEum>(PreferencesLocationEum.CURRENT)
    private val _isConnctionAvailable = MutableStateFlow<Boolean>(true)
    private val _updateFavoriteWeather = MutableStateFlow<List<LocationDataHolder>>(emptyList())


    private val _current = MutableStateFlow<List<CurrentWeather>>(emptyList())
    val current: StateFlow<List<CurrentWeather>> = _current
    private val _daily = MutableStateFlow<List<WeatherResponse>>(emptyList())
    val daily: StateFlow<List<WeatherResponse>> = _daily
    private val _hourly = MutableStateFlow<List<ForecastResponse>>(emptyList())
    val hourly: StateFlow<List<ForecastResponse>> = _hourly


    fun setMode(preferencesLocationEum: PreferencesLocationEum)
    {
        _preferencesLocationEum.value=preferencesLocationEum
        Log.d(TAG, "setMode: $preferencesLocationEum")
    }
    private fun mapToFavoriteDataLatLong(response: List<WeatherResponseEntity>): MutableList<LocationDataHolder> {
        return response.flatMap { weatherEntity -> // Flatten the list of lists
            weatherEntity.currentWeatherList.map { weatherList -> // Map each weatherList entry
                LocationDataHolder(
                    locationName = weatherEntity.cityName, // Get city name for each entity
                    latitude = weatherList.coord.lat, // Safely retrieves the latitude
                    longitude = weatherList.coord.lon // Safely retrieves the longitude
                )
            }
        }.toMutableList() // Convert to MutableList
    }
    fun getAllWeather() {
        viewModelScope.launch {

            repository.getAllWeatherLocalData().collect { weatherResponseEntity ->
                if (_isConnctionAvailable.value) {
                    Log.d("isConnected2", "onViewCreated: isConnected")
                    updateHelperData()
                }
                _current.value = weatherResponseEntity[0].currentWeatherList
                _daily.value = weatherResponseEntity[0].dailyForecastList
                _hourly.value = weatherResponseEntity[0].hourlyForecastList
                val hourlyForecastDataList = mapToHourlyForecastData(_hourly.value.first())
                _weatherForecastHourlyRow.value = hourlyForecastDataList
                val dailyForecastDataList = mapToDailyForecastData(_daily.value.first())
                _weatherForecast16DailyRow.value = dailyForecastDataList


                Log.d(TAG, "getAllWeather: ${weatherResponseEntity}")
            }
        }
    }





    fun setIsConnectionAvailable(isConnected: Boolean) {
        _isConnctionAvailable.value = isConnected
    }
    fun updateHelperData(){
        featch16DailyWeatherData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
        fetchCurrentWeatherData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
        fetchWeatherForecastHourlyData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
       viewModelScope.launch {
           delay(2000)
           insertWeatherResponseEntity()
       }
    }



    fun fetchWeatherForecastHourlyData(apiKey: String) {
        viewModelScope.launch {
            try {
                repository.execute(_preferencesLocationEum.value)
                val lat = repository.getLocationLatitude()
                
                val lon = repository.getLocationLongitude()

                _weatherForecastHourlyData.value = ApiResponse.Loading
                if (lat != null && lon != null) {
                    repository.getHourlyForecastByLatLon(lat, lon, apiKey, null, null).collect{data->
                        val hourlyForecastDataList = mapToHourlyForecastData(data)
                        _weatherForecastHourlyRow.value = hourlyForecastDataList
                        Log.d(TAG, "fetchWeatherForecastHourlyData: $hourlyForecastDataList")

                        _weatherForecastHourlyData.value = ApiResponse.Success(data)
                        weatherResponseEntity.hourlyForecastList= listOf(data)

                    }
                }
            } catch (e: Exception) {
                _weatherForecastHourlyData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun fetchCurrentWeatherData(apiKey: String) {
        viewModelScope.launch {
            try {
                repository.execute(_preferencesLocationEum.value)
                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()


                _currentWeatherData.value = ApiResponse.Loading
                val data = lat?.let { latitude ->
                    lon?.let { longitude ->
                        repository.getCurrenWeatherByLatLon(latitude, longitude, apiKey, null, null)
                    }
                }
                _currentWeatherData.value = ApiResponse.Success(data!!)
                repository.setOldTemperatureUnit(TemperatureUnits.metric.toString())
                repository.setOldWindSpeedUnit(WindSpeedUnits.metric.toString())

                weatherResponseEntity.currentWeatherList= listOf(data)
                weatherResponseEntity.cityName=data.name

            } catch (e: Exception) {
                _currentWeatherData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun featch16DailyWeatherData(apiKey: String) {
        viewModelScope.launch {
            try {
                _weatherForecast16DailyData.value = ApiResponse.Loading
                repository.execute(_preferencesLocationEum.value)

                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()

                if (lat != null && lon != null) {
                    repository.getForecastDailyByLatLon(lat, lon, apiKey, null, null).collect{ data->

                        val dailyForecastDataList = mapToDailyForecastData(data)
                        _weatherForecast16DailyRow.value = dailyForecastDataList
                        weatherResponseEntity.dailyForecastList= listOf(data)

                        Log.d(TAG, "featch16DailyWeatherData: $dailyForecastDataList")

                        _weatherForecast16DailyData.value = ApiResponse.Success(data)
                    }
                }
            } catch (e: Exception) {
                _weatherForecast16DailyData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

//    fun getAllCurrentWeather() {
//        viewModelScope.launch {
//            repository.getAllWeatherLocalData().collect { favoriteWeatherList ->
//                if (_isConnctionAvailable.value) {
////                    _updateFavoriteWeather.value= mapToFavoriteDataLatLong(favoriteWeatherList)
////                    Log.d(TAG, "_updateFavoriteWeather: ${_updateFavoriteWeather.value}")
////                    fetchCurrentWeatherDataUbdate(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
////                    fetchWeatherForecastHourlyDataUbdate(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
////                    featch16DailyWeatherDataUbdate(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
////                    insertWeatherResponseEntityUbdate()
//                }
//                _weatherFavoriteRow.value = mapToFavoriteDatav2(favoriteWeatherList)
//            }
//        }
//    }

    fun insertWeatherResponseEntity()
    {
        if (!weatherResponseEntity.currentWeatherList.isEmpty()&&!weatherResponseEntity.dailyForecastList.isEmpty()&&!weatherResponseEntity.hourlyForecastList.isEmpty()) {
            viewModelScope.launch {
                repository.execute(_preferencesLocationEum.value)
                repository.deleteAllWeatherLocalData()
                weatherResponseEntity.latitude= repository.getLocationLatitude()!!
                weatherResponseEntity.longitude= repository.getLocationLongitude()!!
                Log.d(TAG, "insertWeatherResponseEntity: $weatherResponseEntity")
                repository.insertWeatherLocalData(weatherResponseEntity)
            }
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



    //GetFuture17DayNames
    private fun mapToDailyForecastData(response: WeatherResponse): List<DailyForecastData> {
        val dayNames = getFutureDayNames() // Get the future day names

        return response.list

            .take(7) // Take the next 7 elements
            .mapIndexed { index, weatherList ->
                DailyForecastData(
                    dayName = dayNames.getOrNull(index), // Get the corresponding day name (index + 1)
                    icon = weatherList.weather.firstOrNull()?.icon,
                    description = weatherList.weather.firstOrNull()?.description,
                    tempMax = checkTemperatureUnit(weatherList.temp.max),
                    tempMin = checkTemperatureUnit(weatherList.temp.min)

                    //tempUnit = "°K"
                )
            }
            .toList() // Explicitly convert to List
    }



    // Function to get future day names using Calendar, skipping the current day
    private fun getFutureDayNames(daysCount: Int = 7): List<String> {
        val calendar = Calendar.getInstance()
        val dayNames = mutableListOf<String>()

        // Start from the next day
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        for (i in 0 until daysCount) { // Iterate for all daysCount
            dayNames.add(SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        Log.i("dayNames", "$dayNames")
        return dayNames
    }




    fun checkTemperatureUnit(temp: Double): TemperatureResult {
        val temperatureUnit = repository.getTemperatureUnit()
        val convertedTemperature = when (temperatureUnit) {
            TemperatureUnits.metric.toString() -> UnitConvertHelper.convertTemperature(temp, repository.getOldTemperatureUnit(), TemperatureUnits.metric)
            TemperatureUnits.imperial.toString() -> UnitConvertHelper.convertTemperature(temp, repository.getOldTemperatureUnit(), TemperatureUnits.imperial)
            else -> UnitConvertHelper.convertTemperature(temp, repository.getOldTemperatureUnit(), TemperatureUnits.standard)
        }

        val unit = when (temperatureUnit) {
            TemperatureUnits.metric.toString() -> "°C"
            TemperatureUnits.imperial.toString() -> "°F"
            else -> "°K"
        }

        return TemperatureResult(convertedTemperature, unit)
    }


    fun checkWindSpeedUnit(windSpeed:Double): WindResult {
        val windUnit = repository.getWindSpeedUnit()

        val convertedWindSpeed = when (windUnit) {
            WindSpeedUnits.imperial.toString() ->  UnitConvertHelper.convertWindSpeed(windSpeed,repository.getOldWindSpeedUnit(),
                WindSpeedUnits.imperial)
            WindSpeedUnits.metric.toString() -> UnitConvertHelper.convertWindSpeed(windSpeed,repository.getOldWindSpeedUnit(),
                WindSpeedUnits.metric)
            else -> UnitConvertHelper.convertWindSpeed(windSpeed,repository.getOldWindSpeedUnit(),WindSpeedUnits.imperial)
        }
        val unit = when (windUnit) {
            WindSpeedUnits.metric.toString() -> "m/s"
            else -> "mph"
        }
        return WindResult(convertedWindSpeed, unit)

    }

    // Function to map CurrentWeather to HourlyForecastData
    private fun mapToHourlyForecastData(response: ForecastResponse): List<HourlyForecastData> {
        // Map the list of CurrentWeather to HourlyForecastData
        return response.list.map { currentWeather ->
            // Convert dt_txt to 12-hour time format with AM/PM
            val time = formatTimeFromDateString(currentWeather.dt_txt)
            // Create and return HourlyForecastData object
            HourlyForecastData(
                icon = currentWeather.weather?.firstOrNull()?.icon,
                Time = time, // Converted time with AM/PM
                temp = currentWeather.main?.temp?.let { checkTemperatureUnit(it) }!!,
                //tempUnit = "°K" // Assuming Kelvin (convert if needed)
            )
        }
    }

    // Function to convert date string to formatted time (AM/PM)
    fun formatTimeFromDateString(dateString: String?): String? {
        // Define date formats for input and output
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return dateString?.let { dt ->
            inputDateFormat.parse(dt)?.let { date ->
                outputTimeFormat.format(date) // Convert to 12-hour time with AM/PM
            }
        }
    }

    // Function to get the current date as a string
    fun getCurrentDate(): String {
        // Define the date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Get the current date
        val currentDate = Calendar.getInstance().time
        // Return the formatted date
        return dateFormat.format(currentDate)
    }


    // Function to get the current time in AM/PM format
    fun getCurrentTime(): String {
        // Define the time format
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        // Get the current time
        val currentTime = Calendar.getInstance().time
        // Return the formatted time
        return timeFormat.format(currentTime)
    }






    /*
        @RequiresApi(Build.VERSION_CODES.O) // Ensure you're using Android API level 26+
        fun getFutureDayNames(): List<String> {
            val currentDate = LocalDate.now()
            val dayNamesList = mutableListOf<String>()

            for (i in 1..16) { // Start from 1 to skip the current day and include the next 16 days
                val futureDate = currentDate.plusDays(i.toLong())
                val dayName = futureDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                dayNamesList.add(dayName)
            }

            return dayNamesList
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun mapToDailyForecastData(response: WeatherResponse): List<DailyForecastData> {
            val dayNames = getFutureDayNames() // Get the future day names

            return response.list.mapIndexed { index, weatherList ->
                DailyForecastData(
                    dayName = dayNames.getOrNull(index), // Get the corresponding day name
                    icon = weatherList.weather.firstOrNull()?.icon,
                    description = weatherList.weather.firstOrNull()?.description,
                    tempMax = weatherList.temp.max,
                    tempMin = weatherList.temp.min,
                    tempUnit = "°K"
                )
            }.toList() // Explicitly convert to List
        }
    */


}
