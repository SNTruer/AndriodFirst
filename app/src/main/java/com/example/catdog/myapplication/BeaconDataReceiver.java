package com.example.catdog.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by MyeongJun on 2015. 8. 14..
 */
public class BeaconDataReceiver extends BroadcastReceiver {
    public ArrayList<BeaconData> beaconList;
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";

    public BeaconDataReceiver(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_LOCAL);
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(BROADCAST_LOCAL))
        {
            beaconList = (ArrayList)intent.getSerializableExtra("BeaconData");
        }
    }
}
