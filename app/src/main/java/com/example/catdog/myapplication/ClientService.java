package com.example.catdog.myapplication;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.WindowManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SoHyunSeop on 15. 8. 19..
 */
public class ClientService extends Service {

    private static Client client;
    private static Handler handler;
    private static Context context;

    private static IntentFilter intentFilter;
    private static BroadcastReceiver broadcastReceiver;

    private static Thread broadcastThread;
    private static Queue<BroadData> broadcastQueue = new LinkedList<>();

    private int maxRetry = 3;
    private static int countConnectError = 0;
    private static int countSendError = 0;

    private static Timer timer;
    private static TimerTask timerTask;

    private final String TYPE_LOCATE = "swmaestro.ship.broadcast.LOCATE";
    private final String TYPE_EMERGENCY_TRUE = "swmaestro.ship.broadcast.EMERGENCYTRUE";
    private final String TYPE_EMERGENCY_FALSE = "swmaestro.ship.broadcast.EMERGENCYFALSE";
    private final String TYPE_CALL_MAP = "swmaestro.ship.broadcast.CALLMAP";

    private class BroadData {
        private String action;
        private Intent intent;

        public BroadData(String action, Intent intent) {
            this.action = action;
            this.intent = intent;
        }
    }

    @Override
    public void onCreate() {

        // 리시버 등록
        if (intentFilter == null || broadcastReceiver == null) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(TYPE_LOCATE);
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // 클라이언트 연결
                    if (client != null && !client.isConnected) {
                        client.connectAsync();
                    }

                    // 수신된 정보를 큐에 추가
                    synchronized (broadcastThread) {
                        broadcastQueue.add(new BroadData(intent.getAction(), intent));
                        broadcastThread.notify();
                    }
                }
            };
            registerReceiver(broadcastReceiver, intentFilter);
        }

        // 핸들러 등록
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        // 컨텍스트 등록
        if (context == null) {
            context = this;
        }

        // 스레드 생성
        // - 브로드캐스트 처리 스레드
        broadcastThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (broadcastThread != null) {
                    synchronized (broadcastThread) {
                        if (!broadcastQueue.isEmpty()) {
                            if (client != null && client.isConnected) {
                                BroadData broadData = broadcastQueue.poll();
                                switch (broadData.action) {
                                    case TYPE_LOCATE:
                                        client.request(Client.Type.Locate, new ClientTypeBeacon(broadData.intent.getStringExtra("beacon_id")));
                                        break;

                                    default:
                                        break;
                                }
                            }
                        } else {
                            try {
                                broadcastThread.wait();
                            } catch (InterruptedException e) { }
                        }
                    }
                }
            }
        });
        broadcastThread.start();

        // 타이머 생성
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (countConnectError >= maxRetry) {
                        if (client != null && !client.isConnected) {
                            client.connectAsync();
                        }
                    }
                }
            };
        }
        if (timer == null) {
            timer = new Timer();
            timer.schedule(timerTask, 0, 5000);
        }

        // 클라이언트 처리
        if (client == null) {
            // 클라이언트 생성
            client = new Client("172.16.101.41", 12000);

            // 클라이언트 접속 성공 이벤트
            client.setOnConnectedListener(new Client.onConnected() {
                @Override
                public void onConnected(String ip, int port) {
                    countConnectError = 0;
                    client.request(Client.Type.Register, new ClientTypeDevice());
                    ClientUtility.showToast(context, handler, "서버 접속을 성공했습니다.");
                }
            });

            // 클라이언트 접속 실패 이벤트
            client.setOnConnectErrorListener(new Client.onConnectError() {
                @Override
                public void onConnectError(String ip, int port, final String result) {
                    // 접속을 실패한 경우 재시도
                    synchronized (this) {
                        if (countConnectError < maxRetry) {
                            countConnectError += 1;
                            ClientUtility.showToast(context, handler, "접속 재시도 중... (" + countConnectError + "/" + maxRetry + ")");
                            try { wait(5000); } catch (InterruptedException e) { }
                            client.connectAsync();
                        } else {
                            broadcastThread = null;
                            ClientUtility.showToast(context, handler, "접속을 실패했습니다.\n서버가 응답하지 않습니다.");
                        }
                    }
                }
            });

            // 클라이언트 데이터 전송 이벤트
            client.setOnDataSentListener(new Client.onDataSent() {
                @Override
                public void onDataSent(final String result, boolean isError) {
                    // 발송을 실패한 경우 재시도
                    if (isError) {
                        if (countSendError < maxRetry) {
                            countSendError += 1;
                            try { wait(5000); } catch (InterruptedException e) { }
                            client.clientSend(result);
                        } else {
                            broadcastReceiver = null;
                            ClientUtility.showToast(context, handler, "명령을 실패했습니다.\n서버가 응답하지 않습니다.");
                        }
                    } else {
                        countSendError = 0;
                    }
                }
            });

            // 클라이언트 데이터 수신 이벤트
            client.setOnDataReceivedListener(new Client.onDataReceived() {
                @Override
                public void onDataReceived(final String result, boolean isError) {
                    //ClientUtility.showToast(context, handler, result);
                    if (!isError) {
                        try {
                            JSONParser jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);

                            String type = (String) jsonObject.get("type");
                            JSONObject content = (JSONObject) jsonObject.get("content");

                            switch (type) {
                                case "notify":
                                    final String title;
                                    if (content.get("type").toString().equals("warn")) {
                                        title = "경보";
                                    } else {
                                        title = "공지";
                                    }

                                    final String text = content.get("text").toString();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder
                                                    .setCancelable(false)
                                                    .setTitle(title)
                                                    .setMessage(text)
                                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert = builder.create();
                                            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                            alert.show();

                                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            Notification.Builder mBuilder = new Notification.Builder(context);
                                            mBuilder.setTicker(text);
                                            mBuilder.setWhen(System.currentTimeMillis());
                                            mBuilder.setContentTitle(title);
                                            mBuilder.setContentText(text);
                                            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                                            mBuilder.setAutoCancel(true);
                                            nm.notify(0, mBuilder.build());
                                        }
                                    });
                                    break;

                                case "emergency":
                                    Intent location = new Intent(TYPE_EMERGENCY_TRUE);
                                    sendBroadcast(location);

                                    /*
                                    Intent location = new Intent(TYPE_EMERGENCY_FALSE);
                                    sendBroadcast(location);
                                    */
                                default:
                                    break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
