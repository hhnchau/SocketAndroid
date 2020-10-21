package vn.ptt.socketserverclient.v4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import static vn.ptt.socketserverclient.v4.MessageWorker.SESSION_END;

public class SocketClient implements Runnable, SocketListener {
    private int port;
    private String serverIp;
    private boolean isRunning;
    private Thread mThreadClient;
    private Socket mClientSocket;
    private ClientMessageWorker clientMessageWorker;
    private MessageListener messageListener;

    public SocketClient(String serverIp, int port) {
        this.port = port;
        this.serverIp = serverIp;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void run() {
        if (isRunning) {
            try {
                Thread.sleep(1000);
                if (mClientSocket == null) {
                    SocketAddress address = new InetSocketAddress(serverIp, port);
                    mClientSocket = new Socket();
                    mClientSocket.connect(address);

                    ClientMessageWorker clientMessageWorker = new ClientMessageWorker(mClientSocket, port);
                    clientMessageWorker.setSocketListener(this);
                    clientMessageWorker.startConnect();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopSocket();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                stopSocket();
            } catch (IOException e) {
                e.printStackTrace();
                stopSocket();
            }
        }
    }

    @Override
    public void onDisconnect(MessageWorker messageWorker) {
        if (messageWorker != null) {
            messageWorker.stopRunning();
            messageWorker.setMessageListener(null);

            this.clientMessageWorker = null;
            this.mClientSocket = null;
            if (messageListener != null) messageListener.onDisconnect();
        }
    }

    @Override
    public void onConnect(MessageWorker messageWorker) {
        if (messageWorker != null) {
            this.clientMessageWorker = (ClientMessageWorker) messageWorker;
            messageWorker.setMessageListener(messageListener);
            messageWorker.startRunning();
        }
    }

    public void start() {
        if (mThreadClient == null) {
            mThreadClient = new Thread(this);
            mThreadClient.setPriority(Thread.NORM_PRIORITY);
            isRunning = true;
            mThreadClient.start();
        }
    }

    public void stop() {
        if (clientMessageWorker != null) {
            sendMessage(SESSION_END);
            clientMessageWorker.setSocketListener(null);
            clientMessageWorker.stopRunning();
            clientMessageWorker = null;
        }
        isRunning = false;
        if (mThreadClient != null) {
            mThreadClient.interrupt();
            mThreadClient = null;
        }
    }

    private void stopSocket() {
        try {
            if (mClientSocket != null) {
                mClientSocket.close();
                mClientSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (mClientSocket != null && clientMessageWorker != null) {
            clientMessageWorker.sendMessage(message);
        }
    }

    public int getStatus() {
        if (clientMessageWorker != null) {
            if (clientMessageWorker.getStatus() == MessageWorker.CONNECTED) {
                return MessageWorker.CONNECTED;
            }
        }
        return MessageWorker.DISCONNECTED;
    }
}
