package com.example.catdog.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ListView BtList = (ListView) view.findViewById(R.id.listview);
        //BtSearchedList.add("First");
        BtSearchedListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, BtSearchedList);
        BtList.setAdapter(BtSearchedListAdapter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().getApplicationContext().registerReceiver(mReciever,filter);
        m_BluetoothAdapter.startDiscovery();
        Button button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtSearchedList.add("Touch!");
                BtSearchedListAdapter.notifyDataSetChanged();
            }
        });
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
