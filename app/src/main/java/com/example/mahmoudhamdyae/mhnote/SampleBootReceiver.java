package com.example.mahmoudhamdyae.mhnote;

import android.content.Context;
import android.content.Intent;

/*import BroadcastReceiver;*/

public class SampleBootReceiver /*extends BroadcastReceiver */{

    /*@Override*/
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
        }
    }
}
