package kr.co.noob.copyclient.uilts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import kr.co.noob.copyclient.SmsListener

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val isStartOnBoot = pref.getBoolean("key_onboot", false)
                if (isStartOnBoot) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(Intent(context, SmsListener::class.java))
                    }
                    else {
                        context.startService(Intent(context, SmsListener::class.java))
                        pref.edit().putBoolean("key_service", true).apply()
                    }
                }
            }
        }
    }
}
