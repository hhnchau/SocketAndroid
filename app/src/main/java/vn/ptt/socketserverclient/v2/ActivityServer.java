package vn.ptt.socketserverclient.v2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import vn.ptt.socketserverclient.R;

public class ActivityServer extends AppCompatActivity {
    private SocketServer socketServer;
    TextView infoIp, msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_v2);
        infoIp = findViewById(R.id.infoip);
        msg = findViewById(R.id.msg);
        socketServer = new SocketServer(this);

        infoIp.setText(socketServer.getIpAddress() + ":" + socketServer.getPort());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketServer.onDestroy();
    }
}
