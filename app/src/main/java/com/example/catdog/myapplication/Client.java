package com.example.catdog.myapplication;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by SoHyunSeop on 15. 8. 17..
 */
public class Client {

    //region [ 객체 ]
    enum Type {
        Chat,
        Locate,
        Register
    }

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Thread sendThread;
    private Thread receiveThread;

    private ThreadGroup sendGroup;
    private ThreadGroup receiveGroup;

    private Queue<String> queueSend = new LinkedList<>();

    private String ip;
    private int port;

    public boolean isConnected = false;
    //endregion

    //region [ 생성자 ]
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    //endregion

    //region [ 내부 함수 ]
    public void clientSend(String msg) {
        try {
            // 버퍼에 작성
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // 데이터 전송 성공 이벤트 발생
            onDataSentListener.onDataSent(msg, false);
        } catch (IOException e) {
            // 데이터 전송 실패 이벤트 발생
            onDataSentListener.onDataSent(msg, true);

            // 오류 기록 출력
            e.printStackTrace();
        }
    }

    private void clientClose() {
        try {
            if(inputStream != null)
                inputStream.close();
            if(outputStream != null)
                outputStream.close();
            if(socket != null)
                socket.close();

            queueSend = null;

            inputStream = null;
            outputStream = null;
            socket = null;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region [ 클라이언트 함수 ]
    public void request(Type type, String content) {
        synchronized (sendThread) {
            try {
                queueSend.add(ClientUtility.toJSONString(type, content));
                sendThread.notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void request(Type type, ClientJSON clientJSON) {
        synchronized (sendThread) {
            try {
                queueSend.add(ClientUtility.toJSONString(type, clientJSON.getJSON()));
                sendThread.notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        try {
            // 소켓 초기화
            socket = new Socket(this.ip, this.port);
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = socket.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            // 작업 스레드 시작
            startThread();

            // 소켓 이벤트 발생
            onConnectedListener.onConnected(this.ip, this.port);

            // 접속 플래그 설정
            isConnected = true;
        } catch (IOException e) {
            // 소켓 초기화 실패
            onConnectErrorListener.onConnectError(this.ip, this.port, e.getMessage());

            // 오류 기록 출력
            e.printStackTrace();
        }
    }

    public void connectAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }

    public void disconnect() {
        try {
            // 소켓 종료
            clientClose();

            // 접속 플래그 설정
            isConnected = false;
        } catch (Exception e) {
            // 소켓 종료 오류
            e.printStackTrace();
        }
    }
    //endregion

    //region [ 이벤트 리스너 구성 ]
    private static onConnected onConnectedListener;
    public interface onConnected { void onConnected(String ip, int port); }
    public static void setOnConnectedListener(onConnected listener) { onConnectedListener = listener; }

    private static onConnectError onConnectErrorListener;
    public interface onConnectError { void onConnectError(String ip, int port, String result); }
    public static void setOnConnectErrorListener(onConnectError listener) { onConnectErrorListener = listener; }

    private static onDataSent onDataSentListener;
    public interface onDataSent { void onDataSent(String result, boolean isError); }
    public static void setOnDataSentListener(onDataSent listener) { onDataSentListener = listener; }

    private static onDataReceived onDataReceivedListener;
    public interface onDataReceived { void onDataReceived(String result, boolean isError); }
    public static void setOnDataReceivedListener(onDataReceived listener) { onDataReceivedListener = listener; }
    //endregion

    //region [ 소켓 스레드 서비스 ]
    private void startThread() {
        createReceiveThread();
        createSendThread();

        receiveThread.start();
        sendThread.start();
    }

    private void createSendThread() {
        // 스레드 그룹 생성
        sendGroup = new ThreadGroup(receiveGroup, "SendGroup");

        // 데몬으로 설정
        sendGroup.setDaemon(true);

        // 스레드 생성
        sendThread = new Thread(sendGroup, new Runnable() {
            @Override
            public void run() {
                while (socket != null) {
                    synchronized (sendThread) {
                        if (!queueSend.isEmpty()) {
                            String msg = queueSend.poll();
                            if (msg != null) {
                                clientSend(msg.trim());
                            }
                        } else {
                            try {
                                sendThread.wait();
                            } catch (InterruptedException e) { }
                        }
                    }
                }
            }
        });
    }

    private void createReceiveThread() {
        // 스레드 그룹 생성
        receiveGroup = new ThreadGroup("ReceiveGroup");

        // 스레드 생성
        receiveThread = new Thread(receiveGroup, new Runnable() {
            @Override
            public void run() {
                while(socket != null) {
                    try {
                        String msg = bufferedReader.readLine();
                        if (msg != null) {
                            onDataReceivedListener.onDataReceived(msg.trim(), false);
                        }
                    } catch (SocketTimeoutException e) {
                        // 타임 아웃 메시지
                    } catch (IOException e) {
                        onDataReceivedListener.onDataReceived(e.getMessage(), true);
                        return;
                    }
                }
            }
        });
    }
    //endregion

}
