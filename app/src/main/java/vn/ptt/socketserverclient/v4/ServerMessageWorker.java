package vn.ptt.socketserverclient.v4;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerMessageWorker extends MessageWorker {
    private SocketListener socketListener;
    private SocketPort socketPort;
    private String password;

    public SocketPort getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(SocketPort socketPort) {
        this.socketPort = socketPort;
    }

    public ServerMessageWorker(Socket socket, int port, String password) {
        super(socket, port);
        this.password = password;
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    @Override
    public void startConnect() {
        if (mSocket != null){
            try {
                status = CONNECTING;
                mInputReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOutWriter = new PrintWriter(mSocket.getOutputStream(), true);
                status = CONNECTED;
                serverIp = mSocket.getInetAddress().getHostAddress();
                if (socketListener != null) socketListener.onConnect(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreConnect() {
        if (socketPort != null){
            String data = "COMMAND_SEND_DATA"+":"+socketPort.getPort();
            if (password != null && !TextUtils.isEmpty(password))
                data +=","+password;
            sendMessage(data);
        }
    }

    @Override
    public void stopConnect() {
        sendMessage(SESSION_END);
        if (socketListener != null) socketListener.onDisconnect(this);
    }
}
