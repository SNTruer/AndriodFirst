package com.example.catdog.myapplication;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.json.simple.JSONObject;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
class ClientUtility {
    public static String getDeviceID() {
        return "TEMP-DEVICEID-STRING-NUMBER";
    }

    public static String toJSONString(Client.Type type, Object content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString().toLowerCase());
        jsonObject.put("content", content);

        return jsonObject.toJSONString();
    }

    public static void showToast(final Context context, final Handler handler, final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
