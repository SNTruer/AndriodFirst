package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.*;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends android.app.Fragment {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner m_BluetoothLeScanner;
    private BluetoothLeAdvertiser m_BluetoothLeAdvertiser;
    private static final int REQUEST_ENABLE_BT = 1;
    public View view;
    private HashMap<String, BeaconData> m_beaconHash = new HashMap<>();
    private BeaconListAdapter m_btListAdapter;
    TextView Test;
    ListHandler LHandler = new ListHandler();

    class ListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            m_btListAdapter.notifyDataSetChanged();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        m_btListAdapter = new BeaconListAdapter(getActivity().getApplicationContext());
        ListView BtList = (ListView) view.findViewById(R.id.listview);
        BtList.setAdapter(m_btListAdapter);
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
                    m_btListAdapter.AddItem(data);
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

        //FirstSetting();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
        return view;
    }
}
