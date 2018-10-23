package kr.co.noob.copyclient.uilts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import kr.co.noob.copyclient.R
import kr.co.noob.copyclient.SettingsActivity


class NotificationManager(private var context: Context) {

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    fun getNotification(title: String, indeterminate: Boolean, addDisconnect: Boolean): Notification {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val cIntent = Intent(context, SettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val cpIntent = PendingIntent.getActivity(context, 0, cIntent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_notifications_black_24dp)
            setContentTitle(title)
            setProgress(0 , 0, indeterminate)
            setContentIntent(cpIntent)
        }

        if(addDisconnect) {
            val intent = Intent(context, BroadcastReceiver::class.java).apply {
                action = ACTION_DISCONNECT
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            builder.setOngoing(true)
            builder.addAction(0, "연결 해제", pendingIntent)
        }

        return builder.build()
    }

    fun showNotification(notification: Notification) {
        with(notificationManager) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    fun cancelNotification() {
        with(notificationManager) {
            cancel(NOTIFICATION_ID)
        }
    }

    companion object {
        const val CHANNEL_NAME = "COPY_CLIENT_"
        const val CHANNEL_ID = "COPY_CLIENT"
        const val NOTIFICATION_ID = 31331
        const val ACTION_DISCONNECT = "DISCONNECT"
    }

}