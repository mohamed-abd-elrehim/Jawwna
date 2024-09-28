package com.example.jawwna.mainactivity

import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.jawwna.R
import com.example.jawwna.customui.CustomAlertDialog
import com.example.jawwna.customui.CustomSnackbar
import com.example.jawwna.databinding.ActivityMainBinding
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.broadcastreceiver.NetworkChangeReceiver
import com.example.jawwna.helper.broadcastreceiver.NetworkStateChangeListener
import com.example.jawwna.helper.broadcastreceiver.SharedConnctionStateViewModel
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModel
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() , NetworkStateChangeListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var navController: NavController
    private  var isNightMode= false
    private lateinit var bottomNavigationView: BottomNavigationView
    private val scaleDuration: Long = 250
    private val TAG = "MainActivity"
    private lateinit var rootLayout: View
    private var isNetworkAvailable: Boolean = true
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private val sharedConnctionStateViewModel: SharedConnctionStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the binding
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        rootLayout = mBinding.root
        // Get the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        networkChangeReceiver = NetworkChangeReceiver(this)
        val customAlert = CustomAlertDialog(this)


        // Access the VideoView using View Binding
        val videoView = mBinding.backgroundVideo
        // Initialize bottomNavigationView
        bottomNavigationView = mBinding.bottomNavigationView

        if (intent != null) {
        intent.getStringExtra("MapFragment")?.let {
            if (it == "mapFragment") {
                val bundle = Bundle()
                bundle.putString("actionCurrent", PreferencesLocationEum.CURRENT.toString());
                navController.navigate(R.id.mapFragment, bundle)

            }
        }
            }



        viewModel =
            ViewModelProvider(
                this,
                MainActivityViewModelFactory(Repository.getRepository(this.application))
            ).get(
                MainActivityViewModel::class.java
            )


        viewModel.isNetworkAvailable()
        lifecycleScope.launch {
            viewModel.isNetworkAvailable.collect { isNetworkAvailable ->
                Log.i(TAG, "isNetworkAvailable: $isNetworkAvailable")
                when (isNetworkAvailable) {
                    true -> Log.i(TAG, "Network is available")
                    false -> Log.i(TAG, "Network is not available")
                }
            }
        }

        viewModel.setUpdateLocale(Locale.getDefault().language)
        lifecycleScope.launch {
            viewModel.updateLocale.collect { language ->
                setLocale(language)
              //  setFount()
                applyThemeBasedOnLocaleAndMode()

            }
        }
        // Log to check the current default language
        Log.i(TAG, "Current Locale: ${Locale.getDefault().language}")




        // Set up a listener for the global layout to detect if the keyboard is open
        bottomNavigationView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            bottomNavigationView.getWindowVisibleDisplayFrame(r)
            val screenHeight = bottomNavigationView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > 500) {
                // Keyboard is probably open
                bottomNavigationView.visibility = View.GONE
            } else {
                // Keyboard is closed
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        // Set up a listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    videoView.start()
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.menuSetting -> {
                    videoView.start()
                    navController.navigate(R.id.settingsFragment)
                    true
                }


                R.id.menuFavorite -> {
                    videoView.stopPlayback()
                    navController.navigate(R.id.addFavoriteLocationFragment)
                    true
                }

                else -> false
            }
        }


        // Access the VideoView using View Binding

        // Get current night mode
        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            Log.i(TAG, "onCreate: Night mode is enabled")
            isNightMode = true
        } else {
            Log.i(TAG, "onCreate: Night mode is disabled")
            isNightMode = false
        }

        lifecycleScope.launch {
            sharedConnctionStateViewModel.sharedConnctionState.collect {isConnected ->
                if (isConnected) {
                    CustomSnackbar.show(
                        view = rootLayout, // Pass the root view of the activity
                        message = getString(R.string.connected_to_the_internet),
                        isDarkTheme = isNightMode, // You can pass true or false based on the current theme
                        buttonText = getString(R.string.ok),
                        duration = Snackbar.LENGTH_LONG,
                        showButton = false
                    )   // Action to perform on button click, for example, retrying a request
                } else {
                    CustomSnackbar.show(
                        view = rootLayout, // Pass the root view of the activity
                        message = getString(R.string.no_internet_connection),
                        isDarkTheme = isNightMode, // You can pass true or false based on the current theme
                        buttonText = getString(R.string.open_network),
                        duration = Snackbar.LENGTH_LONG,
                        {
                            customAlert.showDialog(
                                message = getString(R.string.do_you_want_to_open_network_settings),
                                title = getString(R.string.alert_message),
                                isDarkTheme = isNightMode,
                                positiveText= getString(R.string.open_wifi),
                                negativeText = getString(R.string.open_data),
                                positiveAction = {
                                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                    startActivity(intent)

                                },negativeAction = {
                                    val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
                                    startActivity(intent)
                                })
                        },
                        showButton = true)
                }
            }


        }


