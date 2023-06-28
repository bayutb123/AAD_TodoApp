package com.dicoding.todoapp.setting

import android.content.Context
import android.graphics.drawable.Drawable.ConstantState
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                if (newValue == true) {
                    addReminder(channelName, requireContext())
                } else {
                    cancelReminder(requireContext())
                }
                true
            }

        }

        private fun addReminder(channelName: String, context: Context) {
            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .addTag(WORKER_TAG)
                .build()
            workManager.enqueueUniqueWork(
                channelName,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        private fun cancelReminder(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORKER_NAME)
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
    companion object {
        const val WORKER_TAG = "w_1"
        const val WORKER_NAME = "daily_notificaiton"
    }
}