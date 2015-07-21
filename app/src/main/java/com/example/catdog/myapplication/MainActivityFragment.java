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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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

    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    public static short Get_Short_From_Byte(byte firstByte,byte secondByte)
    {
        return (short)((short)(firstByte<<8) + (short)(secondByte));
    }

    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
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
            final int UuidOffset = 9;
            final int MajorOffset = 25;
            final int MinorOffset=27;
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                String decodedRecord = null;
                if (device.getName() == null) return;
                String Uuid = byteArrayToHex(Arrays.copyOfRange(scanRecord,UuidOffset,16));
                short majorId = Get_Short_From_Byte(scanRecord[MajorOffset],scanRecord[MajorOffset+1]);
                short minorId = Get_Short_From_Byte(scanRecord[MinorOffset],scanRecord[MinorOffset+1]);
                BtSearchedList.add(device.getName() + ":" + Uuid + ":" + majorId + ":" + minorId);
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
