package kr.co.noob.copyclient

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kr.co.noob.copyclient.uilts.NotificationManager

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, GeneralPreferenceFragment()).commit()
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {

        private lateinit var notificationManager: NotificationManager
        private lateinit var serviceStatusListener: BroadcastReceiver

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            // Check if app has permission to receiver sms
            val permission = ContextCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.RECEIVE_SMS)
            if(permission == PackageManager.PERMISSION_DENIED) {
                // Permission not granted
                ActivityCompat.requestPermissions(activity, Array(1){ android.Manifest.permission.RECEIVE_SMS }, REQUEST_CODE)
            }

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("key_regex"))
            bindPreferenceSummaryToValue(findPreference("key_ip"))
            bindPreferenceSummaryToValue(findPreference("key_port"))

            notificationManager = NotificationManager(activity.applicationContext)

            val service = findPreference("key_service") as SwitchPreference
            service.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        val switch = newValue as Boolean
                        if(switch) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                activity.applicationContext.startForegroundService(Intent(context, SmsListener::class.java))
                            else
                                activity.applicationContext.startService(Intent(activity.applicationContext, SmsListener::class.java))
                        }
                        else {
                            activity.stopService(Intent(activity.applicationContext, SmsListener::class.java))
                            notificationManager.cancelNotification()
                        }
                        true
                    }

            // Change switch to false by observe connection
            serviceStatusListener = object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    service.isChecked = false
                }
            }
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_DISCONNECTED)
            activity.applicationContext.registerReceiver(serviceStatusListener, intentFilter)
        }

        override fun onDestroy() {
            super.onDestroy()
            activity.applicationContext.unregisterReceiver(serviceStatusListener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(applicationContext, "메세지를 읽기 위해 권한을 허가해주세요.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {

        const val REQUEST_CODE = 31331
        const val ACTION_DISCONNECTED = "kr.co.noob.copyclient.DISCONNECTED"
        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null
                )

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }
    }
}
