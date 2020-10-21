package vn.ptt.socketserverclient.v4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SocketServer implements Runnable, SocketListener {
    public static final int[] LIST_PORTS = {2515, 2516, 2517, 2518, 2519};
    private int port;
    private ServerSocket mServerSocket;
    private Thread mThreadServer;
    private boolean isRunning;
    private int numberClient;
    private int maxClient;
    private String password = "";
    private MessageListener messageListener;
    private List<ServerMessageWorker> clientWorkers = new ArrayList<>();
    private List<SocketPort> ports = new ArrayList<>();


    public void setSocketListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public SocketServer(int port, int maxClient) {
        this.port = port;
        this.maxClient = maxClient;
        for (int i = 0; i < maxClient; i++) {
            ports.add(new SocketPort(LIST_PORTS[i]));
        }
        try {
            mServerSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(1000);
                if (numberClient < maxClient) {
                    if (mServerSocket != null) {
                        Socket socket = mServerSocket.accept();
                        if (socket != null) {
                            ServerMessageWorker serverMessageWorker = new ServerMessageWorker(socket, port, password);
                            serverMessageWorker.setSocketListener(this);
                            serverMessageWorker.startConnect();
                        }
                    }
                }
                autoCheckRefresh();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                if (messageListener != null) messageListener.onDisconnect();
            }
        }
    }

    private void autoCheckRefresh() {
        if (clientWorkers != null) {
            synchronized (clientWorkers) {
                Iterator<ServerMessageWorker> iterator = clientWorkers.iterator();
                while (iterator.hasNext()) {
                    ServerMessageWorker serverMessageWorker = iterator.next();
                    if (!serverMessageWorker.checkRealConnect()) {
                        SocketPort port = serverMessageWorker.getSocketPort();
                        if (port != null) port.setUsed(false);
                        iterator.remove();
                    }
                }
                numberClient = clientWorkers.size();
                if (numberClient == 0) {
                    if (messageListener != null) messageListener.onDisconnect();
                }
            }
        }
    }

    private SocketPort getFreeSocketPort() {
        for (SocketPort port : ports) {
            if (!port.isUsed()) {
                return port;
            }
        }
        return null;
    }

    public void start() {
        if (mThreadServer == null) {
            mThreadServer = new Thread(this);
            isRunning = true;
            mThreadServer.start();
        }
    }

    public void stop() {
        messageListener = null;
        if (clientWorkers != null && clientWorkers.size() > 0) {
            synchronized (clientWorkers) {
                for (MessageWorker messageWorker : clientWorkers) {
                    messageWorker.sendMessage(MessageWorker.SESSION_END);
                    messageWorker.setMessageListener(null);
                    messageWorker.stopRunning();
                }
            }
            clientWorkers.clear();
        }
        numberClient = 0;
        onDestroy();
    }

    public void onDestroy() {
        isRunning = false;
        if (mThreadServer != null) {
            mThreadServer.interrupt();
            mThreadServer = null;
        }

        try {
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ports != null) {
            ports.clear();
            ports = null;
        }
        clientWorkers = null;
    }

    public void sendMessage(String message) {
        if (clientWorkers != null && clientWorkers.size() > 0) {
            for (ServerMessageWorker serverMessageWorker : clientWorkers) {
                if (serverMessageWorker.getStatus() == MessageWorker.CONNECTED) {
                    serverMessageWorker.sendMessage(message);
                }
            }
        }
    }

    public int getStatus() {
        if (clientWorkers != null && clientWorkers.size() > 0) {
            for (ServerMessageWorker serverMessageWorker : clientWorkers) {
                if (serverMessageWorker.getStatus() == MessageWorker.CONNECTED)
                    return MessageWorker.CONNECTED;
            }
        }
        return MessageWorker.DISCONNECTED;
    }

    @Override
    public void onDisconnect(MessageWorker messageWorker) {
        if (clientWorkers != null && messageWorker != null) {
            synchronized (clientWorkers) {
                clientWorkers.remove(messageWorker);
                numberClient = clientWorkers.size();
                SocketPort port = ((ServerMessageWorker) messageWorker).getSocketPort();
                if (port != null) port.setUsed(false);
            }
            messageWorker.setMessageListener(null);
            messageWorker.stopRunning();
        }
        if (numberClient == 0 && messageListener != null) messageListener.onDisconnect();
    }

    @Override
    public void onConnect(MessageWorker messageWorker) {
        if (clientWorkers != null && messageWorker != null) {
            if (numberClient < maxClient) {
                synchronized (clientWorkers) {
                    SocketPort port = getFreeSocketPort();
                    if (port != null) {
                        port.setUsed(true);
                        clientWorkers.add((ServerMessageWorker) messageWorker);
                        ((ServerMessageWorker) messageWorker).setSocketPort(port);
                        numberClient = clientWorkers.size();
                        messageWorker.setMessageListener(messageListener);
                        messageWorker.startRunning();
                        return;
                    }
                }
            }
            messageWorker.stopRunning();
        }
    }
}
