package vn.ptt.socketserverclient.v4;

import android.os.Handler;

public class SocketHelper {
    public static final int REMOTE_PORT = 1989;
    public static final int MAX_CLIENT_CONNECTED = 5;
    private SocketServer mSocketServer;
    private Handler mRemoteHandle = new Handler();


    public void startRemote(MessageListener socketListener) {
        stopRemote();
        mSocketServer = new SocketServer(REMOTE_PORT, MAX_CLIENT_CONNECTED);
        mSocketServer.setSocketListener(socketListener);
        mSocketServer.start();
    }

    private void stopRemote() {
        mRemoteHandle.removeCallbacksAndMessages(null);
        if (mSocketServer != null) {
            mSocketServer.setSocketListener(null);
            mSocketServer.stop();
            mSocketServer = null;
        }
    }

    public synchronized boolean sendCommand(int command, String extra) {
        boolean isConnect = isConnect();
        if (isConnect) {
            String message = command + "/" + extra;
            mSocketServer.sendMessage(message);
            return true;
        }
        return false;
    }

    public boolean isConnect() {
        return mSocketServer != null && mSocketServer.getStatus() == MessageWorker.CONNECTED;
    }
}
