package com.example.catdog.myapplication;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by MyeongJun on 2015. 7. 30..
 */
public class RequestServer {
    private static RequestServer singleton;
    private ArrayList<GroupData> groupList;
    private ArrayList<MapData> mapList;
    static {
        singleton = new RequestServer();
    }

    public static RequestServer getInstance(){
        return singleton;
    }

    public ArrayList getGroupList() throws Exception{
        groupList=null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    String urlstr = "http://www.webengine.co.kr/Escape/?page=api&type=get_group_list";
                    URL url = new URL(urlstr);
                    DocumentBuilder builder;
                    DocumentBuilderFactory factory;
                    Document document;
                    ArrayList<GroupData> list = new ArrayList<>();

                    factory = DocumentBuilderFactory.newInstance();
                    builder = factory.newDocumentBuilder();
                    document = builder.parse(new InputSource(url.openStream()));
                    document.getDocumentElement().normalize();

                    NodeList nodeList = document.getElementsByTagName("group");
                    int count = nodeList.getLength();

                    for (int i = 0; i < count; i++) {
                        Element ele = (Element) nodeList.item(i);

                        if (ele.hasChildNodes()) {
                            String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
                            Integer groupIdx = Integer.parseInt(ele.getElementsByTagName("group_idx").item(0).getTextContent().trim());
                            list.add(new GroupData(name,groupIdx));
                        }
                    }
                    groupList=list;
                    if(list==null) groupList.add(new GroupData("null",0));
                }catch (Exception e)
                {
                    Log.e("error", "error");
                }
            }
        });
        thread.start();
        while(groupList==null){

        }
        return groupList;
    }

    public ArrayList getMapList(final int groupIdx) throws Exception {
        mapList=null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    String urlstr = "http://www.webengine.co.kr/Escape/?page=api&type=get_map_list";
                    String parameter = URLEncoder.encode("group_idx","UTF-8") + "=" + ((Integer)groupIdx).toString();
                    Log.e("error",parameter);
                    URL url = new URL(urlstr);
                    URLConnection con = url.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(parameter);
                    writer.flush();

                    DocumentBuilder builder;
                    DocumentBuilderFactory factory;
                    Document document;
                    ArrayList<MapData> list = new ArrayList<>();

                    factory = DocumentBuilderFactory.newInstance();
                    builder = factory.newDocumentBuilder();
                    document = builder.parse(new InputSource(con.getInputStream()));
                    document.getDocumentElement().normalize();

                    NodeList nodeList = document.getElementsByTagName("map_data");
                    int count = nodeList.getLength();

                    for (int i = 0; i < count; i++) {
                        Element ele = (Element) nodeList.item(i);

                        if (ele.hasChildNodes()) {
                            String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
                            String imageUrl = ele.getElementsByTagName("image").item(0).getTextContent().trim();
                            MapData mapData = new MapData(name,imageUrl);
                            list.add(mapData);
                        }
                    }
                    mapList=list;
                    if(list==null) mapList.add(new MapData("null",""));
                }catch (Exception e)
                {
                    Log.e("error", e.toString());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        while(mapList==null){

        }
        return mapList;
    }
}
