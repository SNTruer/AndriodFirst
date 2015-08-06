package com.example.catdog.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class MapData extends GroupMapSuperData {
    String imageUrl;
    public MapData(String name,String imageUrl)
    {
        super(name);
        this.imageUrl=imageUrl;
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
                MapData mapData = new MapData(name,imageUrl);
                list.add(mapData);
            }
        }

        return list;
    };
}
