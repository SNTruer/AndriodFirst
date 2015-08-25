package com.example.catdog.myapplication;

import org.w3c.dom.Node;

import java.util.PriorityQueue;

/**
 * Created by MyeongJun on 2015. 8. 16..
 */
public class Dijkstra {
    int N;
    boolean[] check;
    int[] routeCheck;
    double[] bestDistance;
    double[][] distance;
    NodePoint[] nodePoints;
    int[] parent;
    int startPoint;
    PriorityQueue<BeaconForDijkstra> queue;

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

    public Dijkstra(int N,int startPoint,double[][] distance,NodePoint[] nodePoints){
        this.N=N;
        this.startPoint=startPoint;
        this.distance=distance;
        this.nodePoints=nodePoints;
        check=new boolean[N+1];
        routeCheck=new int[N+1];
        bestDistance=new double[N+1];
        parent=new int[N+1];
    }

    public int[] getRoute(){
        queue = new PriorityQueue<>();
        queue.add(new BeaconForDijkstra(startPoint,0));
        BeaconForDijkstra data;
        for(int i=1;i<=N;i++){
            bestDistance[i]=Double.MAX_VALUE;
        }
        bestDistance[startPoint]=0;
        for(int i=0;i<N-1;i++) {
            data=queue.peek();
            while(queue.size()>0) {
                data=queue.poll();
                if (!check[data.point]) break;
            }
            int x = data.point;
            check[data.point] = true;
            for(int j=1; j<=N; j++)
            {
                if (distance[x][j]>0 && !check[j] && bestDistance[x]+distance[x][j]<bestDistance[j]) {
                    bestDistance[j] = bestDistance[x] + distance[x][j];
                    parent[j] = x;
                    queue.add(new BeaconForDijkstra(j, bestDistance[x]+distance[x][j]));
                }
            }
        }

        double min = Double.MAX_VALUE;
        int x = 1;
        for (int i=1;i<=N;i++) {
            if(nodePoints[i].exit==1 && min>bestDistance[i]){
                min=bestDistance[i];
                x=i;
            }
        }

        int cnt=0;
        while(x!=startPoint){
            routeCheck[x]=++cnt;
            x=parent[x];
        }
        routeCheck[startPoint]=++cnt;

        return routeCheck;
    }
}
