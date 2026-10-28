@file:Suppress("DEPRECATION")

package io.github.nexalloy.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.PreferenceGroup
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import io.github.libxposed.service.XposedService
import io.github.nexalloy.R
import io.github.nexalloy.appPatchConfigurations

class AppPatchSettingsActivity : Activity() {

    companion object {
        const val ARGUMENT_APP_NAME = "app_name_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_patch_settings)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val appName = intent.getStringExtra(ARGUMENT_APP_NAME)
        actionBar?.title = appName

        if (savedInstanceState != null) return
        val fragment = AppPatchSettingsFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_APP_NAME, appName)
            }
        }
        fragmentManager.beginTransaction()
            .replace(R.id.app_patch_settings_container, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    class AppPatchSettingsFragment : PreferenceFragment(), SettingApplication.ServiceStateListener {

        @Deprecated("Deprecated in Java")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        private var mService: XposedService? = null

        override fun onStart() {
            super.onStart()
            SettingApplication.addServiceStateListener(this, true)
        }

        override fun onStop() {
            SettingApplication.removeServiceStateListener(this)
            super.onStop()
        }

        override fun onServiceStateChanged(service: XposedService?) {
            mService = service
            if (service == null) {
                activity.actionBar?.title = "Binder is null"
                return
            }

            activity.runOnUiThread {
                val appName = arguments?.getString(ARGUMENT_APP_NAME)
                val appPatchInfo = appPatchConfigurations.find { it.appName == appName }
                if (appPatchInfo == null) throw Exception("AppPatchInfo not found, app_name: $appName")
                val defaultPatchStates = appPatchInfo.patches.associate { it.name to it.use }

                val screen = preferenceManager.createPreferenceScreen(context)
                val remotePrefs = service.getRemotePreferences(appPatchInfo.packageName)

                // Header quick action buttons
                object : Preference(context) {
                    @Deprecated("Deprecated in Java")
                    override fun onBindView(view: View) {
                        super.onBindView(view)
                        view.findViewById<Button>(R.id.button_default).setOnClickListener {
                            restoreDefaultPreferences(remotePrefs, defaultPatchStates)
                        }
                        view.findViewById<Button>(R.id.button_none).setOnClickListener {
                            setAllPreferences(remotePrefs, false)
                        }
                        val isInstalled = runCatching {
                            context.packageManager.getPackageInfo(appPatchInfo.packageName, 0)
                        }.isSuccess

                        view.findViewById<Button>(R.id.button_app_info).apply {
                            if (!isInstalled) visibility = View.GONE
                            setOnClickListener {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:${appPatchInfo.packageName}"))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    }
                }.apply {
                    layoutResource = R.layout.preference_header_buttons
                    screen.addPreference(this)
                }

                val (recommendedPatches, experimentalPatches) = appPatchInfo.patches
                    .filter { it.name.isNotEmpty() && !it.name.startsWith("<") }
                    .partition { it.use }

                // 1. Recommended Patches Category
                if (recommendedPatches.isNotEmpty()) {
                    val recommendedCategory = PreferenceCategory(context).apply {
                        title = "Recommended Patches"
                    }
                    screen.addPreference(recommendedCategory)

                    for (patchInfo in recommendedPatches.sortedBy { it.name }) {
                        CheckBoxPreference(context).apply {
                            key = patchInfo.name
                            title = patchInfo.name
                            summary = patchInfo.description
                            isChecked = remotePrefs.getBoolean(patchInfo.name, patchInfo.use)

                            setOnPreferenceChangeListener { _, newValue ->
                                val enabled = newValue as Boolean
                                remotePrefs.edit().putBoolean(key, enabled).apply()
                                val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
                                if (vibrator?.hasVibrator() == true) {
                                    vibrator.vibrate(50)
                                }
                                true
                            }
                            recommendedCategory.addPreference(this)
                        }
                    }
                }

                // 2. Additional Patches Category
                if (experimentalPatches.isNotEmpty()) {
                    val additionalCategory = PreferenceCategory(context).apply {
                        title = "Additional Patches"
                    }
                    screen.addPreference(additionalCategory)

                    for (patchInfo in experimentalPatches.sortedBy { it.name }) {
                        CheckBoxPreference(context).apply {
                            key = patchInfo.name
                            title = patchInfo.name
                            summary = patchInfo.description
                            isChecked = remotePrefs.getBoolean(patchInfo.name, patchInfo.use)

                            setOnPreferenceChangeListener { _, newValue ->
                                val enabled = newValue as Boolean
                                remotePrefs.edit().putBoolean(key, enabled).apply()
                                val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
                                if (vibrator?.hasVibrator() == true) {
                                    vibrator.vibrate(50)
                                }
                                true
                            }
                            additionalCategory.addPreference(this)
                        }
                    }
                }

                preferenceScreen = screen
            }
        }

        fun setAllPreferences(prefs: SharedPreferences, enable: Boolean) {
            if (!isAdded) return
            val editor = prefs.edit()
            fun updatePref(pref: Preference) {
                if (pref is CheckBoxPreference) {
                    pref.isChecked = enable
                    editor.putBoolean(pref.key, enable)
                } else if (pref is PreferenceGroup) {
                    for (i in 0 until pref.preferenceCount) {
                        updatePref(pref.getPreference(i))
                    }
                }
            }
            updatePref(preferenceScreen)
            editor.apply()
        }

        fun restoreDefaultPreferences(prefs: SharedPreferences, defaultPatchStates: Map<String, Boolean>) {
            if (!isAdded) return
            val editor = prefs.edit()
            fun updatePref(pref: Preference) {
                if (pref is CheckBoxPreference) {
                    val defaultState = defaultPatchStates[pref.key] ?: pref.isChecked
                    pref.isChecked = defaultState
                    editor.putBoolean(pref.key, defaultState)
                } else if (pref is PreferenceGroup) {
                    for (i in 0 until pref.preferenceCount) {
                        updatePref(pref.getPreference(i))
                    }
                }
            }
            updatePref(preferenceScreen)
            editor.apply()
        }
    }
}
