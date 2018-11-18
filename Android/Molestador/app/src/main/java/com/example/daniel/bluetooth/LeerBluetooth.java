package com.example.daniel.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.util.Scanner;

public class LeerBluetooth extends Thread {

    private DatosBluetooth btDatos;
    private BluetoothSocket socket;
    private InputStream in;
    private static final String TAG = "Leer BT";
    private Handler handler;
    private int lecturas;
    private String cadenaRecibida = "";
    private Scanner sc;

    public LeerBluetooth(Handler handler) {
        this.btDatos = DatosBluetooth.getInstance();
        this.in = btDatos.getIn();
        this.socket = btDatos.getSocket();
        this.handler = handler;
        this.sc = new Scanner(this.in);
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()&& socket.isConnected()) {
            try {
                if(sc.hasNextLine()) {
                    cadenaRecibida = sc.nextLine();
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("recibido", cadenaRecibida);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Log.d(TAG, "Recibido " + cadenaRecibida);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al leer desde BT");
                Log.e(TAG, e.getMessage());
            }
        }

        try {
            sc.close();
            return;
        } catch (Exception e) {
            Log.e(TAG, "Error al cerrar el scanner");
        }
    }
}