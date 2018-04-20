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
            String layoutSetting = settings.getString(SettingsActivity.KEY_PREF_LAYOUT,"numeric"); //The second parameter is "numeric" which defaults NumericCodeActivity as used CodeActivity

            Intent codeIntent;

            if(layoutSetting.equals("numeric")) codeIntent = new Intent(context, NumericCodeActivity.class); //TODO: Find a better way to check the set layout
            else codeIntent = new Intent(context, GraphicCodeActivity.class);

            codeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            codeIntent.putExtra("broadcastReceiverStart",true);
            context.startActivity(codeIntent);
        }
    }
}
