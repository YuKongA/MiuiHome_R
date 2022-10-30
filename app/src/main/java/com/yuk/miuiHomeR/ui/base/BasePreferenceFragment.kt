package com.yuk.miuiHomeR.ui.base

import android.content.Context
import android.os.Bundle
import com.yuk.miuiHomeR.utils.PrefsUtils
import com.yuk.miuiHomeR.utils.ktx.getLocale
import com.yuk.miuiHomeR.utils.ktx.setLocale
import moralnorm.preference.PreferenceFragmentCompat
import moralnorm.preference.PreferenceManager

open class BasePreferenceFragment : PreferenceFragmentCompat() {

    override fun onAttach(context: Context) {
        super.onAttach(setLocale(context, getLocale(context)))
    }

    fun onCreate(savedInstanceState: Bundle?, prefs_default: Int) {
        super.onCreate(savedInstanceState)
        try {
            preferenceManager.sharedPreferencesName = PrefsUtils.mPrefsName
            preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
            preferenceManager.setStorageDeviceProtected()
            PreferenceManager.setDefaultValues(requireActivity(), prefs_default, false)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {}
}