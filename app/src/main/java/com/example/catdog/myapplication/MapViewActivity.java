package com.example.catdog.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.catdog.myapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MapViewActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    MapCustomView mapView;
    HorizontalScrollView horizontalScrollView;
    ScrollView scrollView;
    LinearLayout linearLayout;
    String imageUrl;
    String mapDetailString;
    BeaconDataReceiver beaconDataReceiver;
    int xStartPos,yStartPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("imageUrl");
        //imageUrl="http://www.webengine.co.kr/Escape/map_image/0263/0366/0156/0042/7b0c1632a6e0eea74c79897516a0d2a1.gif";
        mapDetailString = intent.getStringExtra("mapDetailString");
        setContentView(R.layout.activity_show_me_the_map);

        init();
    }

    void init() {
        mapView = new MapCustomView(this);

        horizontalScrollView=(HorizontalScrollView)findViewById(R.id.maphorizontalscrollview);
        scrollView=(ScrollView)findViewById(R.id.mapscrollview);
        horizontalScrollView.setOnTouchListener(this);
        scrollView.setOnTouchListener(this);
        horizontalScrollView.setFadingEdgeLength(0);
        scrollView.setFadingEdgeLength(0);

        beaconDataReceiver=new BeaconDataReceiver(this);

        try {
            mapView.init(imageUrl, DomChanger.stringToDom(mapDetailString),beaconDataReceiver);
            Log.d("map", "맵 초기화");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("map", "에러라");
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout = (LinearLayout) findViewById(R.id.testlinear);
                Log.d("map", "나는 생성");
                linearLayout.addView(mapView);
            }
        });

        //mapView.setFocusableInTouchMode(true);
        //mapView.requestFocus();

        Button location = (Button) findViewById(R.id.location);
        location.setOnClickListener(this);

        Button navigation = (Button) findViewById(R.id.navigation);
        navigation.setOnClickListener(this);

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.location : // Get the present location and draw it
                Log.d("map", new Integer(mapView.getMeasuredWidth()).toString());
                break;
            case R.id.navigation : // Calculate the shortest path and draw it
                Log.d("map",new Integer(mapView.getMeasuredHeight()).toString());
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
                Log.d("map", "스크롤 " + "x좌표는 " + xStartPos + "에서" + xEndPos + ", y좌표는" + yStartPos + "에서 " + yEndPos + "로");
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
