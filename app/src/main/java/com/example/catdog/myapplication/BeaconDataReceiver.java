package com.example.catdog.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Created by MyeongJun on 2015. 8. 14..
 */
public class BeaconDataReceiver extends BroadcastReceiver {
    public static ArrayList<BeaconData> beaconList;
    private static String nowMap;
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";
    private static final String EMERGENCY_CALL = "swmaestro.ship.broadcast.emergency";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case EMERGENCY_CALL:break;
            case BROADCAST_LOCAL:
            //beaconList = (ArrayList<BeaconData>) intent.getSerializableExtra("BeaconData");
            BeaconData data = (BeaconData) intent.getSerializableExtra("BeaconData");
            if(data==null) return;
            String key = data.GroupIdx + "-" + data.MapIdx;
            if (nowMap == null || !nowMap.equals(key)) {
                Log.d("whatthe", nowMap + ":" + key);
                nowMap = key;
                Intent mapIntent = new Intent(context, MapViewActivity.class);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mapIntent.putExtra("groupIdx",data.GroupIdx);
                mapIntent.putExtra("mapIdx",data.MapIdx);
                context.startActivity(mapIntent);
            }break;
            default:break;
        }
    }
}
