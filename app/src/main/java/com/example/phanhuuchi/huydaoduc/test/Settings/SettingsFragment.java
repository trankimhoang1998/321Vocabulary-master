package com.example.phanhuuchi.huydaoduc.test.Settings;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.phanhuuchi.huydaoduc.test.Main.LockScreenService;
import com.example.phanhuuchi.huydaoduc.test.R;

/**
 * A simple {@link Fragment} subclass.
 *   If you're developing for Android 3.0 (API level 11) and higher, you should use a PreferenceFragment to display your list of Preference objects.
 * You can add a PreferenceFragment to any activity—you don't need to use PreferenceActivity.
 *   Fragments provide a more flexible architecture for your application, compared to using activities alone, no matter what kind of activity you're building. As such,
 * we suggest you use PreferenceFragment to control the display of your settings instead of PreferenceActivity when possible.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    // thêm fragment này vào settingActivity để show preference thay vì show fragment screen

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.preferences,rootKey);
    }



    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        Boolean lockSreenSWitchPref = getPreferenceManager().getSharedPreferences().getBoolean(SettingsActivity.KEY_PREF_LOCKSCREEN_SWITCH,false);
        if(lockSreenSWitchPref == true)
            getActivity().startService(new Intent(getContext(), LockScreenService.class));
        else
            getActivity().stopService(new Intent(getContext(), LockScreenService.class));
        return super.onPreferenceTreeClick(preference);
    }
}
