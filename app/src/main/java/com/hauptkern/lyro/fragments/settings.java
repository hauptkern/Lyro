package com.hauptkern.lyro.fragments;
import static android.content.Context.UI_MODE_SERVICE;

import com.hauptkern.lyro.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class settings extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SwitchPreference appearance = findPreference("switch_preference_1");
        SharedPreferences pref = getActivity().getSharedPreferences("dark_mode",0);
        SharedPreferences.Editor editor = pref.edit();
        appearance.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString()=="true"){
                    editor.putString("dark_mode","enabled");
                    editor.commit();
                    Toast.makeText(getContext(), "Changes will be applied on next start.", Toast.LENGTH_SHORT).show();
                }
                else if (newValue.toString()=="false"){
                    editor.putString("dark_mode","disabled");
                    editor.commit();
                    Toast.makeText(getContext(), "Changes will be applied on next start.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}