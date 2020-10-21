package vn.ptt.socketserverclient.v4;

import android.os.StrictMode;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class MessageWorker implements Runnable {
    public static final int CONNECTING = 0;
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    public static final String SESSION_END = "$$$";
    private boolean isRunning;
    protected int status;
    protected int port;
    protected String serverIp;
    private StringBuffer mSendStrBuffer = new StringBuffer();
    protected BufferedReader mInputReader;
    protected PrintWriter mOutWriter;
    protected Socket mSocket;
    private boolean isFirstConnected;
    private boolean isSendingMessage;
    private Thread mThreadClient;
    private MessageListener messageListener;

    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public abstract void startConnect();

    public abstract void onPreConnect();

    public abstract void stopConnect();

    public MessageWorker(Socket socket, int port){
        this.mSocket = socket;
        this.status = DISCONNECTED;
        this.port = port;
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(20);
                if (status == CONNECTED)
                    socketConnected();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void socketConnected() {
        if (mSendStrBuffer == null || mOutWriter == null || mSocket == null) return;
        if (!mSocket.isConnected()) {
            stopConnect();
            return;
        }
        if (mOutWriter.checkError()) {
            stopConnect();
            return;
        }

        String str;
        try {
            if (mInputReader.ready()){
                str = mInputReader.readLine();
                if (str != null && str.length() != 0){
                    if (SESSION_END.equals(str)){
                        stopConnect();
                    }else {
                       if (messageListener != null){
                           messageListener.onMessageReceived(str);
                       }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            stopConnect();
        }
    }

    public boolean checkRealConnect(){
        if (mSocket != null) return mSocket.isConnected();
        return false;
    }


    public void sendMessage(String message) {
        if (isSendingMessage || mSendStrBuffer == null || mOutWriter == null || TextUtils.isEmpty(message))
            return;

        if (status == CONNECTED) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            isSendingMessage = true;
            mSendStrBuffer.append(message).append("\n");
            mOutWriter.print(mSendStrBuffer);
            mOutWriter.flush();
            mSendStrBuffer.setLength(0);
            isSendingMessage = false;
        } else {
            isSendingMessage = false;
        }
    }

    public void startRunning() {
        if (mThreadClient == null) {
            mThreadClient = new Thread(this);
            isRunning = true;
            mThreadClient.start();
            isSendingMessage = false;

            if (!isFirstConnected) {
                isFirstConnected = true;
                onPreConnect();
            }
            if (messageListener != null) messageListener.onConnected(serverIp, port);
        }
    }

    public void stopRunning() {
        isRunning = false;
        status = DISCONNECTED;
        isSendingMessage = false;
        if (mThreadClient != null) {
            mThreadClient.interrupt();
            mThreadClient = null;
        }

        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getStatus(){
        return status;
    }

    public int getPort(){
        return port;
    }

    public Socket getSocket(){
        return mSocket;
    }

}
