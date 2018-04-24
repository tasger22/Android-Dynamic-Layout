package hu.bme.iit.dynamiclayout_prototype;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by Stealth on 2018. 04. 03..
 */

//Activity for the settings menu containing a fragment
// for the menu view, also the necessary static String variables when looking for specific options
public class SettingsActivity extends Activity {

    public static final String KEY_PREF_TESTMODE = "pref_testmode"; //Key for a boolean, setting whether we want to test or not
    public static final String KEY_PREF_USERCODE = "pref_usercode"; //Key for a boolean, setting to use user defined security code or not
    public static final String KEY_PREF_CODEINPUT = "pref_codeinput"; // Key for a string, user defined code (default: 0000)
    public static final String KEY_PREF_DIFFICULTY = "pref_difficulty"; //Key for a string, string which defines the difficulty of the code input method
    public static final String KEY_PREF_LAYOUT = "pref_layout"; //Key for a string, should be either "numeric" or "graphic"

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }
    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(KEY_PREF_CODEINPUT)){
                String customSecurityCode = sharedPreferences.getString(KEY_PREF_CODEINPUT,"0000");
                CryptClass encrypter = new CryptClass();

                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                if(!customSecurityCode.equals("")) {
                    try {
                        byte[] stringByteArray = encrypter.encrypt(customSecurityCode);
                        String encryptedStr = CryptClass.byteArrayToHexString(stringByteArray);
                        sharedPreferencesEditor.putString(getResources().getString(R.string.encrypted_code_key), encryptedStr);
                        sharedPreferencesEditor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                sharedPreferencesEditor.putString(KEY_PREF_CODEINPUT,""); //We clear the SharedPreferences to never see the cleartext security code
                sharedPreferencesEditor.apply();
            }
        }
    }
}
