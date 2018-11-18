package com.example.daniel.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private BluetoothAdapter bt;
    private BluetoothDevice device;
    private BluetoothSocket tmp = null;
    private BluetoothSocket mmSocket = null;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "Main";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_CANCELED) {
                new DialogoNoBluetooth().show(getSupportFragmentManager(), "dialogo");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = BluetoothAdapter.getDefaultAdapter();

        if (bt == null) {
            new DialogoNoBluetooth().show(getSupportFragmentManager(), "dialogo");
        }

        if (!bt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Button btnAyuda = findViewById(R.id.btnAyuda);
        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AyudaActivity.class);
                startActivity(intent);
            }
        });

        Button btnGetDevices = findViewById(R.id.btnGet);
        btnGetDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();
                ArrayList listDevice = new ArrayList();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceMAC = device.getAddress();
                        listDevice.add(deviceName + "\n" + deviceMAC);
                    }
                }

                listView = findViewById(R.id.deviceList);
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listDevice);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String data[] = ((String) listView.getItemAtPosition(position)).split("\n");

                        BluetoothConnect conectar = new BluetoothConnect(data[1], device, bt);

                        conectar.run();
                        mmSocket = conectar.getSocket();

                        if(mmSocket.isConnected()) {
                            Toast.makeText(MainActivity.this, "Conectado", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Bt conectado");
                            Intent intent = new Intent(MainActivity.this, ProgramacionActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "No conectado", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Bt no conectado");
                        }
                    }
                });
            }
        });
    }
}