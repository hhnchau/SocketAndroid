package vn.ptt.socketserverclient.v4;

public interface MessageListener {
    void onConnected(String paramString, int paramInt);

    void onDisconnect();

    void onMessageReceived(String paramString);
}
