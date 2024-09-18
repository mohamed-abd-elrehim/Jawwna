package com.example.jawwna.splashscreen.viewmodel
import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jawwna.R

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val _animationResource = MutableLiveData<Int>()
    val animationResource: LiveData<Int> get() = _animationResource

    private val _navigateToMainActivity = MutableLiveData<Boolean>()
    val navigateToMainActivity: LiveData<Boolean> get() = _navigateToMainActivity

    fun setAnimationResource(nightModeFlags: Int) {
        val resource = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.nightmodesplashanim // Dark mode animation
            Configuration.UI_MODE_NIGHT_NO -> R.raw.lightmodesplashanim // Light mode animation
            else -> R.raw.lightmodesplashanim // Default to light mode
        }
        _animationResource.value = resource
    }

    fun startSplashTimer(duration: Long) {
        // Simulate a timer for splash screen
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _navigateToMainActivity.value = true
        }, duration)
    }
}
