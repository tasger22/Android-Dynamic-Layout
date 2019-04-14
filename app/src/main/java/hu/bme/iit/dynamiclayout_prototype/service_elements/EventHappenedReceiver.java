package hu.bme.iit.dynamiclayout_prototype.service_elements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import hu.bme.iit.dynamiccodedialog.CodeDialogBase;
import hu.bme.iit.dynamiclayout_prototype.CryptographyImplementation;
import hu.bme.iit.dynamiclayout_prototype.NumericCodeDialog;
import hu.bme.iit.dynamiclayout_prototype.SettingsActivity;

/**
 * Created by Stealth on 2018. 04. 03..
 */

public class EventHappenedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
            context.startService(new Intent(context,ScreenOnWatcherService.class));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        CodeDialogBase dialogBase;

        String layoutFromSettings = settings.getString(SettingsActivity.KEY_PREF_LAYOUT,"numeric");
        boolean isLockScreenEnabled = settings.getBoolean(SettingsActivity.KEY_PREF_LOCKSCREEN,false);
        if (isLockScreenEnabled){
            if("numeric".equals(layoutFromSettings))
            {
                dialogBase = new NumericCodeDialog(context,true,settings, new CryptographyImplementation());
                dialogBase.show();
            }

        }
    }
}
