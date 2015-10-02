package com.yyz.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.util.Map;


public class SettingsActivityFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String LOG_TAG =SettingsActivityFragment.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

         if("checksorting_key".equals(preference.getKey())) {
             CheckBoxPreference checkBoxPreference = (CheckBoxPreference)findPreference("checksorting_key");
             ListPreference sorting_pref = (ListPreference)findPreference("sort_by");
            // System.out.println(preference.getKey());
             sorting_pref.setEnabled(checkBoxPreference.isChecked());

         }
                 // TODO Auto-generated method stub
                return super.onPreferenceTreeClick(preferenceScreen, preference);
             }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref == null) {
            return;
        }

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            listPref.setSummary(listPref.getEntry().toString());
        }
    }

    private void triggerSharePreferencesChange() {
        SharedPreferences pref = getPreferenceScreen().getSharedPreferences();
        Map<String,?> keys = pref.getAll();

        if (keys == null) {
            return;
        }

        for(String key : keys.keySet()) {
            onSharedPreferenceChanged(pref, key);
        }
        Log.e(LOG_TAG, "triggerSharePreferencesChange");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "OnResume");
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        triggerSharePreferencesChange();


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(LOG_TAG, "OnPause");
    }

    @Override
    public void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
        Log.e(LOG_TAG, "OnDestroy");
    }


}
