package vn.ptt.socketserverclient.v4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMessageWorker extends MessageWorker {
    private SocketListener socketListener;

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public ClientMessageWorker(Socket socket, int port) {
        super(socket, port);
    }

    @Override
    public void startConnect() {
        try {
            status = CONNECTING;
            mInputReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mOutWriter = new PrintWriter(mSocket.getOutputStream(), true);
            status = CONNECTED;
            serverIp = mSocket.getInetAddress().getHostAddress();
            if (socketListener != null) socketListener.onConnect(this);
        } catch (IOException e) {
            e.printStackTrace();
            stopConnect();
        }
    }

    @Override
    public void onPreConnect() {

    }

    @Override
    public void stopConnect() {
        sendMessage(SESSION_END);
        if (socketListener != null) socketListener.onDisconnect(this);
    }
}
