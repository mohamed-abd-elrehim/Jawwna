package com.example.jawwna.splashscreen

import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.jawwna.R
import com.example.jawwna.customui.CustomAlertDialog
import com.example.jawwna.databinding.ActivitySplashScreenBinding
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.mainactivity.MainActivity
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModel
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModelFactory
import com.example.jawwna.splashscreen.viewmodel.SplashViewModel
import com.example.jawwna.splashscreen.viewmodel.SplashViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.example.jawwna.customui.CustomPopup
import com.example.jawwna.helper.broadcastreceiver.NetworkChangeReceiver
import com.example.jawwna.helper.broadcastreceiver.NetworkStateChangeListener
import com.example.jawwna.helper.broadcastreceiver.SharedConnctionStateViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() , NetworkStateChangeListener {
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var sharedConnctionStateViewModel: SharedConnctionStateViewModel
    private var isNetworkAvailable: Boolean = true
    private lateinit var rootLayout: View

    private  lateinit var customPopup: CustomPopup
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var welcomeText: TextView
    private lateinit var viewModel: SplashViewModel
    private var isNightMode = false
    private var isCurrenLocationAvailable = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val splashDuration = 3000L  // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize View Binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rootLayout = binding.root
        sharedConnctionStateViewModel = ViewModelProvider(this)[SharedConnctionStateViewModel::class.java]

        customPopup = CustomPopup(this)
        viewModel =
            ViewModelProvider(
                this,
                SplashViewModelFactory(Repository.getRepository(this.application))
            ).get(
                SplashViewModel::class.java
            )

        viewModel.setUpdateLocale(Locale.getDefault().language)

        // Collect the updated locale
        lifecycleScope.launch {
            viewModel.updateLocale.collect { language ->
                setLocale(language)
                applyThemeBasedOnLocaleAndMode()
            }
        }
        Log.i("Current Locale", "Current Locale: ${Locale.getDefault().language}")

        // Collect the current location
        lifecycleScope.launch {
            SharedConnctionStateViewModel.sharedConnctionState.collect { isConnected ->
                if (!isConnected) {
                    isNetworkAvailable=false
                } else {
                  isNetworkAvailable=true
                }
            }
        }

        // Check if current location is available
        viewModel.IsCurrenLocationAvailable()
        lifecycleScope.launch {
            viewModel.isCurrenLocationAvailable.collect { isAvailable ->
                isCurrenLocationAvailable = isAvailable
            }
        }


        networkChangeReceiver = NetworkChangeReceiver(this)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val customAlert = CustomAlertDialog(this)

        lottieAnimationView = binding.lottieAnimationView
        welcomeText = binding.welcomeText

        // Get current night mode and set animation resource in ViewModel
        val nightModeFlags =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        viewModel.setAnimationResource(nightModeFlags)

        // Observe the animation resource LiveData
        lifecycleScope.launch {
            viewModel.animationResource.collect { animationResource ->
                lottieAnimationView.setAnimation(animationResource)
                lottieAnimationView.playAnimation()
                animateTextView()
            }
        }

        // Start the splash timer in ViewModel
        viewModel.startSplashTimer(splashDuration)

        // Create an animation instance for fade-out effect
        val fadeOutAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Set Animation Listener for fade-out effect
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (!isCurrenLocationAvailable) {
                    if (!isNetworkAvailable) {
                        customPopup.showPopup(rootLayout,getString(R.string.no_internet_connection),"please check your internet connection and try again", isNightMode)
                        lifecycleScope.launch {
                            delay(5000)
                            finish()
                        }
                    }else {
                        customAlert.showDialog(
                            message = getString(R.string.location_dialog_message),
                            title = getString(R.string.location_dialog_title),
                            isDarkTheme = isNightMode,
                            positiveText = getString(R.string.map),
                            negativeText = getString(R.string.gps),
                            positiveAction = {
                                // Navigate to MainActivity
                                // Simulate some loading or delay before starting MainActivity
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(
                                        Intent(this@SplashActivity, MainActivity::class.java).apply {
                                            putExtra("MapFragment", "mapFragment") // Pass any data if necessary
                                        }
                                    )
                                    finish() // Optional: call finish() to remove the splash activity from the back stack
                                }, 2000) // Delay of 2000 milliseconds (2 seconds)

                            },
                            negativeAction = {
                                checkGpsEnabled()
                            }
                        )
                    }
                } else {
                    // Start MainActivity when animation ends
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Schedule fade-out animation after splash duration
        welcomeText.startAnimation(fadeOutAnimation)
    }

    private fun animateTextView() {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 2000 // 2 seconds
            fillAfter = true
        }
        welcomeText.startAnimation(fadeIn)
    }

    // Function to set the application's locale
    fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        // Create a new Configuration object and set the new locale
        val config = Configuration(this.resources.configuration).apply {
            setLocale(locale) // For Android 7.0 (Nougat) and higher
        }

        // Update the resources with the new configuration
        this.resources.updateConfiguration(config, this.resources.displayMetrics)

        // For devices running Android N and higher, create a configuration context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.createConfigurationContext(config)
        }

        updateUIText()
    }

    // Function to update UI components dynamically after locale change
    private fun updateUIText() {
        binding.welcomeText.text = getString(R.string.welcome_text)
        // Update other UI components if needed, like menus, toolbar, etc.
    }

    private fun applyThemeBasedOnLocaleAndMode() {
        val currentLocale = resources.configuration.locales[0]
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        // Apply the correct theme based on locale and night mode
        if (currentLocale.language == "ar") {
            if (isNightMode) {
                setTheme(R.style.Theme_Jawwna_Arabic_Night)  // Arabic Night mode theme
            } else {
                setTheme(R.style.Theme_Jawwna_Arabic)  // Arabic Light mode theme
            }
        } else {
            if (isNightMode) {
                setTheme(R.style.Theme_Jawwna_Night)  // Default locale Night mode theme
            } else {
                setTheme(R.style.Theme_Jawwna)  // Default locale Light mode theme
            }
        }
        updateUIText()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reapply the correct theme when the configuration changes
        applyThemeBasedOnLocaleAndMode()
        recreate()  // Restart the activity to apply the new theme
    }


    // Check if GPS is enabled
    private fun checkGpsEnabled() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, open settings
            openLocationSettings()
        } else {
            // If GPS is enabled, retrieve location
            getLocation()
        }
    }

    // Retrieve location using FusedLocationProviderClient
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        viewModel.setCurrenLocation(LatLng(latitude, longitude))
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    val customAlert = CustomAlertDialog(this)
                    customAlert .showDialog(
                        message = getString(R.string.failed_to_retrieve_your_current_location_please_check_your_network_connection_or_select_a_location_from_the_map_manually),
                        title = getString(R.string.warning),
                        isDarkTheme = isNightMode,
                        positiveText = getString(R.string.map),
                        positiveAction = {
                            // Navigate to MainActivity
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
                                putExtra("MapFragment", "mapFragment") // Pass any data if necessary
                            })
                        },
                        negativeText = getString(R.string.check_network),
                        negativeAction = {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(intent)
                        }
                    )

                    // Handle failure to get location
                    Log.e("SplashActivity", "Failed to get location: ${e.message}")
                }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    // Handle the permission request response
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                getLocation()
            } else {
                // Permission denied
                showLocationPermissionDeniedDialog()
            }
        }
    }

    // Show a dialog to the user to grant location permission
    private fun showLocationPermissionDeniedDialog() {
        val customAlert = CustomAlertDialog(this)
        customAlert.showDialog(
            message = getString(R.string.location_permission_required_message),
            title = getString(R.string.location_permission_required_title),
            isDarkTheme = isNightMode,
            positiveText = getString(R.string.settings),
            positiveAction = {
                openAppSettings()
            }
        )
    }
    // Open the app settings for the user to enable permissions
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    // Open location settings
    private fun openLocationSettings() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
    override fun onNetworkStateChanged(isConnected: Boolean) {
        lifecycleScope.launch {
            sharedConnctionStateViewModel.updateSharedData(isConnected)
        }
    }


    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkChangeReceiver)


    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the animation when the activity is destroyed
        lottieAnimationView.cancelAnimation()


    }
}
