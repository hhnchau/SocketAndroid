package vn.ptt.socketserverclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import vn.ptt.socketserverclient.v2.ActivityClient;
import vn.ptt.socketserverclient.v2.ActivityServer;
import vn.ptt.socketserverclient.v3.ClientSocket;
import vn.ptt.socketserverclient.v3.SocketServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void server(View view) {
        startActivity(new Intent(this, Server.class));
    }

    public void client(View view) {
        startActivity(new Intent(this, Client.class));
    }

    public void server2(View view) {
        startActivity(new Intent(this, ActivityServer.class));
    }

    public void client2(View view) {
        startActivity(new Intent(this, ActivityClient.class));
    }

    public void server3(View view) {
        startActivity(new Intent(this, SocketServer.class));
    }

    public void client3(View view) {
        startActivity(new Intent(this, ClientSocket.class));
    }
}
