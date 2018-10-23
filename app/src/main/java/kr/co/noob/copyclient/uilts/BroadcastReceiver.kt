package kr.co.noob.copyclient.uilts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.SmsMessage
import android.util.Log
import kr.co.noob.copyclient.SettingsActivity
import kr.co.noob.copyclient.SmsListener
import kr.co.noob.copyclient.data.SMSMessage

class BroadcastReceiver : BroadcastReceiver() {

    private var listener: SMSReceivedListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            "android.provider.Telephony.SMS_RECEIVED" -> {
                // Incoming new sms, get message info
                var sender: String? = null
                val extra = intent.extras
                if(extra != null && !extra.isEmpty) {
                    val pdus = extra.get("pdus") as Array<Any>
                    if(pdus.isNotEmpty()) {
                        val messages = arrayOfNulls<SmsMessage>(pdus.size)
                        val builder = StringBuilder()
                        pdus.forEachIndexed { index, any ->
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                messages[index] = SmsMessage.createFromPdu(any as ByteArray, extra.getString("format"))
                            }
                            else {
                                messages[index] = SmsMessage.createFromPdu(any as ByteArray)
                            }

                            sender = messages[index]?.originatingAddress
                            builder.append(messages[index]?.messageBody)
                        }

                        val msg = builder.toString()
                        listener?.onMessageReceived(SMSMessage(sender!!, msg))
                    }
                }
            }
            NotificationManager.ACTION_DISCONNECT -> {
                val notificationManager = NotificationManager(context)
                notificationManager.cancelNotification()
                context.sendBroadcast(Intent(SettingsActivity.ACTION_DISCONNECTED))
                context.stopService(Intent(context, SmsListener::class.java))
            }
        }
    }

    fun setSMSListener(listener: SMSReceivedListener) {
        this.listener = listener
    }

    interface SMSReceivedListener {
        fun onMessageReceived(msg: SMSMessage)
    }
}
