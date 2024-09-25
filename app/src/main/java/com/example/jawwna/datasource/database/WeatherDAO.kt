package com.example.jawwna.datasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
//import com.example.jawwna.datasource.model.CurrentWeather
//import com.example.jawwna.datasource.model.FavoriteLocation
//import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {


    // Current location weather operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherLocalData(currentWeather: WeatherResponseEntity)

    @Query("SELECT * FROM WeatherLocalData WHERE cityName = :cityName")
    suspend fun getWeatherLocalData(cityName: String): WeatherResponseEntity?

    @Query("DELETE FROM WeatherLocalData WHERE cityName = :cityName")
    suspend fun deleteWeatherLocalData(cityName: String)

    @Query("DELETE FROM WeatherLocalData")
    suspend fun deleteAllWeatherLocalData()

    @Query("SELECT * FROM WeatherLocalData")
    fun getAllWeatherLocalData(): Flow<List<WeatherResponseEntity>>


    // Favorite locations operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeatherEntity)

    @Query("SELECT * FROM FavoriteWeatherEntity WHERE cityName = :cityName")
    suspend fun getFavoriteWeather(cityName: String): FavoriteWeatherEntity?

    @Query("SELECT * FROM FavoriteWeatherEntity")
    fun getAllFavoriteWeather(): Flow<List<FavoriteWeatherEntity>>
    @Delete
    suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeatherEntity)
    @Query("DELETE FROM FavoriteWeatherEntity")
    suspend fun deleteAllFavoriteWeather()

    @Query("DELETE FROM FavoriteWeatherEntity WHERE cityName = :cityName")
    suspend fun deleteFavoriteWeatherByCityName(cityName: String)



}
