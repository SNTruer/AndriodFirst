package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MyeongJun on 2015. 8. 1..
 */
public class BeaconDataSender extends Thread {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;
    public View view;
    public boolean stopSw;
    private HashMap<String, BeaconData> m_beaconHash = new HashMap<>();
    private ArrayList<BeaconData> m_beaconData = new ArrayList<>();
    private Context context;
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";
    //private BeaconListAdapter m_btListAdapter;

    public BeaconDataSender(Context context)
    {
        this.context=context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void run(){
        m_BluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device.getName() == null) return;
                AnalyzedPacket pkt = new AnalyzedPacket(rssi,scanRecord);
                BeaconData data = m_beaconHash.get(pkt.Uuid + pkt.MajorId + pkt.MinorId);
                if(data == null)
                {
                    data = new BeaconData(pkt.Uuid,pkt.MajorId,pkt.MinorId,pkt.Distance);
                    m_beaconHash.put(pkt.Uuid + pkt.MajorId + pkt.MinorId, data);
                    m_beaconData.add(data);
                    //m_btListAdapter.AddItem(data);
                    //LHandler.sendMessage(new Message());
                }
                else
                {
                    data.Distance=pkt.Distance;
                    //LHandler.sendMessage(new Message());
                }
            }
        });
        while(!stopSw)
        {
            try {
                sleep(500);
                sendBroadCast();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendBroadCast()
    {
        Intent intent = new Intent(BROADCAST_LOCAL);
        intent.putExtra("BeaconData",m_beaconData);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
