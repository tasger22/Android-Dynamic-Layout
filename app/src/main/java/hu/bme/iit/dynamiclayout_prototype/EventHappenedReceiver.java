package hu.bme.iit.dynamiclayout_prototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Stealth on 2018. 04. 03..
 */

public class EventHappenedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {

            if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) context.startService(new Intent(context,ScreenOnWatcherService.class));

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            CodeDialogBase dialogBase;

            String layoutFromSettings = settings.getString(SettingsActivity.KEY_PREF_LAYOUT,"numeric");
            boolean isLockScreenEnabled = settings.getBoolean(SettingsActivity.KEY_PREF_LOCKSCREEN,false);
            if (isLockScreenEnabled){
                if(layoutFromSettings.equals("numeric")) dialogBase = new NumericCodeDialog(context,true);
                else dialogBase = new GraphicCodeDialog(context,true);
                dialogBase.show();
            }
        }
    }
}
