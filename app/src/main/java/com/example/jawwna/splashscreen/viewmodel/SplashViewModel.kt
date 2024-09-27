package com.example.jawwna.splashscreen.viewmodel
import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jawwna.R
import com.example.jawwna.datasource.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel(private val repository: IRepository) : ViewModel() {

    private val _animationResource = MutableStateFlow<Int>(0)
    val animationResource: StateFlow<Int> get() = _animationResource

    private val _navigateToMainActivity = MutableStateFlow<Boolean>(false)
    val navigateToMainActivity: StateFlow<Boolean> get() = _navigateToMainActivity

    private val _updateLocale = MutableStateFlow<String>("")
    val updateLocale: StateFlow<String> get() = _updateLocale

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

    fun startSplashTimer(duration: Long) {
        // Simulate a timer for splash screen
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _navigateToMainActivity.value = true
        }, duration)
    }
}
