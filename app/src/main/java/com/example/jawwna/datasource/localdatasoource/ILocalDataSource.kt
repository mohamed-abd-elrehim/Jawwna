package com.example.jawwna.datasource.localdatasoource

import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
     suspend fun  insertWeatherLocalData(currentWeather: WeatherResponseEntity)
     suspend fun  getWeatherLocalData(cityName: String): WeatherResponseEntity?
     suspend fun  deleteWeatherLocalData(cityName: String)
     suspend fun  deleteAllWeatherLocalData()
     fun getAllWeatherLocalData(): Flow<List<WeatherResponseEntity>>

     suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeatherEntity)
     suspend fun getFavoriteWeather(cityName: String): FavoriteWeatherEntity?
     fun getAllFavoriteWeather(): Flow<List<FavoriteWeatherEntity>>
     suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeatherEntity)
     suspend fun deleteAllFavoriteWeather()
     suspend fun deleteFavoriteWeatherByCityName(cityName: String)


}