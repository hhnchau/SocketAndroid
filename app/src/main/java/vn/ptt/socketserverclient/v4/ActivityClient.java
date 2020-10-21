package vn.ptt.socketserverclient.v4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vn.ptt.socketserverclient.R;

public class ActivityClient extends AppCompatActivity implements MessageListener {
    TextView response;
    EditText editTextAddress, editTextPort, edtInput;
    Button buttonConnect, buttonClear;

    private vn.ptt.socketserverclient.v4.SocketClient socketClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_v2);


        editTextAddress = findViewById(R.id.addressEditText);
        editTextAddress.setText("192.168.31.25");
        editTextPort = findViewById(R.id.portEditText);
        editTextPort.setText("1989");
        edtInput = findViewById(R.id.input);
        edtInput.setText("text String");
        buttonConnect = findViewById(R.id.connectButton);
        buttonClear = findViewById(R.id.clearButton);
        response = findViewById(R.id.responseTextView);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketClient = new vn.ptt.socketserverclient.v4.SocketClient(editTextAddress.getText().toString(), Integer.parseInt(editTextPort.getText().toString()));
                socketClient.setMessageListener(ActivityClient.this);
                socketClient.start();

            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                response.setText("");
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketClient.sendMessage(edtInput.getText().toString());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        socketClient.stop();
    }

    @Override
    public void onConnected(final String ip, final int port) {
        Log.e("SOCKET", ip + ":" + port);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActivityClient.this, "Connected from: " + ip + ":" + port, Toast.LENGTH_SHORT).show();
                response.setText("Connected from: "+ ip +":"+ port);
            }
        });
    }

    @Override
    public void onDisconnect() {
        Log.e("SOCKET", "disconnect");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActivityClient.this, "Disconnect", Toast.LENGTH_SHORT).show();
                response.setText("disconnect");
            }
        });
    }

    @Override
    public void onMessageReceived(final String message) {
        Log.e("SOCKET", message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActivityClient.this, "Message: " + message, Toast.LENGTH_SHORT).show();
                response.setText(message);
            }
        });
    }
}
