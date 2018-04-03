package hu.bme.iit.dynamiclayout_prototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Stealth on 2018. 04. 03..
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent codeIntent = new Intent(context, MainActivity.class);
            codeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            codeIntent.putExtra("bootStart",true);
            context.startActivity(codeIntent);
        }
    }
}
