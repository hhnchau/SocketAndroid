package vn.ptt.socketserverclient.v4;

public interface SocketListener {
    void onDisconnect(MessageWorker messageWorker);
    void onConnect(MessageWorker messageWorker);
}
