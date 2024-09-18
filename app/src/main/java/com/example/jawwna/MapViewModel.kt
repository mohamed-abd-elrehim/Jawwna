package com.example.jawwna

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    // LiveData to store map data, such as location
    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> = _location

    init {
        // Initialize with some default location (Sydney in this case)
        _location.value = LatLng(-34.0, 151.0)
    }

    // Function to update location
    fun setLocation(lat: Double, lng: Double) {
        _location.value = LatLng(lat, lng)
    }
}
