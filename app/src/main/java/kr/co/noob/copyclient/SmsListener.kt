package kr.co.noob.copyclient

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import kr.co.noob.copyclient.data.Message
import kr.co.noob.copyclient.data.SMSMessage
import kr.co.noob.copyclient.uilts.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.regex.Pattern

class SmsListener : Service(), ReceiverThread.MessageListener,
    BroadcastReceiver.SMSReceivedListener {

    private var socket: Socket? = null
    private var isConnected = false
    private var broadcastReceiver: BroadcastReceiver? = null
    private lateinit var pref: SharedPreferences
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent): IBinder? {
        // Bind
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        // Register broadcast receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED")
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        broadcastReceiver = BroadcastReceiver()
        broadcastReceiver!!.setSMSListener(this)
        registerReceiver(broadcastReceiver, intentFilter)

        // Get preference
        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val serverIp: String? = pref.getString("key_ip", null)
        val serverPort = pref.getString("key_port", resources.getString(R.string.default_port))

        // Init notification manager
        notificationManager = kr.co.noob.copyclient.uilts.NotificationManager(applicationContext)
        startForeground(FOREGROUND_ID, notificationManager.getNotification("서버와 연결 중...", true, true))

        // Connect to server
        Thread {
            try {
                socket = Socket()
                socket!!.connect(InetSocketAddress(serverIp, serverPort!!.toInt()), 3000)
                isConnected = true

                val receiver =  ReceiverThread(socket!!)
                receiver.setOnMessageListener(this)
                receiver.start()
            } catch (e: IOException) {
                e.printStackTrace()
                notificationManager.showNotification(notificationManager.getNotification("서버와 연결할 수 없습니다.", false, false))

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) stopForeground(Service.STOP_FOREGROUND_DETACH)
                else stopForeground(false)

                stopSelf()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        socket?.close()
        isConnected = false

        // Unregister sms receiver and edit preference
        unregisterReceiver(broadcastReceiver)
        pref.edit().putBoolean("key_service", false).apply()
        sendBroadcast(Intent(SettingsActivity.ACTION_DISCONNECTED))
    }

    // Handle message from server
    override fun onMessageReceived(msg: Message) {
        when(msg) {
            Message.EOC -> {
                // Thread loop will be finished
                notificationManager.showNotification(notificationManager.getNotification("서버에 의해 연결이 해제되었습니다.", false, false))

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) stopForeground(Service.STOP_FOREGROUND_DETACH)
                else stopForeground(false)

                stopSelf()
            }
            Message.REJ -> {
                // Thread loop will be finished
                notificationManager.showNotification(notificationManager.getNotification("비밀번호가 맞지 않습니다.", false, false))

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) stopForeground(Service.STOP_FOREGROUND_DETACH)
                else stopForeground(false)

                stopSelf()
            }
            Message.ACK -> {
                // Authentication complete
                notificationManager.showNotification(notificationManager.getNotification("서버와 연결되었습니다.", false, true))
            }
            Message.RQ_PW -> {
                // Server require password
                val password = pref.getString("key_password", "")
                MessageSender(socket!!).execute(password)
            }
            Message.CONNECTED -> {
                // Connection made
            }
            else -> {
                // Unknown message
                throw Error(String.format("Unknown message received : %s", msg))
            }
        }
    }

    // Handle message from SMS broadcast receiver
    override fun onMessageReceived(msg: SMSMessage) {
        if(socket != null) {
            // Get regex from preference and apply
            val regex = pref.getString("key_pref", "\\d+")
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(msg.content)

            if(matcher.find()) {
                // Met the condition
                MessageSender(socket!!).execute(matcher.group())
            }
            else {
                // Nothing found
            }
        }
    }

    companion object {
        const val FOREGROUND_ID = 31331
    }
}
