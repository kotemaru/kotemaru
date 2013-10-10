package org.kotemaru.android.adkterm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class PrefActivity extends PreferenceActivity {
	private PrefFragment fragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new PrefFragment(this);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
	}
	public void setChanged(boolean b) {
		Intent intent = new Intent();
		setResult(b ? RESULT_OK : RESULT_CANCELED, intent);
	}

	public static class PrefFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener
	{
		private PrefActivity activity;
		
		public PrefFragment(PrefActivity activity) {
			super();
			this.activity = activity;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.adkterm_pref); // res/xml/adkterm_pref.xml
		}

		@Override
		public void onResume() {
			super.onResume();
			resetSummary();
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString) {
			resetSummary();
			activity.setChanged(true);
		}

		public void resetSummary() {
			SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
			PreferenceScreen screen = this.getPreferenceScreen();
			for (int i = 0; i < screen.getPreferenceCount(); i++) {
				Preference pref = screen.getPreference(i);
				if (pref instanceof CheckBoxPreference) continue;

				String key = pref.getKey();
				String val = sharedPrefs.getString(key, "");
				pref.setSummary(val);
			}
		}

	}

}