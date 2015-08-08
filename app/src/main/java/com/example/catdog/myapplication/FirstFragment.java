package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by MyeongJun on 2015. 7. 25..
 */
public class FirstFragment extends Fragment {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;
    private View view;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initFragment()
    {
        if(m_BluetoothAdapter!=null){
            if(!m_BluetoothAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void initSearchBtn()
    {
        Button btn = (Button)view.findViewById(R.id.search_beacon_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frg = new MainActivityFragment();
                FragmentTransaction trs = getFragmentManager().beginTransaction();
                trs.add(R.id.firstlayout,frg);
                trs.addToBackStack(null);

                trs.commit();
            }
        });
    }

    private void initGetGroupBtn()
    {
        Button btn = (Button)view.findViewById(R.id.getgroupbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frg = new GroupListFragment();
                FragmentTransaction trs = getFragmentManager().beginTransaction();
                trs.add(R.id.firstlayout, frg);
                trs.addToBackStack(null);

                trs.commit();
            }
        });
    }

    private void initBeaconTestBtn()
    {
        Button btn = (Button)view.findViewById(R.id.broadcastbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frg = new BeaconBroadcastGetterFragment();
                FragmentTransaction trs = getFragmentManager().beginTransaction();
                trs.add(R.id.firstlayout,frg);
                trs.addToBackStack(null);

                trs.commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);

        initFragment();
        initSearchBtn();
        initGetGroupBtn();
        initBeaconTestBtn();

        return view;
    }
}
