package com.example.catdog.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.catdog.myapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MapViewActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private static final String CALL_MAP = "swmaestro.ship.broadcast.callmap";
    MapCustomView mapView;
    HorizontalScrollView horizontalScrollView;
    ScrollView scrollView;
    LinearLayout linearLayout;
    String imageUrl;
    String mapDetailString;
    Integer mapIdx;
    BeaconDataReceiver beaconDataReceiver;
    private BeaconService beaconService;
    int xStartPos,yStartPos;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            beaconService=((BeaconService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            beaconService=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mapIdx=(Integer)intent.getSerializableExtra("mapId");

        setContentView(R.layout.activity_show_me_the_map);

        doBindService();
        init();
    }

    private void doBindService(){
        Intent serviceIntent = new Intent(this,BeaconService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(intent!=null){
            if(mapView!=null){
                mapIdx=(Integer)intent.getSerializableExtra("mapId");
                linearLayout.removeView(mapView);
                addMapView();
            }
        }
    }

    private void addMapView(){
        String parameter = null;
        try {
            parameter = URLEncoder.encode("map_id", "UTF-8") + "=" + ((Integer)mapIdx).toString();
            ServerUtill.mapRequest(parameter,new ServerUtill.OnComplete(){

                @Override
                public void onComplete(byte[] byteArray) {
                    try {
                        MapData map = MapData.getMapDataFromDom(DomChanger.byteToDom(byteArray));
                        imageUrl = map.imageUrl;
                        mapDetailString = map.mapDetailString;
                        MapViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapView = new MapCustomView(MapViewActivity.this);
                                try {
                                    mapView.init(imageUrl, DomChanger.stringToDom(mapDetailString));
                                    beaconService.setChangeCallback(new BeaconService.BeaconChangeCallback() {
                                        @Override
                                        public void method(BeaconData data) {
                                            if (data == null) return;
                                            String key = data.Uuid + "-" + data.MajorId + "-" + data.MinorId;
                                            mapView.checkBeacon(key);
                                            mapView.zoomPointer(key);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                );
                                mapView.setLayoutParams(params);
                                linearLayout = (LinearLayout) findViewById(R.id.testlinear);
                                linearLayout.addView(mapView);
                            }
                        });
                    } catch (Exception e) {
                        Log.d("whatthe",e.toString() + "completeError");
                    }
                }
            });
        }catch (Exception e){
            Log.d("whatthe",e.toString() + "Error");
        }
    }

    void init() {
        horizontalScrollView=(HorizontalScrollView)findViewById(R.id.maphorizontalscrollview);
        scrollView=(ScrollView)findViewById(R.id.mapscrollview);
        horizontalScrollView.setOnTouchListener(this);
        scrollView.setOnTouchListener(this);
        horizontalScrollView.setFadingEdgeLength(0);
        scrollView.setFadingEdgeLength(0);

        addMapView();

        //mapView.setFocusableInTouchMode(true);
        //mapView.requestFocus();

        Button location = (Button) findViewById(R.id.location);
        location.setOnClickListener(this);

        Button navigation = (Button) findViewById(R.id.navigation);
        navigation.setOnClickListener(this);

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    int p=0;
    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.location : // Get the present location and draw it
                Intent intent = new Intent(CALL_MAP);
                intent.putExtra("groupIdx",new Integer(1));
                intent.putExtra("mapIdx",new Integer(6));
                sendBroadcast(intent);
                break;
            case R.id.navigation : // Calculate the shortest path and draw it
                p++;
                Log.d("whatthe",new Integer(p).toString());
                mapView.zoomPointer(p);
                break;
            case R.id.cancel : // Erase all of lines except exit gates.

                break;
        }
    }

    private void scroll(int dx,int dy)
    {
        horizontalScrollView.scrollBy(dx,0);
        scrollView.scrollBy(0,dy);
    }

    @Override
    public void onDestroy(){
//        mapView.onDestroy();
        super.onDestroy();
        Log.d("whatthe","혼돈파괴");
        beaconService.resetMap();
        unbindService(serviceConnection);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_POINTER_DOWN:
                xStartPos=(int)event.getRawX();
                yStartPos=(int)event.getRawY();
                break;
            case MotionEvent.ACTION_DOWN:
                xStartPos=(int)event.getRawX();
                yStartPos=(int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(xStartPos==0 && yStartPos==0){
                    xStartPos=(int)event.getRawX();
                    yStartPos=(int)event.getRawY();
                    break;
                }
                int xEndPos=(int)event.getRawX();
                int yEndPos=(int)event.getRawY();
                scroll(xStartPos-xEndPos,yStartPos-yEndPos);
                //Log.d("map", "스크롤 " + "x좌표는 " + xStartPos + "에서" + xEndPos + ", y좌표는" + yStartPos + "에서 " + yEndPos + "로");
                xStartPos=xEndPos;
                yStartPos=yEndPos;
                break;
            case MotionEvent.ACTION_UP:
                xStartPos=0;
                yStartPos=0;
                break;
            default:
                break;
        }
        //xStartPos=(int)event.getRawX();
        //yStartPos=(int)event.getRawY();
        return true;
    }
}
