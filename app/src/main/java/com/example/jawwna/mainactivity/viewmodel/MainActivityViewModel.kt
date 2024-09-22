package com.example.jawwna.mainactivity.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jawwna.R

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _videoUri = MutableLiveData<Uri>()
    val videoUri: LiveData<Uri> get() = _videoUri

    private val _buttonNavigationBar = MutableLiveData<Int>()
    val buttonNavigationBar: LiveData<Int> get() = _buttonNavigationBar

    fun setVideoUri(packageName: String, nightModeFlags: Int) {
        val context = getApplication<Application>().applicationContext
        val uri = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> Uri.parse("android.resource://$packageName/${R.raw.nightmodebacground}")
            Configuration.UI_MODE_NIGHT_NO -> Uri.parse("android.resource://$packageName/${R.raw.lightmodebacground}")
            else -> Uri.parse("android.resource://$packageName/${R.raw.lightmodebacground}")
        }
        _videoUri.value = uri
    }


    fun setbuttonBackground(packageName: String, nightModeFlags: Int) {
        val colorResId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.drawable.bottom_bar_background_night_mode
            Configuration.UI_MODE_NIGHT_NO -> R.drawable.bottom_bar_background_light_mode
            else -> R.drawable.bottom_bar_background_light_mode
        }
        _buttonNavigationBar.value = colorResId


    }


}
