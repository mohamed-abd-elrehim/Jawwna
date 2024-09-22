package com.example.jawwna.mainactivity

import android.content.res.Configuration
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.jawwna.R
import com.example.jawwna.databinding.ActivityMainBinding
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavController
    private  lateinit var bottomNavigationView: BottomNavigationView


    private val scaleDuration:Long=250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        viewModel.setbuttonBackground(this.packageName, nightModeFlags)
        // Observe the video URI from ViewModel
        viewModel.buttonNavigationBar.observe(this, Observer { color ->
            // Set up the VideoView with the URI provided by the ViewModel
            mBinding.bottomNavigationView.setBackgroundResource(color)
            mBinding.coordinatorLayout.setBackgroundResource(color)
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                // Device is in Night Mode
                // Change the menu icons for night mode (for example, dark icons)
                mBinding.bottomNavigationView.menu.findItem(R.id.menuHome).icon =
                    ContextCompat.getDrawable(this, R.drawable.round_home_24_night_mode)
                mBinding.bottomNavigationView.menu.findItem(R.id.menuFavorite).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite_night_mode)
                mBinding.bottomNavigationView.menu.findItem(R.id.menuSetting).icon =
                    ContextCompat.getDrawable(this, R.drawable.round_settings_24_night_mode)
            } else {
                // Device is in Day Mode
                // Change the menu icons for light mode (for example, light icons)
                mBinding.bottomNavigationView.menu.findItem(R.id.menuHome).icon =
                    ContextCompat.getDrawable(this, R.drawable.round_home_24_light_mode)
                mBinding.bottomNavigationView.menu.findItem(R.id.menuFavorite).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite_light_mode)
                mBinding.bottomNavigationView.menu.findItem(R.id.menuSetting).icon =
                    ContextCompat.getDrawable(this, R.drawable.round_settings_24_light_mode)
            }
        })



        // Set the video URI in the ViewModel based on the night mode
        viewModel.setVideoUri(this.packageName, nightModeFlags)

                // Observe the video URI from ViewModel
                viewModel.videoUri.observe(this, Observer { uri ->
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
                })

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