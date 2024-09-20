package com.example.jawwna.mapscreen.viewmodel
// MapViewModel.kt
import android.app.Application
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.model.LocationData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class MapViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for location
    private val geocoder = Geocoder(application)

    // Initialize with default location from LocationData singleton
    private val _location = MutableLiveData(LocationData.getCurrentLatLng())
    val location: LiveData<LatLng> get() = _location

    // LiveData for place name
    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> get() = _placeName

    // LiveData for map mode
    private val _mapMode = MutableLiveData<Int>()
    val mapMode: LiveData<Int> get() = _mapMode


    // Method to update the location
    fun updateLocation(latitude: Double, longitude: Double) {
        // Update the LocationData singleton
        LocationData.updateLocation(latitude, longitude)
        // Update the LiveData with the new location

        _location.value = LatLng(latitude, longitude)

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
                LocationData.updatePlaceName(placeName.value.toString())
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
//    fun getMapStyle(packageName: String,nightModeFlags: Int) {
//        val mode = when (nightModeFlags) {
//            Configuration.UI_MODE_NIGHT_YES ->  Uri.parse("android.resource://$packageName/${R.raw.map_style_night}")
//            Configuration.UI_MODE_NIGHT_NO ->Uri.parse("android.resource://$packageName/${R.raw.map_style_light}")
//            else -> Uri.parse("android.resource://$packageName/${R.raw.map_style_light}")
//        }
//        _mapMode.value = mode
//    }


}

