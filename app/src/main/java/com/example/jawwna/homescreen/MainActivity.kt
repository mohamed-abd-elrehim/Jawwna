package com.example.jawwna.homescreen
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.jawwna.databinding.ActivityMainBinding
import com.example.jawwna.homescreen.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    // Declare the View Binding object
    private lateinit var binding: ActivityMainBinding

    // Declare the ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access the VideoView using View Binding
        val videoView = binding.backgroundVideoView

        // Get current night mode
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

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

        // Set the video URI in the ViewModel based on the night mode
        viewModel.setVideoUri(packageName, nightModeFlags)
    }
}