// Set button background based on the night mode
        viewModel.setbuttonBackground(this.packageName, nightModeFlags)

// Observe the background color from ViewModel
        lifecycleScope.launch {
            viewModel.buttonNavigationBar.collect { color ->
                mBinding.bottomNavigationView.setBackgroundResource(color)
                mBinding.coordinatorLayout.setBackgroundResource(color)
            }
        }

// Set icons for the navigation menu
        viewModel.setIconUri(this.packageName, nightModeFlags)

        fun collectIconUpdates() {
            lifecycleScope.launch {
                // Collecting icons in a single coroutine to reduce redundancy
                launch {
                    viewModel.iconFavorite.collect { uri ->
                        mBinding.bottomNavigationView.menu.findItem(R.id.menuFavorite).setIcon(uri)
                    }
                }
                launch {
                    viewModel.iconSetting.collect { uri ->
                        mBinding.bottomNavigationView.menu.findItem(R.id.menuSetting).setIcon(uri)
                    }
                }
                launch {
                    viewModel.iconHome.collect { uri ->
                        mBinding.bottomNavigationView.menu.findItem(R.id.menuHome).setIcon(uri)
                    }
                }
            }
        }

// Call the function to start collecting icon updates
        collectIconUpdates()


        // Set the video URI in the ViewModel based on the night mode
        viewModel.setVideoUri(this.packageName, nightModeFlags)
        lifecycleScope.launch {

            // Observe the video URI from ViewModel
            viewModel.videoUri.collect { uri ->
                // Set up the VideoView with the URI provided by the ViewModel
                videoView.setVideoURI(uri)
                videoView.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    mediaPlayer.setVolume(0f, 0f) // Mute the video

                    // Set fade-in animation
                    mediaPlayer.setOnInfoListener { _, what, _ ->
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            videoView.animate()
                                .alpha(1f)
                                .setDuration(2000)
                                .start()
                        }
                        true
                    }

                    // Handle loop and fade-in effect
                    mediaPlayer.setOnCompletionListener {
                        videoView.alpha = 0f
                        mediaPlayer.seekTo(0)
                        mediaPlayer.start()
                        videoView.animate()
                            .alpha(1f)
                            .setDuration(2000)
                            .start()
                    }

                    mediaPlayer.start()
                }
            }
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

    // Function to set the application's locale
     fun setLocale(language:String) {
        // Create a Locale object for the specified language code
        val locale = Locale(language)
        Locale.setDefault(locale) // Set the default locale for the application

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


    }
    private fun applyThemeBasedOnLocaleAndMode() {
        // Detect current locale
        val currentLocale = resources.configuration.locales[0]

        // Detect if the UI is in night mode
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        // Apply the correct theme
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

        lifecycleScope.launch {
            sharedConnctionStateViewModel.sharedConnctionState.collect { isConnected ->
                Log.i(TAG, "applyThemeBasedOnLocaleAndMode: $isConnected")
                isNetworkAvailable = isConnected
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reapply the correct theme when the configuration changes (e.g., night mode)
        applyThemeBasedOnLocaleAndMode()
        recreate()  // Restart the activity to apply the new theme
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        lifecycleScope.launch {
            sharedConnctionStateViewModel.updateSharedData(isConnected)
        }
    }





}


// mapView = binding.mapView
//        mapView.mapboxMap.styleURI.get(2)


//
//        // Access the VideoView using View Binding
//        val videoView = binding.backgroundVideoView
//
//        // Get current night mode
//        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
//
//        // Observe the video URI from ViewModel
//        viewModel.videoUri.observe(this, Observer { uri ->
//            // Set up the VideoView with the URI provided by the ViewModel
//            videoView.setVideoURI(uri)
//            videoView.setOnPreparedListener { mediaPlayer ->
//                mediaPlayer.isLooping = true
//                mediaPlayer.setVolume(0f, 0f) // Mute the video
//
//                // Set fade-in animation
//                mediaPlayer.setOnInfoListener { _, what, _ ->
//                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//                        videoView.animate()
//                            .alpha(1f)
//                            .setDuration(2000)
//                            .start()
//                    }
//                    true
//                }
//
//                // Handle loop and fade-in effect
//                mediaPlayer.setOnCompletionListener {
//                    videoView.alpha = 0f
//                    mediaPlayer.seekTo(0)
//                    mediaPlayer.start()
//                    videoView.animate()
//                        .alpha(1f)
//                        .setDuration(2000)
//                        .start()
//                }
//
//                mediaPlayer.start()
//            }
//        })
//
//        // Set the video URI in the ViewModel based on the night mode
// viewModel.setVideoUri(packageName, nightModeFlags)