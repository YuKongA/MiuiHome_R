package com.yuk.miuiHomeR.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yuk.miuiHomeR.R;
import com.yuk.miuiHomeR.ui.base.BaseAppCompatActivity;
import com.yuk.miuiHomeR.ui.base.SubFragment;
import com.yuk.miuiHomeR.utils.BackupUtils;
import com.yuk.miuiHomeR.utils.Locales;
import com.yuk.miuiHomeR.utils.PrefsUtils;

import java.util.ArrayList;
import java.util.Locale;

import moralnorm.preference.DropDownPreference;
import moralnorm.preference.Preference;
import moralnorm.preference.SwitchPreference;

public class SettingsActivity extends BaseAppCompatActivity {

    @NonNull
    @Override
    public Fragment initFragment() {
        setTitle(R.string.settings);
        return new SettingsFragment();
    }

    public static class SettingsFragment extends SubFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        SwitchPreference mHideIcon;
        DropDownPreference mLocaleSelector;
        Preference mBackupSettings;
        Preference mRestoreSettings;

        @Override
        public int getContentResId() {
            return R.xml.prefs_settings;
        }

        @Override
        public void initPrefs() {
            ArrayList<String> mLocaleName = new ArrayList<>();
            mHideIcon = findPreference("prefs_key_settings_hide_icon");
            mLocaleSelector = findPreference("prefs_key_settings_language");
            mBackupSettings = findPreference("prefs_key_settings_backup");
            mRestoreSettings = findPreference("prefs_key_settings_restore");
            var displayLocaleTags = Locales.DISPLAY_LOCALES;
            for (var displayLocale : displayLocaleTags) {
                if (displayLocale.equals("SYSTEM")) {
                    mLocaleName.add(0, getString(R.string.system_default));
                    continue;
                }
                var localizedLocale = Locale.forLanguageTag(displayLocale);
                mLocaleName.add(localizedLocale.getDisplayName(localizedLocale));
            }
            mLocaleSelector.setEntries(mLocaleName.toArray(new CharSequence[0]));

            mHideIcon.setOnPreferenceChangeListener(this);
            mLocaleSelector.setOnPreferenceChangeListener(this);
            mBackupSettings.setOnPreferenceClickListener(this);
            mRestoreSettings.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mBackupSettings) {
                BackupUtils.INSTANCE.backup(requireActivity(), PrefsUtils.mSharedPreferences);
            } else if (preference == mRestoreSettings) {
                BackupUtils.INSTANCE.recovery(requireActivity(), PrefsUtils.mSharedPreferences);
            }
            return true;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference == mHideIcon) {
                PackageManager pm = requireActivity().getPackageManager();
                int mComponentEnabledState;
                if ((Boolean) o) {
                    mComponentEnabledState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
                } else {
                    mComponentEnabledState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                }
                pm.setComponentEnabledSetting(new ComponentName(getActivity(),
                        MainActivity.class.getName() + "Alias"), mComponentEnabledState,
                        PackageManager.DONT_KILL_APP);
            } else if (preference == mLocaleSelector) {
                requireActivity().recreate();
            }
            return true;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        switch (requestCode) {
            case BackupUtils.CREATE_DOCUMENT_CODE:
                BackupUtils.INSTANCE.handleCreateDocument(this, data.getData());
                break;

            case BackupUtils.OPEN_DOCUMENT_CODE:
                BackupUtils.INSTANCE.handleReadDocument(this, data.getData());
                break;
        }
    }

}
