package com.example.uberclone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

object Constants {

    val DRIVER_KEY: String = "DriverKey"
    val REQUEST_DRIVER_DECLINE = "Decline"
    val RIDER_KEY: String = "RiderKey"
    val PICKUP_LOCATION: String = "PickIupLocation"
    val REQUEST_DRIVER_TITLE: String = "RequestDriver"

    val TOKEN_REFERENCE: String = "Token"
    val DRIVERS_LOCATION_REFERENCE: String = "DriversLocation"
    var currentUser: DriverInfoModel? = null
    val DRIVER_INFO_REFERENCE = "DriverInfo"

    val NOTI_BODY = "body"
    val NOTI_TITLE = "title"

    fun buildWelcomeMessage(): String {
        return StringBuilder("Welcome, ")
            .append(currentUser?.firstName)
            .append(" ")
            .append(currentUser?.lastName)
            .toString()
    }

    fun showNotification(
        context: Context,
        id: Int,
        title: String?,
        body: String?,
        intent: Intent?
    ) {
        var pendingIntent: PendingIntent? = null
        if (intent != null)
            pendingIntent =
                PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val NOTIFICATION_CHANNEL_ID = "com.example.uberclone"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Uber Clone",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Uber Clone"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
        }


        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        builder.setContentTitle(title)

        builder.setAutoCancel(false)
        builder.setContentText(body)
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        builder.setDefaults(android.app.Notification.DEFAULT_VIBRATE)
        builder.setSmallIcon(R.drawable.ic_car)
        builder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.ic_car
            )
        )

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent)
        val notification = builder.build()
        notificationManager.notify(id, notification)
        Log.d("title",title!!)
    }

    fun buildName(firstName: String, lastName: String): String {
        return StringBuilder(firstName).append(" ").append(lastName).toString()
    }


    //GET BEARING
    fun getBearing(begin: LatLng, end: LatLng): Float {
        //You can copy this function by link at description
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
            Math.atan(lng / lat)
        )
            .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
            Math.atan(lng / lat)
        ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 270).toFloat()
        return (-1).toFloat()
    }

    //DECODE POLY
    fun decodePoly(encoded: String): MutableList<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    fun setWelcomeMessage(textWelcome: TextView?) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour >= 1 && hour <= 12) {
            textWelcome?.text = StringBuilder("Good morning")
        } else if (hour > 12 && hour <= 17) {
            textWelcome?.text = java.lang.StringBuilder("Good afternoon")
        } else {
            textWelcome?.text = StringBuilder("Good evening")
        }
    }

    fun formatDuration(duration: String): CharSequence {
        if (duration.contains("mins")) {
            return duration.substring(0,duration.length -1)
        }else {
            return duration
        }
    }

    fun formatAddress(address: String): String {
        val commaIndex = address.indexOf(',')
        val secondCommaIndex = address.indexOf(',', commaIndex + 1)

        if (commaIndex != -1) {
            return address.substring(commaIndex + 1, secondCommaIndex).trim()
        }

        return address
    }

}