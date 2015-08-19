package com.example.catdog.myapplication;

import org.json.simple.JSONObject;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
class ClientTypeLocation extends ClientJSON {
    private double x = 0;
    private double y = 0;

    public ClientTypeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", this.x);
        jsonObject.put("y", this.y);

        return jsonObject;
    }
}
