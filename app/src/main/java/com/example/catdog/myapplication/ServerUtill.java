package com.example.catdog.myapplication;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by MyeongJun on 2015. 7. 30..
 */
public class ServerUtill {
    public static void normalRequest(final OnComplete onObj) throws Exception
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlstr = "http://www.webengine.co.kr/Escape/?page=api&type=get_group_list";
                    URL url = new URL(urlstr);
                    URLConnection con = url.openConnection();
                    InputStream is = con.getInputStream();
                    byte[] getByte = new byte[10240];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while (true) {
                        if (is.read(getByte) > 0) {
                            baos.write(getByte, 0, getByte.length);
                        } else break;
                    }
                    onObj.onComplete(baos);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void postRequest(final String parameter,final OnComplete onObj) throws Exception
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String urlstr = "http://www.webengine.co.kr/Escape/?page=api&type=get_map_list";
                    Log.e("error",parameter);
                    URL url = new URL(urlstr);
                    URLConnection con = url.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(parameter);
                    writer.flush();

                    InputStream is = con.getInputStream();
                    byte[] getByte = new byte[10240];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while (true) {
                        if (is.read(getByte) > 0) {
                            baos.write(getByte, 0, getByte.length);
                        } else break;
                    }
                    onObj.onComplete(baos);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public interface OnComplete
    {
        public void onComplete(ByteArrayOutputStream baos);
    }
}
