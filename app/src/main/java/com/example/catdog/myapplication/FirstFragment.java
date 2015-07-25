package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by MyeongJun on 2015. 7. 25..
 */
public class FirstFragment extends Fragment {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;
    private View view;

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

    private void ButtonSetting()
    {
        Button btn = (Button)view.findViewById(R.id.search_beacon_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frg = new MainActivityFragment();
                FragmentTransaction trs = getFragmentManager().beginTransaction();
                trs.replace(R.id.firstlayout,frg);
                trs.addToBackStack(null);

                trs.commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);

        FirstSetting();
        ButtonSetting();

        return view;
    }
}
