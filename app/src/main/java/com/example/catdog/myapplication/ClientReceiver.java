package com.example.catdog.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
public class ClientReceiver extends BroadcastReceiver {

    // TODO 정적 브로드캐스트 리시버 삭제 후 서비스쪽에서 동적으로 브로드캐스트 수신하도록 수정
    // TODO 위치를 바로 발송하지 말고 멤버 변수에 보관 후 발송 및 실패시 큐의 항목을 발송 재시도
    // TODO 서비스 시작시 바로 소켓 접속하지 말고 브로드캐스트 받은 경우에만 null 검사 후 소켓 연결

    private static Thread checkThread;
    private static Queue<ClientTypeLocation> workQueue = new LinkedList<>();

    private static ClientService clientService;
    private static boolean clientBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스 가져오기
            ClientService.ClientBinder binder = (ClientService.ClientBinder) service;
            clientService = binder.getService();

            // 바인딩 정보 갱신
            clientBound = true;

            // 검사 스레드 시작
            if (checkThread == null) {
                checkThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (clientBound) {
                            if (!workQueue.isEmpty()) {
                                // 1. 클리이언트 접속 여부 검사 필요
                                // 2. 스레드 무한 루프 방지 필요
                                // 3. 큐 검사 작업 필요
                                clientService.client.request(Client.Type.Locate, workQueue.poll());
                            }
                        }
                    }
                });

                checkThread.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 바인딩 정보 갱신
            clientBound = false;
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        // 컨텍스트
        Context applicationContext = context.getApplicationContext();

        // 서비스 검사
        boolean serviceRunning = false;
        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.catdog.myapplication.ClientService".equals(service.service.getClassName())) {
                serviceRunning = true;
            }
        }

        if (serviceRunning) {
            // 서비스 바인딩
            if (clientService == null) {
                applicationContext.bindService(new Intent(applicationContext, ClientService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            }

            // 브로드캐스트 수신 처리
            String actionName = intent.getAction();
            switch (actionName) {
                case "com.example.catdog.myapplication.LOCATE":
                    workQueue.add(new ClientTypeLocation(127, 286));
                    break;

                default:
                    break;

            }
        } else {
            Toast.makeText(applicationContext, "서비스가 실행중이지 않습니다.\n요청을 받을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
