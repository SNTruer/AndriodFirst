package com.example.catdog.myapplication;

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
}
