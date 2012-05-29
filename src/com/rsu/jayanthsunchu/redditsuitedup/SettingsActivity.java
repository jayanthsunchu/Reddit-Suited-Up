package com.rsu.jayanthsunchu.redditsuitedup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	SharedPreferences shPrefs;
	SharedPreferences.Editor shEditor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		shPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		shEditor = shPrefs.edit();
		CheckBoxPreference chkCtg = (CheckBoxPreference) findPreference("clicktogo");
		chkCtg.setOnPreferenceClickListener(ctgClick);
		CheckBoxPreference chkStv = (CheckBoxPreference) findPreference("swipetovote");
		chkStv.setOnPreferenceClickListener(stvClick);
		CheckBoxPreference chkTheme = (CheckBoxPreference)findPreference("theme");
		chkTheme.setOnPreferenceClickListener(themeClickListener);
		
	}
	
	private OnPreferenceClickListener themeClickListener = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference p) {
			CheckBoxPreference c = (CheckBoxPreference) p;
			if (c.isChecked()) {
				shEditor.putString("theme", "dark");
				shEditor.commit();
			} else {
				shEditor.putString("theme", "white");
				shEditor.commit();
			}
			
			setResult(Constants.CONST_THEME_RESULT, null);
			finish();
			return true;
		}

	};

	private OnPreferenceClickListener ctgClick = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference p) {
			CheckBoxPreference c = (CheckBoxPreference) p;
			if (c.isChecked()) {
				shEditor.putBoolean("ctg", true);
				shEditor.commit();
			} else {
				shEditor.putBoolean("ctg", false);
				shEditor.commit();
			}
			return true;
		}

	};

	private OnPreferenceClickListener stvClick = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference p) {
			CheckBoxPreference c = (CheckBoxPreference) p;
			if (c.isChecked()) {
				shEditor.putBoolean("stv", true);
				shEditor.commit();
			} else {
				shEditor.putBoolean("stv", false);
				shEditor.commit();
			}
			return true;
		}

	};

}
