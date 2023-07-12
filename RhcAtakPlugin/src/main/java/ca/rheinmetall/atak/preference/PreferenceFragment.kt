package ca.rheinmetall.atak.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import ca.rheinmetall.atak.R
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.preference.PreferencesExtensions.getStringPreference
import ca.rheinmetall.atak.preference.PreferencesExtensions.updateString
import com.atakmap.android.gui.PanEditTextPreference
import com.atakmap.android.preference.PluginPreferenceFragment
import javax.inject.Inject

@SuppressLint("ValidFragment")
class PreferenceFragment @Inject constructor(
    @PluginContext private val pluginContext: Context,
    @DefaultSharedPreferences private val sharedPreferences: SharedPreferences
) : PluginPreferenceFragment(pluginContext, R.xml.preference) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateStringPreference(PreferenceEnum.API_KEY)
    }

    private fun updateStringPreference(pref: KeyValueProperty) {
        findPreference(pref.key)?.let { p ->
            (p as PanEditTextPreference).text = sharedPreferences.getStringPreference(pref)
            p.setOnPreferenceChangeListener { _, nv ->
                sharedPreferences.updateString(pref, "$nv")
                true
            }
        }
    }
}