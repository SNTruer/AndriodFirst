package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import at.markushi.ui.CircleButton;

/**
 * Created by MyeongJun on 2015. 7. 25..
 */
public class FirstFragment extends Fragment {
    private BeaconService beaconService;
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;
    private View view;
    private Intent serviceIntent;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            beaconService=((BeaconService.LocalBinder)service).getService();

            Toast.makeText(FirstFragment.this.getActivity(),"서비스 시작",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            beaconService=null;
            Toast.makeText(FirstFragment.this.getActivity(),"서비스 종료",Toast.LENGTH_LONG).show();
        }
    };

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
        ImageButton btn = (ImageButton)view.findViewById(R.id.search_beacon_button);
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
        ImageButton btn = (ImageButton)view.findViewById(R.id.getgroupbutton);
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


    private void initServiceBtn(){
        final CircleButton btn = (CircleButton)view.findViewById(R.id.servicebutton);
        final TextView textView = (TextView)view.findViewById(R.id.servicetext);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(beaconService==null) {
                        serviceIntent=new Intent(getActivity(),BeaconService.class);
                        //getActivity().startService(serviceIntent);
                        getActivity().bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
                        //getActivity().startService(new Intent(getActivity(), ClientService.class));
                        btn.setColor(Color.RED);
                        textView.setText("서비스를 종료하시려면 버튼을 눌러주세요");
                    }
                    else{
                        //getActivity().stopService(serviceIntent);
                        //serviceIntent=null;
                        getActivity().unbindService(serviceConnection);
                        beaconService=null;
                        btn.setColor(Color.rgb(Integer.parseInt("99",16),Integer.parseInt("CC",16),Integer.parseInt("00",16)));
                        textView.setText("서비스를 시동해주십시오");
                    }
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);

        initFragment();
        initSearchBtn();
        initGetGroupBtn();
        initServiceBtn();

        // 브로드캐스트 테스트 전용 버튼
        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent location = new Intent("com.example.catdog.myapplication.LOCATE");
                location.putExtra("ID", "갱신할 비콘 아이디");
                getActivity().sendBroadcast(location);
            }
        });

        // 서비스 테스트 전용 버튼
        view.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new Intent(getActivity(), ClientService.class));
            }
        });

        return view;
    }
}
