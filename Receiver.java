package net.akg.com.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    public Receiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationService.class));
    }

}
