package com.example.catdog.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by MyeongJun on 2015. 7. 31..
 */
public class GroupData extends GroupMapSuperData {
    Integer groupIdx;

    public GroupData(String name,Integer groupIdx)
    {
        super(name);
        this.groupIdx=groupIdx;
    }

    public static ArrayList<GroupData> getGroupListFromDom(Document document) throws Exception
    {
        NodeList nodeList = document.getElementsByTagName("group");
        int count = nodeList.getLength();
        ArrayList<GroupData> list = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Element ele = (Element) nodeList.item(i);

            if (ele.hasChildNodes()) {
                String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
                Integer groupIdx = Integer.parseInt(ele.getElementsByTagName("group_idx").item(0).getTextContent().trim());
                list.add(new GroupData(name,groupIdx));
            }
        }

        return list;
    };
}
