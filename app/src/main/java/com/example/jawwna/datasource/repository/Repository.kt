package com.example.jawwna.datasource.repository

import android.app.Application
import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.example.jawwna.datasource.localdatasoource.LocalDataSource
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.IPreferencesLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesCurrentLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesFavLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.IPreferencesSettingsHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.PreferencesSettingsHelper
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.remotedatasource.RemoteDataSource
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.UpdateLocale
import com.example.jawwna.mapscreen.geocodingservice.GeocodingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class Repository private constructor(val application: Application) : IRepository {

    private lateinit var iPreferencesSettingsHelper: IPreferencesSettingsHelper
    private lateinit var iPreferencesLocationHelper: IPreferencesLocationHelper
    private val geocodingService = GeocodingService(application)

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(application: Application): Repository {
            return INSTANCE ?: synchronized(this) {
                Repository(application).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
        LocalDataSource.init(application)
        iPreferencesSettingsHelper = PreferencesSettingsHelper(application)

    }

    // Implement the repository methods here
    // Implement the RemoteDataSource methods here
    override suspend fun getCurrenWeatherByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return RemoteDataSource.getCurrenWeatherByLatLon(lat, lon, apiKey, getLanguageCode(), units)
    }

    override suspend fun getCurrenWeatherByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return RemoteDataSource.getCurrenWeatherByCityName(cityName, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getCurrenWeatherByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return RemoteDataSource.getCurrenWeatherByCityAndCountry(query, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return RemoteDataSource.getForecastByLatLon(lat, lon, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return RemoteDataSource.getForecastByCityName(cityName, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return RemoteDataSource.getForecastByCityAndCountry(query, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getHourlyForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return RemoteDataSource.getHourlyForecastByLatLon(lat, lon, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getHourlyForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return RemoteDataSource.getHourlyForecastByCityName(cityName, apiKey,  getLanguageCode(), units)
    }

    override suspend fun getHourlyForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return RemoteDataSource.getHourlyForecastByCityAndCountry(query, apiKey,  getLanguageCode(), units)
    }


    //16-day-forecast data
    override suspend fun getForecastDailyByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<WeatherResponse> {
        return RemoteDataSource.getForecastDailyByLatLon(lat, lon, apiKey,  getLanguageCode(), units)

    }

    override suspend fun insertWeatherLocalData(currentWeather: WeatherResponseEntity) {
        LocalDataSource.insertWeatherLocalData(currentWeather)
    }

    override suspend fun getWeatherLocalData(cityName: String): WeatherResponseEntity? {
        return LocalDataSource.getWeatherLocalData(cityName)
    }

    override suspend fun deleteWeatherLocalData(cityName: String) {
        LocalDataSource.deleteWeatherLocalData(cityName)
    }

    override suspend fun deleteAllWeatherLocalData() {
        LocalDataSource.deleteAllWeatherLocalData()
    }

    override fun getAllWeatherLocalData(): Flow<List<WeatherResponseEntity>> {
        return LocalDataSource.getAllWeatherLocalData()
    }

    override suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        LocalDataSource.insertFavoriteWeather(favoriteWeather)
    }

    override suspend fun getFavoriteWeather(cityName: String): FavoriteWeatherEntity? {
        return LocalDataSource.getFavoriteWeather(cityName)
    }

    override fun getAllFavoriteWeather(): Flow<List<FavoriteWeatherEntity>> {
        return LocalDataSource.getAllFavoriteWeather()
    }

    override suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        LocalDataSource.deleteFavoriteWeather(favoriteWeather)
    }

    override suspend fun deleteAllFavoriteWeather() {
        LocalDataSource.deleteAllFavoriteWeather()
    }

    override suspend fun deleteFavoriteWeatherByCityName(cityName: String) {
        LocalDataSource.deleteFavoriteWeatherByCityName(cityName)
    }

    // save settings

    override fun saveGetLocationMode(mode: String) {
        iPreferencesSettingsHelper.saveGetLocationMode(mode)
    }

    override fun getGetLocationMode(): String? {
        return iPreferencesSettingsHelper.getGetLocationMode()
    }

    override fun clearGetLocationMode() {
        iPreferencesSettingsHelper.clearGetLocationMode()
    }

    override fun setOldTemperatureUnit(unit: String) {
        iPreferencesSettingsHelper.setOldTemperatureUnit(unit)
    }

    override fun setOldWindSpeedUnit(unit: String) {
        iPreferencesSettingsHelper.setOldWindSpeedUnit(unit)
    }

    override fun saveTemperatureUnit(unit: String) {
        iPreferencesSettingsHelper.saveTemperatureUnit(unit)
    }

    override fun getTemperatureUnit(): String? {
        return iPreferencesSettingsHelper.getTemperatureUnit()
    }

    override fun getOldTemperatureUnit(): String? {
        return iPreferencesSettingsHelper.getOldTemperatureUnit()
    }

    override fun clearTemperatureUnit() {
        iPreferencesSettingsHelper.clearTemperatureUnit()
    }

    override fun getOldWindSpeedUnit(): String? {
        return iPreferencesSettingsHelper.getOldWindSpeedUnit()
    }

    override fun saveWindSpeedUnit(unit: String) {
        iPreferencesSettingsHelper.saveWindSpeedUnit(unit)
    }

    override fun getWindSpeedUnit(): String? {
        return iPreferencesSettingsHelper.getWindSpeedUnit()
    }

    override fun clearWindSpeedUnit() {
        iPreferencesSettingsHelper.clearWindSpeedUnit()
    }

    override fun saveLanguage(language: String) {
        iPreferencesSettingsHelper.saveLanguage(language)
    }

    override fun getLanguage(): String? {
        return iPreferencesSettingsHelper.getLanguage()
    }

    override fun clearLanguage() {
        iPreferencesSettingsHelper.clearLanguage()
    }

    override fun saveTheme(theme: String) {
        iPreferencesSettingsHelper.saveTheme(theme)
    }

    override fun getTheme(): String? {
        return iPreferencesSettingsHelper.getTheme()
    }

    override fun clearTheme() {
        iPreferencesSettingsHelper.clearTheme()
    }

    override fun saveNotifications(status: String) {
        iPreferencesSettingsHelper.saveNotifications(status)
    }

    override fun getNotifications(): String? {
        return iPreferencesSettingsHelper.getNotifications()
    }

    override fun clearNotifications() {
        iPreferencesSettingsHelper.clearNotifications()
    }

    override fun clearAllSettings() {
        iPreferencesSettingsHelper.clearAllSettings()
    }

    override fun resetSettings() {
        iPreferencesSettingsHelper.resetSettings()
    }


    override fun execute(preferencesLocationEum: PreferencesLocationEum) {
        when (preferencesLocationEum) {
            PreferencesLocationEum.FAVOURITE -> iPreferencesLocationHelper =
                PreferencesFavLocationHelper(application)
            // Calls ClassA's implementation
            PreferencesLocationEum.CURRENT -> iPreferencesLocationHelper =
                PreferencesCurrentLocationHelper(application)
            // Calls ClassC's implementation
            else -> iPreferencesLocationHelper = PreferencesLocationHelper(application)

        }
    }


    override fun saveLocationName(name: String) {
          iPreferencesLocationHelper.saveLocationName(name)

    }

    override fun getLocationName(): String? {
        return iPreferencesLocationHelper.getLocationName()

    }

    override fun clearLocationName(){
         iPreferencesLocationHelper.clearLocationName()

    }

    override fun saveLocationLatitude(latitude: Double){
          iPreferencesLocationHelper.saveLocationLatitude(latitude)

    }

    override fun getLocationLatitude(): Double {
        return iPreferencesLocationHelper.getLocationLatitude()

    }

    override fun clearLocationLatitude(){
         iPreferencesLocationHelper.clearLocationLatitude()

    }

    override fun saveLocationLongitude(longitude: Double) {
          iPreferencesLocationHelper.saveLocationLongitude(longitude)

    }

    override fun clearLocationLongitude(){
         iPreferencesLocationHelper.clearLocationLongitude()

    }

    override fun getLocationLongitude(): Double {
        return  iPreferencesLocationHelper.getLocationLongitude()

    }

    override fun clearAllLocation() {
         iPreferencesLocationHelper.clearAllLocation()


    }

    override fun searchPlace(query: String): Flow<LatLng?> {
        return geocodingService.searchPlace(query)
    }

    override suspend fun getCountryNameFromLatLong(latitude: Double, longitude: Double): String? {
        return geocodingService.getCountryNameFromLatLong(latitude, longitude)

    }

    override fun getLanguageCode(): String {
        return when (getLanguage()) {
            "en" -> "en"
            "ar" -> "ar"
            else -> "en"
        }
    }




}