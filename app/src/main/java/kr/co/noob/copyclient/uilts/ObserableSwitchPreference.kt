package kr.co.noob.copyclient.uilts

import android.content.Context
import android.content.SharedPreferences
import android.database.ContentObserver
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Switch

class ObserableSwitchPreference(context: Context?) : SwitchPreference(context) {

    val pref = PreferenceManager.getDefaultSharedPreferences(context)

    override fun onBindView(view: View?) {
        super.onBindView(view)

        if(view != null) {
            val switch = view.findViewById(android.R.id.switch_widget) as Switch

            val observer = object: ContentObserver(switch.handler) {
                override fun onChange(selfChange: Boolean) {
                    switch.isChecked = pref.getBoolean("key_service", false)
                }
            }

            switch.addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    context.contentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, observer)
                }

                override fun onViewAttachedToWindow(v: View?) {
                    context.contentResolver.unregisterContentObserver(observer)
                }

            })
        }
    }

}