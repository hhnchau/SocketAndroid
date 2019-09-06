package vn.ptt.socketserverclient.v2;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends AsyncTask<Void, Void, String> {
    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;

    SocketClient(String dstAddress, int dstPort, TextView textResponse) {
        this.dstAddress = dstAddress;
        this.dstPort = dstPort;
        this.textResponse = textResponse;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Socket socket = null;
        try {
            socket = new Socket(dstAddress, dstPort);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead;
            InputStream inputStream = socket.getInputStream();
            while((bytesRead = inputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        }catch (IOException e){
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }finally {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        textResponse.setText(s);
        super.onPostExecute(s);
    }
}
