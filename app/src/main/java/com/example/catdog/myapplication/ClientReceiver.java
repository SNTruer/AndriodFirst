package com.example.catdog.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
public class ClientReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        switch (actionName) {
            case "com.example.catdog.myapplication.LOCATE":
                String id = intent.getStringExtra("ID");
                Toast.makeText(context, "위치 갱신을 요청했습니다.\n" + id, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;

        }
    }
}
