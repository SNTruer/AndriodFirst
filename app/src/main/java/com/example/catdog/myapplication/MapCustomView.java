package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
public class MapCustomView extends TouchImageView implements Runnable, View.OnTouchListener{

    private float scaleFactor = 1.0f;
    public int height;
    public int width;
    private String mapUrl;
    private NodePoint[] realNodes = new NodePoint[101]; // index = "idx"
    private HashMap<String, NodeBeacon> realBeacons = new HashMap<String, NodeBeacon>();
    private double[][] distance = new double[101][101];
    public ScaleGestureDetector scaleDetector;
    private Context context;
    private Dijkstra dijkstra;
    private int[] routeCheck = new int[101];
    private String nowBeaconKey;
    private NodePoint startPoint;
    public Thread beaconThread;

    public class NodeBeacon {

        int index;
        double x, y;
    }


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

    public void changeSize()
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                //ViewGroup.LayoutParams params = MapCustomView.this.getLayoutParams();
                //params.width = (int) (width * (scaleFactor));
                //params.height = (int) (height * (scaleFactor));
                MapCustomView.super.measure(MeasureSpec.makeMeasureSpec((int)(width*scaleFactor),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int)(height*scaleFactor),MeasureSpec.EXACTLY));
                //setLayoutParams(params);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void init(String url,Document doc)
    {
        //setOnTouchListener(this);
        /*scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
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
        });*/

        //scaleDetector.setQuickScaleEnabled(true);
        beaconCircle=BitmapFactory.decodeResource(getResources(), R.drawable.beaconcircle);
        beaconCircle = Bitmap.createScaledBitmap(beaconCircle,30,30,true);

        setMapURL(url);
        Thread thread = new Thread(this);
        thread.start();
        getXml(doc);
    }

    Bitmap m_bitmap;
    Bitmap beaconCircle;
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
            Log.d("whatthe", "크기 재는게 끝남");
            //measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
            //changeSize();
            post(new Runnable() {
                @Override
                public void run() {
                    //MapCustomView.super.setImageBitmap(Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565));
                    MapCustomView.super.setImageBitmap(m_bitmap);
                }
            });

            refreshImage();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("map",e.toString());
        }
    }

    public void refreshImage()
    {
        this.postInvalidate(); // onDraw 를 부르는 메소드 (invalidate 메소드는 UI Thread 에서 부를 수 있는 메소드)
        // postInvalidate() 는 언제나 가능
    }

    public void onDestroy(){
    }

    public boolean checkBeacon(String key){
        if(nowBeaconKey!=null && nowBeaconKey.equals(key)) return false;
        nowBeaconKey=key;
        NodeBeacon beacon = realBeacons.get(key);
        if(beacon==null) return false;
        double min = Double.MAX_VALUE;
        int start=1;
        startPoint=realNodes[beacon.index];
        start=beacon.index;
        dijkstra = new Dijkstra(NodePoint.maxIndex,start,distance,realNodes);
        routeCheck = dijkstra.getRoute();
        //beaconChangeCallback.callBack(beacon.x,beacon.y);
        Log.d("whatthe","비콘체크 실행!!" + key);
        refreshImage();
        return true;
    }

    private double getDistance(double x,double y,double x1,double y1){
        return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.scale(super.normalizedScale, super.normalizedScale);
        //canvas.scale(scaleFactor,scaleFactor);
        //canvas.translate(-10,-10);
        super.onDraw(canvas);
        Log.d("map", "맵 그리기 시작합니다.");
        if (m_bitmap == null || realNodes == null) {
            if(m_bitmap==null) Log.d("map","비트맵이 널");
            else Log.d("map","realNodes가 널");
            return;
        }

        canvas.setMatrix(super.matrix);
        canvas.translate(0,height/15);

        //super.setZoom(normalizedScale,100,100);

        canvas.drawBitmap(m_bitmap, 0, 0, null);
        Log.d("map", "맵 그리기 시작합니다.");

        Paint paint = new Paint();
        if(nowBeaconKey !=null){
            paint.setColor(Color.GREEN);
            NodeBeacon beacon = realBeacons.get(nowBeaconKey);
            if(beacon!=null) {
                canvas.drawCircle((float) beacon.x+width/100, (float) beacon.y+width/100, width/50, paint);
                //canvas.drawBitmap(beaconCircle,(float)beacon.x,(float)beacon.y,null);
            }
        }

        for(int i=1;i<=NodePoint.maxIndex;i++){
            for (int j = i + 1; j <= NodePoint.maxIndex; j++) {

                if (distance[i][j] > 0) {
                    paint.setStrokeWidth(width/200);
                    if(routeCheck[i]>0 && routeCheck[j]>0 && Math.abs(routeCheck[i]-routeCheck[j])==1)
                    {
                    }
                    else {
                        paint.setColor(Color.RED);
                        canvas.drawLine((float) realNodes[i].x+width/100, (float) realNodes[i].y+width/100, (float) realNodes[j].x+width/100, (float) realNodes[j].y+width/100, paint);
                    }
                }
            }
        }

        for (int i = 1; i <= NodePoint.maxIndex; i++) {

            if ((realNodes[i].isExit || routeCheck[i]>0)) paint.setColor(Color.BLUE);
            else paint.setColor(Color.RED);
            canvas.drawCircle((float) realNodes[i].x+width/100, (float) realNodes[i].y+width/100, width/100, paint);
        }

        for(int i=1;i<=NodePoint.maxIndex;i++){
            for (int j = i + 1; j <= NodePoint.maxIndex; j++) {

                if (distance[i][j] > 0) {
                    paint.setStrokeWidth(width/200);
                    if(routeCheck[i]>0 && routeCheck[j]>0 && Math.abs(routeCheck[i]-routeCheck[j])==1)
                    {
                        paint.setColor(Color.BLUE);
                        canvas.drawLine((float) realNodes[i].x+width/100, (float) realNodes[i].y+width/100, (float) realNodes[j].x+width/100, (float) realNodes[j].y+width/100, paint);
                        if(routeCheck[i]>routeCheck[j]) fillArrow(canvas, (float) realNodes[i].x, (float) realNodes[i].y, (float) realNodes[j].x, (float) realNodes[j].y, Color.GREEN);
                        else fillArrow(canvas, (float) realNodes[j].x+width/100, (float) realNodes[j].y+width/100, (float) realNodes[i].x+width/100, (float) realNodes[i].y+width/100, Color.GREEN);
                    }
                }
            }
        }

    }

    public void zoomPointer(final int i){
        post(new Runnable() {
            @Override
            public void run() {
                MapCustomView.super.setScrollPosition((float)(realNodes[i].x/width),(float)(realNodes[i].y/height));
            }
        });
    }

    public void zoomPointer(final String key){
        post(new Runnable() {
            @Override
            public void run() {
                Log.d("whatthe","줌포인터 실행!");
                if(realBeacons.get(key)==null) return;
                int i = realBeacons.get(key).index;
                MapCustomView.super.setScrollPosition((float)(realNodes[i].x/width),(float)(realNodes[i].y/height));
            }
        });
    }

    private void fillArrow(Canvas canvas, float x0, float y0, float x1, float y1,int color) {

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        float frac = (float) 0.1;

        float point_x_1 = x0 + (float) ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = y0 + (float) ((1 - frac) * deltaY - frac * deltaX);

        float point_x_2 = x1;
        float point_y_2 = y1;

        float point_x_3 = x0 + (float) ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = y0 + (float) ((1 - frac) * deltaY + frac * deltaX);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(point_x_2, point_y_2);
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.lineTo(point_x_1, point_y_1);
        path.close();

        canvas.drawPath(path, paint);
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
                    NodeBeacon imsi = new NodeBeacon();

                    int index = Integer.parseInt(node.getAttribute("idx"));
                    realNodes[index].x = Double.parseDouble(node.getAttribute("x"));
                    realNodes[index].y = Double.parseDouble(node.getAttribute("y"));
                    int exit=Integer.parseInt(node.getAttribute("exit"));
                    realNodes[index].isExit = exit!=0;

                    imsi.index=index;
                    imsi.x = realNodes[index].x;
                    imsi.y = realNodes[index].y;
                    String uuid = node.getAttribute("uuid");
                    String majorid = node.getAttribute("majorid");
                    String minorid = node.getAttribute("minorid");
                    if(uuid!=null && uuid!="")
                    {
                        realBeacons.put(uuid + "-" + majorid + "-" + minorid, imsi);
                    }

                    NodeList links = node.getElementsByTagName("link");

                    start = index;

                    for(int j=0; j<links.getLength(); j++){

                        Element link = (Element)links.item(j);

                        end = Integer.parseInt(link.getAttribute("idx"));

                        distance[start][end] = Double.parseDouble(link.getAttribute("distance"));
                        distance[end][start] = Double.parseDouble(link.getAttribute("distance"));
                    }
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
        //scaleDetector.onTouchEvent(event);

        return true;
    }
}
