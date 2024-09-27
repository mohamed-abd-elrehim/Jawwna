package com.example.jawwna.mainactivity

import android.content.res.Configuration
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.jawwna.R
import com.example.jawwna.databinding.ActivityMainBinding
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.helper.UpdateLocale
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModel
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModelFactory
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModel
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView


    private val scaleDuration: Long = 250
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel =
            ViewModelProvider(
                this,
                MainActivityViewModelFactory(Repository.getRepository(this.application))
            ).get(
                MainActivityViewModel::class.java
            )

        viewModel.setUpdateLocale(Locale.getDefault().language)
        lifecycleScope.launch {
            viewModel.updateLocale.collect { language ->
                setLocale(language)
              //  setFount()

            }
        }
        // Log to check the current default language
        Log.i(TAG, "Current Locale: ${Locale.getDefault().language}")

        // Initialize the binding
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        // Access the VideoView using View Binding
        val videoView = mBinding.backgroundVideo

        // Initialize bottomNavigationView
        bottomNavigationView = mBinding.bottomNavigationView

        // Get the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

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
                    navController.navigate(R.id.mapFragment)
                    true
                }

                else -> false
            }
        }


        // Access the VideoView using View Binding

        // Get current night mode
        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
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
    fun setFount(){
        // Check current locale
         val currentLocale = getResources().getConfiguration().locale;
        if (currentLocale.getLanguage().equals("ar")) {
            // If Arabic, apply the Arabic text appearance style
            setTheme(R.style.AppTextAppearanceArabic);
        } else {
            // Default font for other languages
            setTheme(R.style.AppTextAppearance);
        }

        setContentView(R.layout.activity_main);  // Load the UI after setting the theme
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