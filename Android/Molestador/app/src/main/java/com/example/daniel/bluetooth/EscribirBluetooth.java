package com.example.daniel.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class EscribirBluetooth {

    private static EscribirBluetooth obj;
    private DatosBluetooth btDatos;
    private BluetoothSocket socket;
    private OutputStream out;
    private static final String TAG = "Escribir BT";

    private EscribirBluetooth(){
        this.btDatos = DatosBluetooth.getInstance();
        this.socket = btDatos.getSocket();
        this.out = btDatos.getOut();
    }

    public static EscribirBluetooth getInstance() {
        if(obj == null) {
            obj = new EscribirBluetooth();
        }
        return obj;
    }

    public void escribir(MensajeTx tipoMensajeTx, String cadena) {
        if(socket.isConnected()) {
            try {
                String enviar = tipoMensajeTx.ordinal() + cadena;
                out.write(enviar.getBytes(Charset.forName("UTF-8")));
            } catch (IOException e) {
                Log.e(TAG, "Error al escribir al BT");
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
