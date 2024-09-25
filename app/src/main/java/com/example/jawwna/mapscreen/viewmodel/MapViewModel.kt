package com.example.jawwna.mapscreen.viewmodel
// MapViewModel.kt
import android.content.res.Configuration
import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.mapscreen.geocodingservice.IGeocodingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


class MapViewModel(private val repository: IRepository) : ViewModel() {
    private val TAG = "MapViewModel"



    // Initialize with default location from LocationData singleton
    private val _location = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng> get() = _location


    // Initialize with default location from LocationData singleton
    private val _searchLocation = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val searchLocation: StateFlow<LatLng> get() = _searchLocation

    // LiveData for place name
    private val _placeName = MutableStateFlow<String>("")
    val placeName: StateFlow<String> get() = _placeName

    // LiveData for map mode
    private val _mapMode = MutableStateFlow<Int>(0)
    val mapMode: StateFlow<Int> get() = _mapMode

    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableStateFlow<Int>(0)
    val cardSettingsFieldBackgroundLightModeLiveData: StateFlow<Int> get() = _cardSettingsFieldBackgroundLightMode

    //LiveData Chanege icon
    private val _icon = MutableStateFlow<Int>(0)
    val icon: StateFlow<Int> get() = _icon




    // Initialize with default location
    init {
        repository.execute(PreferencesLocationEum.MAP)
        val defaultName = repository.getLocationName()
        val defaultLatitude = repository.getLocationLatitude()
        val defaultLongitude = repository.getLocationLongitude()
        // Update the LocationData singleton
        _location.value = LatLng(defaultLatitude, defaultLongitude)
        _placeName.value = defaultName ?: "Unknown Place"

    }


    // Method to update the location
    fun updateLocation(latitude: Double, longitude: Double) {
        // Update the LocationData singleton
        _location.value = LatLng(latitude, longitude)
        // Update the LiveData with the default location
        // Update the LiveData with the new location
        //Feactch place name
        fetchPlaceName(latitude, longitude)
        repository.execute(PreferencesLocationEum.MAP)
        repository.saveLocationLatitude(latitude)
        repository.saveLocationLongitude(longitude)
        repository.saveLocationName(_placeName.value.toString())


    }

    @Suppress("DEPRECATION")
    fun fetchPlaceName(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val name = repository.getCountryNameFromLatLong(latitude, longitude)
            _placeName.value = name ?: "Unknown Place"
        }
    }



    fun searchPlace (qury: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchPlace(qury).collect {
                if (it != null) {

                    _searchLocation.value = it

                }
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
    fun saveLocationData(placeName: String, latitude: Double, longitude: Double,mode:PreferencesLocationEum) {
        when (mode) {
            PreferencesLocationEum.FAVOURITE -> {
                repository.execute(mode)
                repository.saveLocationLatitude(latitude)
                repository.saveLocationLongitude(longitude)
                repository.saveLocationName(placeName)
            }
            PreferencesLocationEum.CURRENT -> {
                repository.execute(mode)
                repository.saveLocationLatitude(latitude)
                repository.saveLocationLongitude(longitude)
                repository.saveLocationName(placeName)
            }
            else -> {
              println("")

            }

        }
        // Here you can save the data to shared preferences or any other storage as needed

    }

}