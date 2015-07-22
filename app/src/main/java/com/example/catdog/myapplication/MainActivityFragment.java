package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.*;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogRecord;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner m_BluetoothLeScanner;
    private BluetoothLeAdvertiser m_BluetoothLeAdvertiser;
    private static final int REQUEST_ENABLE_BT = 1;
    public View view;
    public HashMap<String, BeaconData> BeaconHash = new HashMap<String, BeaconData>();
    public BeaconListAdapter BtListAdapter;
    TextView Test;
    ListHandler LHandler = new ListHandler();

    class ListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            BtListAdapter.notifyDataSetChanged();
        }
    }

    public MainActivityFragment() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void FirstSetting()
    {
        if(m_BluetoothAdapter!=null){
            if(!m_BluetoothAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        BtListAdapter = new BeaconListAdapter(getActivity().getApplicationContext());
        ListView BtList = (ListView) view.findViewById(R.id.listview);
        BtList.setAdapter(BtListAdapter);
        m_BluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device.getName() == null) return;
                Analyzed_Packet pkt = new Analyzed_Packet(rssi,scanRecord);
                BeaconData data = BeaconHash.get(pkt.Uuid);
                if(data == null)
                {
                    data = new BeaconData(pkt.Uuid,pkt.MajorId,pkt.MinorId,pkt.Distance);
                    BeaconHash.put(pkt.Uuid, data);
                    BtListAdapter.AddItem(data);
                    LHandler.sendMessage(new Message());
                }
                else
                {
                    data.Distance=pkt.Distance;
                    LHandler.sendMessage(new Message());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        FirstSetting();
        return view;
    }
}
