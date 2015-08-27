package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by MyeongJun on 2015. 8. 14..
 */
public class BeaconService extends Service implements Runnable {
    private final IBinder binder = new LocalBinder();
    private static final String BROADCAST_LOCATE = "swmaestro.ship.broadcast.LOCATE";
    private static final String EMERGENCY_CALL = "swmaestro.ship.broadcast.EMERGENCYTRUE";
    private static final String EMERGENCY_FALSE = "swmaestro.ship.broadcast.EMERGENCYFALSE";
    private static final String CALL_MAP = "swmaestro.ship.broadcast.CALLMAP";
    private static final String BROADCAST_LOCAL = "swmaestro.ship.broadcast.local";
    private static final String SERIAL_UUID = "24ddf4118cf1440c87cde368daf9c93e";

    public interface BeaconChangeCallback {
        public void method(BeaconData data);
    }

    public class LocalBinder extends Binder {
        BeaconService getService() {
            return BeaconService.this;
        }
    }

    private BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private HashMap<String, BeaconData> beaconDataHashMap;
    private Hashtable<String, BeaconData> listBeaconHash;
    private HashMap<Integer, HashMap<Integer, MapData> > groupMapHash;
    private BroadcastReceiver receiver;
    Vector<BeaconData> beaconList;
    private Thread serviceThread;
    private BeaconData nearestBeacon;
    private boolean emergencyFlag = true;
    private BeaconChangeCallback changeCallback;
    private boolean stopFlag=false,sortFlag=false;
    private long time,stopTime,sortTime;
    private Integer nowMap = new Integer(0);


    private BluetoothAdapter.LeScanCallback scanCallBack;

    @Override
    public void run() {
        while(true){
            try {
                synchronized (beaconList) {
                    Collections.sort(beaconList);
                }
                Intent intent = new Intent(BROADCAST_LOCAL);
                intent.putExtra("BeaconData",beaconList.get(0));
                sendBroadcast(intent);
                Log.d("suck","서비스가 돌고 있음");
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
        tempCallMap();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void startBeaconSearch(){
        /*TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                stopBeaconSearch();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 3000);

        for(int i=1;i<=5;i++){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    synchronized (beaconList){
                        Collections.sort(beaconList);
                    }
                    if(nearestBeacon==null || beaconList.get(0)!=nearestBeacon){
                        if(beaconList==null || beaconList.size()==0) return;
                        nearestBeacon=beaconList.get(0);
                        Intent intent = new Intent(BROADCAST_LOCATE);
                        Log.d("whatthe","최근접 비콘 변경");
                        sendBroadcast(intent);
                        if(changeCallback !=null)
                        {
                            Log.d("whatthe","콜백이 널이 아님");
                            changeCallback.method(nearestBeacon);
                        }
                    }
                }
            };
            timer = new Timer();
            timer.schedule(timerTask,i*500);
        }*/
        m_BluetoothAdapter.startLeScan(scanCallBack);
    }

    public void resetMap(){
        nowMap=0;
    }

    private void beaconSort(){
        synchronized (beaconList){
            Collections.sort(beaconList);
        }
        if(nearestBeacon==null || beaconList.get(0)!=nearestBeacon){
            if(beaconList==null || beaconList.size()==0) return;
            nearestBeacon=beaconList.get(0);
            Intent intent = new Intent(BROADCAST_LOCATE);
            intent.putExtra("beacon_id",nearestBeacon.Uuid+"-"+nearestBeacon.MajorId+"-"+nearestBeacon.MinorId);
            Log.d("whatthe","최근접 비콘 변경");
            sendBroadcast(intent);
            if(changeCallback !=null)
            {
                Log.d("whatthe","콜백이 널이 아님");
                changeCallback.method(nearestBeacon);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopBeaconSearch(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startBeaconSearch();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 3000);
        m_BluetoothAdapter.stopLeScan(scanCallBack);
    }

    public void setChangeCallback(BeaconChangeCallback callback){
        Log.d("whatthe","콜백등록");
        this.changeCallback=callback;
        changeCallback.method(nearestBeacon);
    }

    public BeaconData getNearestBeacon(){
        return nearestBeacon;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action){
                    case EMERGENCY_CALL:emergencyFlag=true;break;
                    case EMERGENCY_FALSE:emergencyFlag=false;break;
                    case CALL_MAP:
                        if(!emergencyFlag) break;
                        Integer mapIdx = (Integer)intent.getSerializableExtra("mapId");
                        if(nowMap==mapIdx) return;
                        nowMap=mapIdx;
                        Intent mapIntent = new Intent(context, MapViewActivity.class);
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        //Integer groupIdx =(Integer)intent.getSerializableExtra("groupIdx");
                        //mapIntent.putExtra("groupIdx",groupIdx);
                        mapIntent.putExtra("mapId",mapIdx);
                        context.startActivity(mapIntent);
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_LOCAL);
        filter.addAction(EMERGENCY_CALL);
        filter.addAction(CALL_MAP);
        registerReceiver(receiver, filter);

        scanCallBack = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device.getName() == null) return;
                time = System.currentTimeMillis();
                if (!stopFlag) {
                    stopTime = time;
                    stopFlag = true;
                }
                if(!sortFlag){
                    sortTime=time;
                    sortFlag=true;
                }
                if (time-stopTime >= 3000) {
                    //Log.d("whatthe","비콘스톱");
                    stopFlag=false;
                    //stopBeaconSearch();
                }
                if(time-sortTime>=300){
                    //Log.d("whatthe","비콘정렬");
                    sortFlag=false;
                    beaconSort();
                }

                AnalyzedPacket pkt = new AnalyzedPacket(rssi, scanRecord);

                //beaconDataHashMap은 서버 DB에서 비콘 정보를 가져온 데이터를 담고 있음, 이것이 null이라면 우리쪽이랑 상관없는 비콘이란 것이므로 패스
                //BeaconData data = beaconDataHashMap.get(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId);
                //if (data == null) return;

                //UUID가 우리꺼랑 다르면 패스
                if (!pkt.Uuid.equals(SERIAL_UUID)){
                    //Log.d("whatthe", SERIAL_UUID + "\n" + pkt.Uuid);
                    return;
                }

                //listBeaconHash는 내 주변의 비콘의 정보를 가진 map임.
                BeaconData data = listBeaconHash.get(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId);
               // Log.d("whatthe","beaconscan");
                if (data == null) {
                    //data = beaconDataHashMap.get(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId);
                    data = new BeaconData(pkt.Uuid,pkt.MajorId,pkt.MinorId,pkt.Distance);
                    data.Distance = pkt.Distance;
                    listBeaconHash.put(pkt.Uuid + "-" + pkt.MajorId + "-" + pkt.MinorId, data);
                    beaconList.add(data);
                    //Log.d("whatthe", "beaconscan");
                } else {
                    data.Distance = pkt.Distance;
                    //Log.d("whatthe", "beaconscan");
                }
            }
        };
    }

    private void tempCallMap(){
        Intent intent = new Intent(CALL_MAP);
        intent.putExtra("mapId",new Integer(1));
        sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {
        m_BluetoothAdapter.stopLeScan(scanCallBack);
        Log.d("whatthe", "서비스 파괴");
    }
}
