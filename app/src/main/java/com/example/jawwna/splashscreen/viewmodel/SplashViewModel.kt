package com.example.jawwna.splashscreen.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.helper.PreferencesLocationEum
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: IRepository) : ViewModel() {
    private val TAG = "SplashViewModel"

    private val _animationResource = MutableStateFlow<Int>(0)
    val animationResource: StateFlow<Int> get() = _animationResource

    private val _navigateToMainActivity = MutableStateFlow<Boolean>(false)
    val navigateToMainActivity: StateFlow<Boolean> get() = _navigateToMainActivity

    private val _updateLocale = MutableStateFlow<String>("")
    val updateLocale: StateFlow<String> get() = _updateLocale

    private val _isCurrenLocationAvailable = MutableStateFlow<Boolean>(false)
    val isCurrenLocationAvailable: StateFlow<Boolean> get() = _isCurrenLocationAvailable
    private val currenLocationName = MutableStateFlow<String?>("")

    

    fun IsCurrenLocationAvailable() {
        repository.execute(PreferencesLocationEum.CURRENT)
        _isCurrenLocationAvailable.value = repository.isLocationSaved()
    }

    fun setUpdateLocale(language: String) {
        _updateLocale.value = repository.getLanguage()!!
    }


    fun setAnimationResource(nightModeFlags: Int) {
        val resource = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.nightmodesplashanim // Dark mode animation
            Configuration.UI_MODE_NIGHT_NO -> R.raw.lightmodesplashanim // Light mode animation
            else -> R.raw.lightmodesplashanim // Default to light mode
        }
        _animationResource.value = resource
    }

    fun getLocationName(location: LatLng) {
        viewModelScope.launch {
            currenLocationName.value=repository.getCountryNameFromLatLong(location.latitude, location.longitude)!!

        }
    }

    fun setCurrenLocation(location: LatLng) {
        getLocationName(location)
        repository.execute(PreferencesLocationEum.CURRENT)
        repository.saveLocationLatitude(location.latitude)
        repository.saveLocationLongitude(location.longitude)
        repository.saveLocationName(currenLocationName.value!!)
        Log.i(TAG, "setCurrenLocation: "+ currenLocationName.value + " " + location.latitude + " " + location.longitude)

    }

    fun startSplashTimer(duration: Long) {
        // Simulate a timer for splash screen
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _navigateToMainActivity.value = true
        }, duration)
    }
}
