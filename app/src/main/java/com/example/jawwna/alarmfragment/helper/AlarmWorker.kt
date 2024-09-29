package com.example.jawwna.alarmfragment.helper

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.jawwna.R
import com.example.jawwna.databinding.OverlayLayoutBinding
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.datasource.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.delay

class AlarmWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private lateinit var binding: OverlayLayoutBinding
    private val repository: IRepository = Repository.getRepository(context as Application)
    private val overlayManager: OverlayManager = OverlayManager(applicationContext)
    private val TAG = "AlarmWorker"

    private var mediaPlayer: MediaPlayer? = null // Singleton MediaPlayer instance

    override suspend fun doWork(): Result {
        val date = inputData.getString("date")
        val time = inputData.getString("time")

        if (date == null || time == null) {
            return Result.failure() // Invalid input
        }

        val targetTimeInMillis = convertDateTimeToMillis("$date $time")

        try {
            // Delay until the target time is reached
            while (System.currentTimeMillis() < targetTimeInMillis) {
                if (isStopped) {
                    return Result.failure() // Handle cancellation
                }
                delay(1000) // Check every second
            }

            // Fetch the alarm from the repository
            val alarmEntity = withContext(Dispatchers.IO) {
                repository.getAlarmByDateTime(date, time)
            }

            return if (alarmEntity != null) {
                // Show overlay and play sound
                withContext(Dispatchers.Main) {
                    overlayManager.showOverlay(
                        alarmEntity.icon,
                        alarmEntity.description,
                        alarmEntity.minTemp,
                        alarmEntity.maxTemp
                    )
                    playAlarmSound() // Play the alarm sound
                    delay(1000)
                    repository.deleteAlarm(alarmEntity) // Delete the alarm after 1 second
                }
                Result.success() // Successfully showed overlay and played sound
            } else {
                Result.failure() // No result found
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in AlarmWorker: ${e.message}")
            return Result.failure() // Return failure in case of exceptions
        }
    }

    private fun playAlarmSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alarmsound) // Use your sound file
            mediaPlayer?.start() // Start playing the sound

            mediaPlayer?.setOnCompletionListener {
                it.release() // Release the MediaPlayer when done
                mediaPlayer = null // Reset the mediaPlayer reference
            }
        }
    }

    private fun convertDateTimeToMillis(dateTimeString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.parse(dateTimeString)?.time ?: 0L
    }

    fun cancelAlarm(date: String, time: String) {
        val alarmId = generateAlarmId(date, time)
        WorkManager.getInstance(applicationContext).cancelUniqueWork(alarmId)
        Log.d(TAG, "Cancelling alarm with ID: $alarmId")
        stopAlarmSound()
        OverlayManager(applicationContext).dismissOverlay() // Dismiss overlay if necessary
    }

    private fun stopAlarmSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null // Reset the mediaPlayer reference
        }
    }

     fun generateAlarmId(date: String?, time: String?): String {
        return "$date-$time" // Concatenate date and time to form a unique ID
    }
}
