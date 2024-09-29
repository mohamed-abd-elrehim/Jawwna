package com.example.jawwna.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.example.jawwna.R
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.datasource.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.annotation.DrawableRes
import com.bumptech.glide.request.transition.Transition

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
 val  context: Context = context
    private val repository: IRepository = Repository.getRepository(context as Application)
    private val TAG = "NotificationWorker"

    override suspend fun doWork(): Result {
        val date = inputData.getString("date")
        val time = inputData.getString("time")
        Log.d(TAG, "doWork: $date $time")

        if (date == null || time == null) {
            return Result.failure() // Invalid input
        }

        val targetTimeInMillis = convertDateTimeToMillis("$date $time")

        try {
            // Wait until the target time is reached (not ideal for precision timing, better use AlarmManager)
            while (System.currentTimeMillis() < targetTimeInMillis) {
                if (isStopped) {
                    return Result.failure() // Worker was stopped, handle cancellation
                }
               delay(1000)
            }
            val alarmEntity = withContext(Dispatchers.IO) {
                repository.getAlarmByDateTime(date, time)
            }

            // Trigger the notification
            if (alarmEntity != null) {
                sendNotification(alarmEntity)
                return Result.success() // Successfully sent the notification
            } else {
                return Result.failure() // Alarm not found in repository
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in NotificationWorker: ${e.message}")
            return Result.failure() // Return failure in case of exceptions
        }
    }

    private fun sendNotification(alarmEntity: AlarmEntity) {
        val channelId = "ALARM_NOTIFICATION_CHANNEL"
        createNotificationChannel() // Ensure channel is created

        val notificationId = System.currentTimeMillis().toInt()

        // Build the full URL for the weather icon
        val modeSuffix = if (isDarkModeEnabled(applicationContext)) "n" else "d"
        val baseUrl = "https://openweathermap.org/img/wn/"
        val fullIconUrl = "$baseUrl${alarmEntity.icon.dropLast(1)}$modeSuffix@2x.png"

        // Create a Bitmap for the notification icon using Glide
        Glide.with(applicationContext)
            .asBitmap()
            .load(fullIconUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Create the notification
                    val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(
                            context.getString(
                                R.string.reminder_for_at,
                                alarmEntity.description,
                                alarmEntity.date,
                                alarmEntity.time
                            ))
                        .setSmallIcon(R.drawable.weather_notfaction) // Set the small icon here
                        .setLargeIcon(resource) // Set the downloaded Bitmap as the large icon
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)

                    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(notificationId, notificationBuilder.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle cleanup if necessary
                }
            })
    }



    private fun convertDateTimeToMillis(dateTimeString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.parse(dateTimeString)?.time ?: 0L
    }

    // Create notification channel for Android 8.0 and above
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "ALARM_NOTIFICATION_CHANNEL"
            val channelName = "Alarm Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun isDarkModeEnabled(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
