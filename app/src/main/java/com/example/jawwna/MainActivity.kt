package com.example.jawwna

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jawwna.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Declare the View Binding object
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access the VideoView using View Binding
        val videoView = binding.backgroundVideoView

        // Get current night mode
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        //val videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.lightmodebacground)

        // Choose video based on the current night mode
        val videoUri = when (nightModeFlags) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                // Dark mode: Use night mode background video
                Uri.parse("android.resource://" + packageName + "/" + R.raw.nightmodebacground)
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                // Light mode: Use light mode background video
                Uri.parse("android.resource://" + packageName + "/" + R.raw.lightmodebacground)
            }
            else -> {
                // Default to light mode if unknown
                Uri.parse("android.resource://" + packageName + "/" + R.raw.lightmodebacground)
            }
        }

        // Set initial alpha to 0 (invisible) for fade-in effect
        videoView.alpha = 0f

        // Set up the VideoView
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f) // Mute the video

            // Set the fade-in animation once the video is ready to render
            mediaPlayer.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    videoView.animate()
                        .alpha(1f)  // Fade in to full visibility
                        .setDuration(2000)  // Duration of fade-in (2 seconds)
                        .start()  // Start the animation
                }
                true
            }

            // Handle when the video completes to reset and loop with fade-in
            mediaPlayer.setOnCompletionListener {
                videoView.alpha = 0f  // Reset alpha to 0 (invisible)
                mediaPlayer.seekTo(0)  // Restart the video from the beginning
                mediaPlayer.start()  // Start video playback

                // Apply fade-in animation again when loop restarts
                videoView.animate()
                    .alpha(1f)
                    .setDuration(2000)  // Duration of fade-in (2 seconds)
                    .start()
            }

            // Start video playback
            mediaPlayer.start()
        }


        }
    }


