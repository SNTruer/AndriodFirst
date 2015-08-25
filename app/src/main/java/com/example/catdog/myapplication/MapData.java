package com.example.catdog.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class MapData extends GroupMapSuperData implements Serializable {
    String imageUrl;
    String mapDetailString;

    public MapData(String name,String imageUrl,String mapDetailString)
    {
        super(name);
        this.imageUrl=imageUrl;
        //this.mapDetailDocument=mapDetailDocument;
        this.mapDetailString=mapDetailString;
    }

    public static ArrayList<MapData> getMapListFromDom(Document document) throws Exception
    {
        NodeList nodeList = document.getElementsByTagName("map_data");
        int count = nodeList.getLength();
        ArrayList<MapData> list = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Element ele = (Element) nodeList.item(i);

            if (ele.hasChildNodes()) {
                String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
                String imageUrl = ele.getElementsByTagName("image").item(0).getTextContent().trim();
                String mapString = ele.getElementsByTagName("map").item(0).getTextContent().trim();
                //Document mapDocument = DomChanger.stringToDom(ele.getElementsByTagName("map").item(0).getTextContent().trim());
                MapData mapData = new MapData(name,imageUrl,mapString);
                Integer mapIdx = Integer.parseInt(ele.getElementsByTagName("map_idx").item(0).getTextContent().trim());
                list.add(mapData);
            }
        }

        return list;
    };

    public static HashMap<Integer, MapData> getMapHashMapFromDom(Document document) throws Exception{
        NodeList nodeList = document.getElementsByTagName("map_data");
        int count = nodeList.getLength();
        HashMap<Integer, MapData> hashMap = new HashMap<>();

        for (int i = 0; i < count; i++) {
            Element ele = (Element) nodeList.item(i);

            if (ele.hasChildNodes()) {
                String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
                String imageUrl = ele.getElementsByTagName("image").item(0).getTextContent().trim();
                String mapString = ele.getElementsByTagName("map").item(0).getTextContent().trim();
                //Document mapDocument = DomChanger.stringToDom(ele.getElementsByTagName("map").item(0).getTextContent().trim());
                MapData mapData = new MapData(name,imageUrl,mapString);
                Integer mapIdx = Integer.parseInt(ele.getElementsByTagName("map_idx").item(0).getTextContent().trim());
                hashMap.put(mapIdx, mapData);
            }
        }

        return hashMap;
    }

    public static MapData getMapDataFromDom(Document document) throws Exception{
        NodeList nodeList = document.getElementsByTagName("map");
        int count = nodeList.getLength();
        MapData mapData = null;

        for (int i = 0; i < count; i++) {
            Element ele = (Element) nodeList.item(i);

            if (ele.hasChildNodes()) {
                String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
                String imageUrl = ele.getElementsByTagName("image").item(0).getTextContent().trim();
                String mapString = ele.getElementsByTagName("map").item(0).getTextContent().trim();
                //Document mapDocument = DomChanger.stringToDom(ele.getElementsByTagName("map").item(0).getTextContent().trim());
                mapData = new MapData(name,imageUrl,mapString);
                break;
            }
        }

        return mapData;
    }
}
