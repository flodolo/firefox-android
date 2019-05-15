/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.mozilla.fenix.R
import org.mozilla.fenix.utils.Settings

class AccessibilityFragment : PreferenceFragmentCompat() {
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).title = getString(R.string.preferences_accessibility)
        (activity as AppCompatActivity).supportActionBar?.show()
        val textSizePreference =
            findPreference<PercentageSeekBarPreference>(getString(R.string.pref_key_accessibility_font_scale))
        textSizePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                (newValue as? Int).let {
                    val newTextScale = (newValue as Int).toFloat() / PERCENT_TO_DECIMAL
                    Settings.getInstance(context!!).setFontSizeFactor(newTextScale)
                }
                true
            }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.accessibility_preferences, rootKey)
    }

    companion object {
        const val PERCENT_TO_DECIMAL = 100f
    }
}
