package com.example.jawwna.mainactivity.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jawwna.R

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _videoUri = MutableLiveData<Uri>()
    val videoUri: LiveData<Uri> get() = _videoUri

    fun setVideoUri(packageName: String, nightModeFlags: Int) {
        val context = getApplication<Application>().applicationContext
        val uri = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> Uri.parse("android.resource://$packageName/${R.raw.nightmodebacground}")
            Configuration.UI_MODE_NIGHT_NO -> Uri.parse("android.resource://$packageName/${R.raw.lightmodebacground}")
            else -> Uri.parse("android.resource://$packageName/${R.raw.lightmodebacground}")
        }
        _videoUri.value = uri
    }
}
