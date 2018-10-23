package kr.co.noob.copyclient.uilts

import kr.co.noob.copyclient.data.Log

object Logger {

    fun addLog(log: Log, info: String?) {
        var msg = ""
        when(log) {
            Log.ACTION_CONNECTED -> {
                msg = "Connected : $info"
            }
            Log.ACTION_DISCONNECTED -> {
                msg = "Disconnected : $info"
            }
            Log.ACTION_REJECTED -> {
                msg = "Authentication failed : $info"
            }
            Log.ACTION_ACK -> {
                msg = "Authentication succeed : $info"
            }
            Log.ACTION_SEND -> {
                msg = "Send message : $info"
            }
            Log.ACTION_RECEIVED -> {
                msg = "Message received : $info"
            }
            Log.ACTION_ERROR -> {
                msg = "Error occurred : $info"
            }
            Log.ACTION_SMS_RECEIVED -> {
                msg = "SMS received : $info"
            }
            Log.ACTION_ON_BOOT -> {
                msg = "Start service on boot : $info"
            }
        }

        val logTime = System.currentTimeMillis()
        android.util.Log.d("taggg", "test log : $msg, $logTime")
    }

}