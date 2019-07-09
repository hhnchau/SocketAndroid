package vn.ptt.socketserverclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
}
