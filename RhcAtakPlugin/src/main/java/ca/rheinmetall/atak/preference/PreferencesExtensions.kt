package ca.rheinmetall.atak.preference

import android.content.SharedPreferences

object PreferencesExtensions {
    fun SharedPreferences.getStringPreference(keyValueProperty: KeyValueProperty): String {
        return getString(keyValueProperty.key, keyValueProperty.defaultValue) ?: keyValueProperty.defaultValue
    }

    fun SharedPreferences.updateString(keyValueProperty: KeyValueProperty, value: String) {
        edit().putString(keyValueProperty.key, value).apply()
    }
}