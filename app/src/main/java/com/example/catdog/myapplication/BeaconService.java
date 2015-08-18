package com.example.catdog.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by MyeongJun on 2015. 8. 14..
 */
public class BeaconService extends Service implements Runnable {
    private BeaconDataReceiver receiver;
    private BeaconDataSender sender;
    private HashMap<String, BeaconData> beaconDataHashMap;
    ArrayList<GroupData> groupList;
    private HashMap<Integer, HashMap<Integer, MapData> > groupMapHash;
    ArrayList<BeaconData> beaconList;
    private String nowMap;
    private String parameter;

    @Override
    public void run() {
        nowMap = new String("");
        while(true){
            beaconList=receiver.beaconList;
            if(beaconList==null) continue;
            synchronized (this){
                Iterator<BeaconData> iterator = beaconList.iterator();
                while(iterator.hasNext()) {
                    BeaconData data = iterator.next();
                    String key = data.Uuid + "-" + data.MajorId + "-" + data.MinorId;
                    BeaconData getData = beaconDataHashMap.get(key);
                    if (getData==null) continue;
                    if(!nowMap.equals(getData.GroupIdx.toString() + "-" + getData.MapIdx.toString())){
                        Log.d("map",nowMap + " 에서 " + getData.GroupIdx.toString() + "-" + getData.MapIdx.toString() + " 로");
                        callMap(getData.GroupIdx,getData.MapIdx);
                    }
                    break;
                }
            }
        }
    }

    private void callMap(final Integer groupIdx,final Integer mapIdx){
        if(groupMapHash.get(groupIdx)==null){
            try {
                parameter = URLEncoder.encode("group_idx", "UTF-8") + "=" + ((Integer)groupIdx).toString();
                nowMap = ((Integer)(groupIdx)).toString() + "-" + ((Integer)mapIdx).toString();
                ServerUtill.mapRequest(parameter, new ServerUtill.OnComplete() {
                    @Override
                    public void onComplete(byte[] byteArray) {
                        try {
                            groupMapHash.put(groupIdx, MapData.getMapHashMapFromDom(DomChanger.byteToDom(byteArray)));
                            Intent actIntent = new Intent(BeaconService.this,MapViewActivity.class);
                            actIntent.putExtra("imageUrl", groupMapHash.get(groupIdx).get(mapIdx).imageUrl);
                            actIntent.putExtra("mapDetailString", groupMapHash.get(groupIdx).get(mapIdx).mapDetailString);
                            actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(actIntent);
                            BeaconService.this.stopSelf();
                        } catch (Exception e) {
                            Log.d("map",e.toString());
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            nowMap = ((Integer)(groupIdx)).toString() + "-" + ((Integer)mapIdx).toString();
            Intent actIntent = new Intent(this,MapViewActivity.class);
            actIntent.putExtra("imageUrl", groupMapHash.get(groupIdx).get(mapIdx).imageUrl);
            actIntent.putExtra("mapDetailString", groupMapHash.get(groupIdx).get(mapIdx).mapDetailString);
            actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(actIntent);
            this.stopSelf();
        }
    }

    @Override
    public void onCreate(){
        //1.서버 API로 비콘과 그 비콘이 속해있는 그룹, 맵 리스트를 갖고 옴
        //2.블루투스 Lescan으로 지속적으로 데이터를 갖고 옴.
        //3.비콘이 존재하면 그 비콘이 존재하는 맵 액티비티를 띄워 줌
        init();
        Thread thread = new Thread(this);
        thread.start();
    }

    private void init(){
        //요 부분에서 맵 리스트를 갖고 올 것.

        groupMapHash = new HashMap<>();

        beaconDataHashMap = new HashMap<>();
        BeaconData data1 = new BeaconData(1,1,
                "http://www.webengine.co.kr/Escape//map_image/0263/0366/0156/0042/7b0c1632a6e0eea74c79897516a0d2a1.gif"
        );
        beaconDataHashMap.put("24ddf4118cf1440c87cde368daf9c93e-18244-17731",data1);
        beaconDataHashMap.put("24ddf4118cf1440c87cde368daf9c93e-18249-18247",data1);
        data1 = new BeaconData(1,1,
                "http://www.webengine.co.kr/Escape//map_image/0263/0366/0156/0042/7b0c1632a6e0eea74c79897516a0d2a1.gif"
        );
        beaconDataHashMap.put("24ddf4118cf1440c87cde368daf9c93e-18249-18247",data1);

        receiver = new BeaconDataReceiver(this);
        sender = new BeaconDataSender(this);
        sender.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
