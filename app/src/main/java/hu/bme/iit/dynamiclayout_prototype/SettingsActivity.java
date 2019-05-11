package hu.bme.iit.dynamiclayout_prototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.Nullable;

import hu.bme.iit.dynamiccodedialog.CryptographyImplementation;

/**
 * Created by Stealth on 2018. 04. 03..
 */

//Activity for the settings menu containing a fragment
// for the menu view, also the necessary static String variables when looking for specific options
public class SettingsActivity extends Activity {
    public final static int REQUEST_CODE = 5463;

    public static final String KEY_PREF_TESTMODE = "pref_testmode"; //Key for a boolean, setting whether we want to test or not
    public static final String KEY_PREF_CODEINPUT = "pref_codeinput"; // Key for a string, user defined code (default: 0000)
    public static final String KEY_PREF_DIFFICULTY = "pref_difficulty"; //Key for a string, string which defines the difficulty of the code input method
    public static final String KEY_PREF_LAYOUT = "pref_layout"; //Key for a string, should be either "numeric" or "graphic"
    public static final String KEY_PREF_LOCKSCREEN = "pref_lockscreen"; //Key for a boolean, shows whether the app shows code dialog on lockscreen or boot

    public static String staticPackageName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        staticPackageName = getApplicationContext().getPackageName();

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
                CryptographyImplementation encrypter = new CryptographyImplementation();

                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                if(!"".equals(customSecurityCode)) {
                    try {
                        byte[] stringByteArray = encrypter.encrypt(customSecurityCode);
                        String encryptedStr = encrypter.byteArrayToHexString(stringByteArray);
                        sharedPreferencesEditor.putString(getResources().getString(R.string.encrypted_code_key), encryptedStr);
                        sharedPreferencesEditor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                sharedPreferencesEditor.putString(KEY_PREF_CODEINPUT,""); //We clear the SharedPreferences to never see the cleartext security code
                sharedPreferencesEditor.apply();
            }

            else if(key.equals(KEY_PREF_LOCKSCREEN)){
                checkDrawOverlayPermission();
            }


        }

        public void checkDrawOverlayPermission() {
            /** check if we already  have permission to draw over other apps */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getContext())) {
                    final AlertDialog.Builder drawOverAppsAlert = new AlertDialog.Builder(getContext());
                    drawOverAppsAlert.setTitle("ATTENTION!");
                    drawOverAppsAlert.setMessage("Pressing OK opens up the app settings where you have to give permission to draw over other apps");
                    drawOverAppsAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /** if not construct intent to request permission */
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + staticPackageName));
                            /** request permission via start activity for result */
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    });
                    drawOverAppsAlert.show();
                }
            }
        }
    }
}
