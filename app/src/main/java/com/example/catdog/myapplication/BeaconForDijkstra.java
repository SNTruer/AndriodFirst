package com.example.catdog.myapplication;

/**
 * Created by MyeongJun on 2015. 8. 16..
 */
public class BeaconForDijkstra implements Comparable<BeaconForDijkstra> {
    int point;
    double distance;

    public BeaconForDijkstra(int point,double distance){
        this.point=point;
        this.distance=distance;
    }

    @Override
    public int compareTo(BeaconForDijkstra another) {
        return (this.distance>another.distance) ? 1 : (this.distance==another.distance) ? 0 : -1;
    }
}
