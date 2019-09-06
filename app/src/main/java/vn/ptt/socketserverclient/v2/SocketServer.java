package vn.ptt.socketserverclient.v2;

import android.app.Activity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class SocketServer {
    private Activity activity;
    private ServerSocket serverSocket;
    private String message = "";
    private static final int PORT = 8080;

    public SocketServer(Activity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

    }

    private class SocketServerThread extends Thread {
        private int count = 0;

        @Override
        public void run() {
            super.run();

            try {
                serverSocket = new ServerSocket(PORT);
                while (true) {

                    Socket socket = serverSocket.accept();
                    count++;
                    message += "#" + count + " From " + socket.getInetAddress() + socket.getPort() + "\n";

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ActivityServer)activity).msg.setText(message);
                        }
                    });

                    SocketServerReplayThread socketServerReplayThread = new SocketServerReplayThread(socket, count);
                    socketServerReplayThread.run();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class SocketServerReplayThread extends Thread {
        private Socket socket;
        private int count;

        SocketServerReplayThread(Socket socket, int count) {
            this.socket = socket;
            this.count = count;
        }

        @Override
        public void run() {
            super.run();

            OutputStream outputStream;
            String msg = "Hello From Server, you are #" + count;

            try {
                outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.println(msg);
                printStream.close();

                message += "Replayed: " + msg + "\n";
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ActivityServer)activity).msg.setText(message);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ActivityServer)activity).msg.setText(message);
                    }
                });
            }
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumerationInterface = NetworkInterface.getNetworkInterfaces();
            while (enumerationInterface.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationInterface.nextElement();
                Enumeration<InetAddress> enumerationAddress = networkInterface.getInetAddresses();
                while (enumerationAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumerationAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at: " + inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    public String getPort(){
        return String.valueOf(PORT);
    }

    public void onDestroy(){
        if (serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
