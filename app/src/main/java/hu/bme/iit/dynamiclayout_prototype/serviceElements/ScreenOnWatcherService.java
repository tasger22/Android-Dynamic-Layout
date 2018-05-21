package hu.bme.iit.dynamiclayout_prototype.serviceElements;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

//Service which starts a Broadcast Receiver to listen for a SCREEN_ON broadcast
public class ScreenOnWatcherService extends Service {
    private BroadcastReceiver screenOnEventReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        screenOnEventReceiver = new EventHappenedReceiver();

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnEventReceiver,screenStateFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenOnEventReceiver);
    }
}
