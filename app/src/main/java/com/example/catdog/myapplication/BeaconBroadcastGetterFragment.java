package com.example.catdog.myapplication;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyeongJun on 2015. 8. 7..
 */
public class BeaconBroadcastGetterFragment extends Fragment{
    private View view;
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";
    public static ArrayList<BeaconData> beaconList;
    public static BeaconListAdapter listAdapter;
    public ListView listView;
    private static BeaconReceiver receiver;
    private BeaconDataSender sender;

    public static class BeaconReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BROADCAST_LOCAL))
            {
                beaconList = (ArrayList)intent.getSerializableExtra("BeaconData");
                listAdapter.SetList(beaconList);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void init()
    {
        receiver = new BeaconReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_LOCAL);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);

        sender = new BeaconDataSender(getActivity().getApplicationContext());

        sender.start();

        listAdapter = new BeaconListAdapter(getActivity().getApplicationContext());
        beaconList = new ArrayList<>();
        listAdapter.SetList(beaconList);
        listView = (ListView)view.findViewById(R.id.listview);
        listView.setAdapter(listAdapter);

        //LocalBroadcastManager.getInstance().registerReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_main,container,false);

        //init();

        return view;
    }
}
