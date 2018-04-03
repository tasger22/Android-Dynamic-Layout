package hu.bme.iit.dynamiclayout_prototype;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;


/**
 * Created by Stealth on 2018. 04. 03..
 */

public class SettingsActivity extends Activity {

    public static final String KEY_PREF_TESTMODE = "pref_testmode";
    public static final String KEY_PREF_USERCODE = "pref_usercode";
    public static final String KEY_PREF_CODEINPUT = "pref_codeinput";
    public static final String KEY_PREF_DIFFICULTY = "pref_difficulty";
    public static final String KEY_PREF_LAYOUT = "pref_layout";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }
    public static class SettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
