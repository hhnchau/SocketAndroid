package vn.ptt.socketserverclient.v4;

public class SocketPort {
    private int port;
    private boolean isUsed;

    public SocketPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
