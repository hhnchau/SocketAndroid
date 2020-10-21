package vn.ptt.socketserverclient.v4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vn.ptt.socketserverclient.R;
import vn.ptt.socketserverclient.v2.SocketServer;

public class ActivityServer extends AppCompatActivity {
    private SocketHelper socketHelper;

    TextView infoIp, msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_v2);

        infoIp = findViewById(R.id.infoip);
        msg = findViewById(R.id.msg);

        socketHelper = new SocketHelper();
        socketHelper.startRemote(new MessageListener() {
            @Override
            public void onConnected(final String ip, final int port) {
                Log.e("SOCKET", ip + ":" + port);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityServer.this, "Connected from: " + ip +":"+ port, Toast.LENGTH_SHORT).show();
                        msg.setText("Connected from: "+ ip +":"+ port);
                    }
                });
            }

            @Override
            public void onDisconnect() {
                Log.e("SOCKET", "disconnect");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityServer.this, "Disconnect", Toast.LENGTH_SHORT).show();
                        msg.setText("disconnect");
                    }
                });
            }

            @Override
            public void onMessageReceived(final String message) {
                Log.e("SOCKET", message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityServer.this, "Message: " + message, Toast.LENGTH_SHORT).show();
                        msg.setText(message);
                    }
                });
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketHelper.sendCommand(1, "String");
            }
        });
    }
}
