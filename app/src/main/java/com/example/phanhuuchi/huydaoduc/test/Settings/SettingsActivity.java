package com.example.phanhuuchi.huydaoduc.test.Settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_LOCKSCREEN_SWITCH = "locksreen_switch";
    public static final String KEY_PREF_MUTESOUND_SWITCH = "mute_sound";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, new SettingsFragment())
                .commit();


        // VD để lấy setting values
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean sWitchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOCKSCREEN_SWITCH,false);

    }


}
