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

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner m_BluetoothLeScanner;
    private BluetoothLeAdvertiser m_BluetoothLeAdvertiser;
    private static final int REQUEST_ENABLE_BT = 1;
    public ArrayList<String> BtSearchedList = new ArrayList<String>();
    public View view;
    public ArrayAdapter<String> BtSearchedListAdapter;
    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BtSearchedList.add(device.getName() + " : " + device.getAddress());
                BtSearchedListAdapter.notifyDataSetChanged();
            }
        }
    };
    TextView Test;

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
        try {
            //m_BluetoothLeScanner = m_BluetoothAdapter.getBluetoothLeScanner();
            //m_BluetoothLeAdvertiser = m_BluetoothAdapter.getBluetoothLeAdvertiser();
        }catch(Exception e)
        {
            Log.e("Error!!!!",e.getMessage());
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ListView BtList = (ListView) view.findViewById(R.id.listview);
        //BtSearchedList.add("First");
        BtSearchedListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, BtSearchedList);
        BtList.setAdapter(BtSearchedListAdapter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().getApplicationContext().registerReceiver(mReciever, filter);
        m_BluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if(device.getName() == null) return;
                BtSearchedList.add(device.getUuids() + ":" + device.getName() + ":" + rssi);
                BtSearchedListAdapter.notifyDataSetChanged();
            }
        });
        /*m_BluetoothLeScanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BtSearchedList.add(result.getRssi() + ":" + result.getDevice() + ":");
                BtSearchedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        });*/
    }

    public void onResume()
    {
        super.onResume();
    }

    private void FindDevice() {
        m_BluetoothAdapter.startDiscovery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        Test = (TextView)view.findViewById(R.id.TestText);

        FirstSetting();
        return view;
    }
}
