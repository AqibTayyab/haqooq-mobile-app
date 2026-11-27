package com.example.services.sos

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import com.example.MainActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class SosLocationResult(
    val mapsLink: String?,
    val readableStatus: String,
    val permissionGranted: Boolean,
    val gpsEnabled: Boolean
)

/**
 * Member 4 device integration helper for real Android GPS location (with timeout & last-known
 * fallback), battery percentage, haptic/vibration feedback, and Safety Check-In notifications.
 */
object SosDeviceHelper {
    private const val PREFS_CHECKIN = "haqooq_checkin_prefs"
    private const val KEY_DEADLINE_MS = "checkin_deadline_ms"
    private const val KEY_DURATION_MIN = "checkin_duration_min"
    private const val CHANNEL_ID = "haqooq_safety_checkin"
    private const val NOTIF_ID = 4109

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchLocationWithFallback(context: Context): SosLocationResult {
        if (!hasLocationPermission(context)) {
            return SosLocationResult(
                mapsLink = null,
                readableStatus = "Location unavailable (permission denied)",
                permissionGranted = false,
                gpsEnabled = isLocationEnabled(context)
            )
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val gpsEnabled = lm != null && LocationManagerCompat.isLocationEnabled(lm)
        if (lm == null || !gpsEnabled) {
            return SosLocationResult(
                mapsLink = null,
                readableStatus = "Location unavailable (GPS off)",
                permissionGranted = true,
                gpsEnabled = false
            )
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        ).filter { runCatching { lm.isProviderEnabled(it) }.getOrDefault(false) }

        var location: Location? = null
        for (provider in providers) {
            location = withTimeoutOrNull(3000L) {
                requestSingleLocation(context, lm, provider)
            }
            if (location != null) break
        }

        if (location == null) {
            location = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            ).mapNotNull { provider ->
                runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
            }.maxByOrNull { it.time }
        }

        return if (location != null) {
            val lat = String.format(Locale.US, "%.6f", location.latitude)
            val lng = String.format(Locale.US, "%.6f", location.longitude)
            val mapsUrl = "https://maps.google.com/?q=$lat,$lng"
            SosLocationResult(
                mapsLink = mapsUrl,
                readableStatus = "$lat, $lng",
                permissionGranted = true,
                gpsEnabled = true
            )
        } else {
            SosLocationResult(
                mapsLink = null,
                readableStatus = "Location fix timed out (GPS weak indoors)",
                permissionGranted = true,
                gpsEnabled = true
            )
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleLocation(
        context: Context,
        lm: LocationManager,
        provider: String
    ): Location? = suspendCancellableCoroutine { cont ->
        val cancelSignal = CancellationSignal()
        cont.invokeOnCancellation { cancelSignal.cancel() }
        try {
            LocationManagerCompat.getCurrentLocation(
                lm,
                provider,
                cancelSignal,
                ContextCompat.getMainExecutor(context)
            ) { loc ->
                if (cont.isActive) cont.resume(loc)
            }
        } catch (_: Exception) {
            if (cont.isActive) cont.resume(null)
        }
    }

    fun readBatteryPercent(context: Context): Int {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return 85
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return if (level >= 0 && scale > 0) (level * 100) / scale else 85
    }

    @Suppress("DEPRECATION")
    fun vibrateTick(context: Context, isFinalAlert: Boolean = false) {
        val durationMs = if (isFinalAlert) 320L else 55L
        runCatching {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        if (isFinalAlert) VibrationEffect.DEFAULT_AMPLITUDE else 120
                    )
                )
            } else {
                vibrator.vibrate(durationMs)
            }
        }
    }

    fun persistCheckInDeadline(context: Context, deadlineMs: Long, durationMinutes: Int) {
        context.getSharedPreferences(PREFS_CHECKIN, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_DEADLINE_MS, deadlineMs)
            .putInt(KEY_DURATION_MIN, durationMinutes)
            .apply()
    }

    fun clearCheckInDeadline(context: Context) {
        context.getSharedPreferences(PREFS_CHECKIN, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_DEADLINE_MS)
            .remove(KEY_DURATION_MIN)
            .apply()
    }

    fun getSavedCheckInState(context: Context): Pair<Long, Int>? {
        val prefs = context.getSharedPreferences(PREFS_CHECKIN, Context.MODE_PRIVATE)
        val deadline = prefs.getLong(KEY_DEADLINE_MS, -1L)
        if (deadline <= 0L) return null
        val durationMin = prefs.getInt(KEY_DURATION_MIN, 15)
        return deadline to durationMin
    }

    fun showCheckInExpiredNotification(context: Context, title: String, body: String) {
        if (!hasNotificationPermission(context)) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Safety Check-In Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when a HAQOOQ Safety Check-In timer expires without confirmation."
            }
            nm.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching { nm.notify(NOTIF_ID, notification) }
    }
}
