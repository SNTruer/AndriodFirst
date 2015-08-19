package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by MyeongJun on 2015. 8. 14..
 */
public class BeaconService extends Service implements Runnable {
    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static boolean onlyOneFlag;
    private BeaconDataReceiver receiver;
    private BeaconDataSender sender;
    private HashMap<String, BeaconData> beaconDataHashMap;
    private Hashtable<String, BeaconData> listBeaconHash;
    private HashMap<Integer, HashMap<Integer, MapData> > groupMapHash;
    Vector<BeaconData> beaconList;
    private Thread serviceThread;
    private String nowMap;
    private String parameter;
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";

    @Override
    public void run() {
        while(true){
            try {
                synchronized (beaconList) {
                    Collections.sort(beaconList);
                }
                Intent intent = new Intent(BROADCAST_LOCAL);
                intent.putExtra("BeaconDataVector",beaconList);
                sendBroadcast(intent);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                Log.d("whatthe",e.toString());
            }
        }
    }

    @Override
    public void onCreate(){
        //1.서버 API로 비콘과 그 비콘이 속해있는 그룹, 맵 리스트를 갖고 옴
        //2.블루투스 Lescan으로 지속적으로 데이터를 갖고 옴.
        //3.비콘이 존재하면 그 비콘이 존재하는 맵 액티비티를 띄워 줌

        init();
        startBeaconSearch();
        serviceThread=new Thread(this);
        serviceThread.start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void startBeaconSearch(){
        m_BluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device.getName() == null) return;
                AnalyzedPacket pkt = new AnalyzedPacket(rssi, scanRecord);

                //beaconDataHashMap은 서버 DB에서 비콘 정보를 가져온 데이터를 담고 있음, 이것이 null이라면 우리쪽이랑 상관없는 비콘이란 것이므로 패스
                BeaconData data = beaconDataHashMap.get(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId);
                if(data==null) return;

                //listBeaconHash는 내 주변의 비콘의 정보를 가진 map임.
                data = listBeaconHash.get(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId);
                if (data == null) {
                    data = beaconDataHashMap.get(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId);
                    data.Distance=pkt.Distance;
                    listBeaconHash.put(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId,data);
                    beaconList.add(data);
                } else {
                    data.Distance = pkt.Distance;
                }
            }
        });
    }

    private void init(){
        //요 부분에서 맵 리스트를 갖고 올 것.

        groupMapHash = new HashMap<>();

        beaconDataHashMap = new HashMap<>();
        BeaconData data1 = new BeaconData(1,1,
                "http://www.webengine.co.kr/Escape//map_image/0263/0366/0156/0042/7b0c1632a6e0eea74c79897516a0d2a1.gif"
        );
        data1.Uuid="24ddf4118cf1440c87cde368daf9c93e";
        data1.MajorId=18244;
        data1.MinorId=17731;
        beaconDataHashMap.put("24ddf4118cf1440c87cde368daf9c93e-18244-17731",data1);
        data1 = new BeaconData(1,1,
                "http://www.webengine.co.kr/Escape//map_image/0263/0366/0156/0042/7b0c1632a6e0eea74c79897516a0d2a1.gif"
        );
        data1.Uuid="24ddf4118cf1440c87cde368daf9c93e";
        data1.MajorId=18249;
        data1.MinorId=18247;
        beaconDataHashMap.put("24ddf4118cf1440c87cde368daf9c93e-18249-18247", data1);

        listBeaconHash=new Hashtable<>();
        beaconList=new Vector<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("suck","서비스 파괴");
        serviceThread.interrupt();
    }
}
