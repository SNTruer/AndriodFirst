package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by imcheck on 2015. 7. 30..
 */
public class MapCustomView extends View implements Runnable, View.OnTouchListener{

    private float scaleFactor = 3.0f;
    public int height;
    public int width;
    private String mapUrl;
    private NodePoint[] realNodes = new NodePoint[101]; // index = "idx"
    private HashMap<String, NodeBeacon> realBeacons = new HashMap<String, NodeBeacon>();
    private double[][] distance = new double[101][101];
    public ScaleGestureDetector scaleDetector;
    private Context context;
    private Dijkstra dijkstra;
    private boolean[] routeCheck = new boolean[101];
    private BeaconDataReceiver beaconDataReceiver;
    private String nowBeaconKey;
    private NodePoint startPoint;

    public MapCustomView(Context context) {
        super(context);
        this.context=context;
    }
    public MapCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }
    public MapCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    //@Override
    /*public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        Log.d("map", "나는 onmeasure");
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =0;
        switch (heightMode){
            case MeasureSpec.UNSPECIFIED: heightSize = heightMeasureSpec;break;
            case MeasureSpec.AT_MOST: heightSize = MeasureSpec.getSize(heightMeasureSpec);break;
            case MeasureSpec.EXACTLY: heightSize = MeasureSpec.getSize(heightMeasureSpec);break;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =0;
        switch (widthMode){
            case MeasureSpec.UNSPECIFIED: widthSize = widthMeasureSpec;break;
            case MeasureSpec.AT_MOST: widthSize = MeasureSpec.getSize(widthMeasureSpec);break;
            case MeasureSpec.EXACTLY: widthSize = MeasureSpec.getSize(widthMeasureSpec);break;
        }

        widthSize=(int)(width*(scaleFactor+0.5f));
        heightSize=(int)(height*(scaleFactor+0.5f));
        setMeasuredDimension(widthSize, heightSize);
    }*/

    public void changeSize()
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = MapCustomView.this.getLayoutParams();
                params.width = (int) (width * (scaleFactor));
                params.height = (int) (height * (scaleFactor));
                setLayoutParams(params);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void init(String url,Document doc,BeaconDataReceiver receiver)
    {
        beaconDataReceiver=receiver;
        setOnTouchListener(this);
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor*=detector.getScaleFactor();
                //Log.d("map", "스케일 변화! : " + scaleFactor);
                MapCustomView.this.refreshImage();
                changeSize();
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                changeSize();
                Log.d("map","변화 종료");
            }
        });
        scaleDetector.setQuickScaleEnabled(true);

        setMapURL(url);
        Thread thread = new Thread(this);
        thread.start();
        getXml(doc);
        startBeacon();
    }

    Bitmap m_bitmap;
    public void run()
    {
        try {
            URL url = new URL(getMapURL());

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            InputStream is = con.getInputStream();
            int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK)
                return;

            int len = con.getContentLength();

            byte data[] = new byte[len];
            int nTotal = 0;
            while(true) {

                int nRead = is.read(data, nTotal, len-nTotal);
                if(nRead == -1)
                    break;
                nTotal += nRead;
            }

            is.close();
            con.disconnect();

            m_bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            height = m_bitmap.getHeight();
            width = m_bitmap.getWidth();
            Log.d("map","크기 재는게 끝남");
            //measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
            changeSize();

            refreshImage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshImage()
    {
        this.postInvalidate(); // onDraw 를 부르는 메소드 (invalidate 메소드는 UI Thread 에서 부를 수 있는 메소드)
        // postInvalidate() 는 언제나 가능
    }

    private void startBeacon(){
        nowBeaconKey=new String("");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(beaconDataReceiver.beaconList==null) continue;
                    if(beaconDataReceiver.beaconList.size()==0) continue;
                    BeaconData data=beaconDataReceiver.beaconList.get(0);
                    String key=data.Uuid+"-"+data.MajorId+"-"+data.MinorId;
                    if(!nowBeaconKey.equals(key)){
                        if(checkBeacon(key)) nowBeaconKey=key;
                    }
                }
            }
        });
        thread.start();
    }

    private boolean checkBeacon(String key){
        NodeBeacon beacon = realBeacons.get(key);
        if(beacon==null) return false;
        double min = Double.MAX_VALUE;
        int start=1;
        for(int i=1;i<=NodePoint.maxIndex;i++){
            if(min>getDistance(beacon.x,beacon.y,realNodes[i].x,realNodes[i].y)){
                min=getDistance(beacon.x,beacon.y,realNodes[i].x,realNodes[i].y);
                startPoint=realNodes[i];
                start=i;
            }
        }
        dijkstra = new Dijkstra(NodePoint.maxIndex,start,distance,realNodes);
        routeCheck = dijkstra.getRoute();
        refreshImage();
        return true;
    }

    private double getDistance(double x,double y,double x1,double y1){
        return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (m_bitmap == null || realNodes == null)
            return;

        canvas.scale(scaleFactor, scaleFactor);
        canvas.drawBitmap(m_bitmap, 0, 0, null);

        Paint paint = new Paint();
        if(nowBeaconKey !=null){
            paint.setColor(Color.GREEN);
            NodeBeacon beacon = realBeacons.get(nowBeaconKey);
            if(beacon!=null) {
                canvas.drawCircle((float) beacon.x, (float) beacon.y, 10, paint);
                paint.setStrokeWidth(3);
                canvas.drawLine((float) beacon.x, (float) beacon.y, (float) startPoint.x, (float) startPoint.y, paint);
            }
        }

        for (int i = 1; i <= NodePoint.maxIndex; i++) {

            if ((realNodes[i].exit == 1 || routeCheck[i])) paint.setColor(Color.BLUE);
            else paint.setColor(Color.RED);
            canvas.drawCircle((float) realNodes[i].x, (float) realNodes[i].y, 10, paint);
            for (int j = i + 1; j <= NodePoint.maxIndex; j++) {

                if (distance[i][j] > 0) {
                    if(routeCheck[i] && routeCheck[j]) paint.setColor(Color.BLUE);
                    else paint.setColor(Color.RED);
                    paint.setStrokeWidth(3);
                    canvas.drawLine((float) realNodes[i].x, (float) realNodes[i].y, (float) realNodes[j].x, (float) realNodes[j].y, paint);
                }
            }
        }
    }

    public void setMapURL(String url){
        mapUrl=url;
    }

    String getMapURL(){
        return mapUrl;
    }

    public void getXml(Document doc){

        int start, end;

        for(int i=1; i<=100; i++) realNodes[i] = new NodePoint();

        //InputStream is = getResources().openRawResource(R.raw.pointers_coordinate); // 나중에 여기를 URL 로 고쳐야 한다.
        try {
            try {

                Element root = doc.getDocumentElement();

                NodeList pointers = root.getElementsByTagName("point");

                NodePoint.maxIndex = pointers.getLength();
                Log.d("Fuck","Get Xml -> Success"+Integer.toString(NodePoint.maxIndex));

                for(int i=0; i< NodePoint.maxIndex; i++){

                    Element node = (Element)pointers.item(i);

                    int index = Integer.parseInt(node.getAttribute("idx"));
                    realNodes[index].x = Double.parseDouble(node.getAttribute("x"));
                    realNodes[index].y = Double.parseDouble(node.getAttribute("y"));
                    realNodes[index].exit = Integer.parseInt(node.getAttribute("exit"));

                    NodeList links = node.getElementsByTagName("link");

                    start = index;

                    for(int j=0; j<links.getLength(); j++){

                        Element link = (Element)links.item(j);

                        end = Integer.parseInt(link.getAttribute("idx"));

                        distance[start][end] = Double.parseDouble(link.getAttribute("distance"));
                        distance[end][start] = Double.parseDouble(link.getAttribute("distance"));
                    }
                }

                NodeList beacons = root.getElementsByTagName("beacon");

                for(int i=0; i<beacons.getLength(); i++) {

                    Element beacon = (Element)beacons.item(i);
                    NodeBeacon imsi = new NodeBeacon();

                    imsi.x = Double.parseDouble(beacon.getAttribute("x"));
                    imsi.y = Double.parseDouble(beacon.getAttribute("y"));
                    realBeacons.put(beacon.getAttribute("uuid")+"-"+beacon.getAttribute("majorid")+"-"+beacon.getAttribute("minorid"),imsi);
                }
            } catch (Exception e) {
                Log.d("Fuck", e.toString());
            }

        } catch (Exception e) {
            Log.d("Fuck", e.toString());
        }
        refreshImage();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        return true;
    }
}
