package org.kotemaru.android.nnalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;

public class PrefActivity extends FragmentActivity {
	private PrefFragment fragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new PrefFragment();
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
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref); // res/xml/adkterm_pref.xml
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
			((PrefActivity) getActivity()).setChanged(true);
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

	public static class Config {
		public final static String K_HOST = "host";
		public final static String K_POST = "port";
		public final static String K_SOUND = "sound";
		public final static String K_VIFRATION = "vibration";

		private static SharedPreferences sharedPrefs;

		public static void init(Context context) {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		public static String getHost() {
			return sharedPrefs.getString(K_HOST, "kote.dip.jp");
		}
		public static int getPost() {
			return Integer.parseInt(sharedPrefs.getString(K_POST, "9001"));
		}

		public static Boolean isSound() {
			return sharedPrefs.getBoolean(K_SOUND, true);
		}
		public static Boolean isVibration() {
			return sharedPrefs.getBoolean(K_VIFRATION, true);
		}
	}
}