package im.ac.ucm.memetexts.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import im.ac.ucm.memetexts.R

class SettingsFragment: PreferenceFragmentCompat() {
    /**Enables the PreferenceScreen to appear*/
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}