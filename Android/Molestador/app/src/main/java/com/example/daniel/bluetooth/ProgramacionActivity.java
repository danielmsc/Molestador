package com.example.daniel.bluetooth;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

public class ProgramacionActivity extends AppCompatActivity {

    private DatosBluetooth btDatos;
    private EscribirBluetooth salida;
    private LeerBluetooth leer;
    private TextView recibido;
    private Handler handler;
    private boolean esVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programacion);

        handler = new Handler(Looper.getMainLooper());
        btDatos = DatosBluetooth.getInstance();
        salida = EscribirBluetooth.getInstance();

        Button btn = findViewById(R.id.buttonEnviarHora);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarHoraMilis();
            }
        });

        final TextView horaSeleccionada = findViewById(R.id.textHora);
        Button btnProgAlarma = findViewById(R.id.buttonProgAlarma);
        btnProgAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(ProgramacionActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String hora = String.format("%02d:%02d", hourOfDay, minute);
                                horaSeleccionada.setText("Alarma programada: " + hora);
                                salida.escribir(Mensaje.SET_ALARMA, hora);
                            }
                        }, mHour, mMinute, true);
                tpd.show();
            }
        });

        if(Looper.myLooper() == null) {
            Looper.prepare();
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String cad = bundle.getString("recibido");
                recibido.setText(cad);
                //Si la cadena que recibe es "APAGAR", lanzo la actividad de gestos de desbloqueo
                if(cad.equals("APAGAR")) {
                    Intent intent = new Intent(ProgramacionActivity.this, SensoresActivity.class);
                    intent.putExtra("visible", esVisible);
                    startActivity(intent);
                }
            }
        };
        recibido = findViewById(R.id.textViewData);
        leer = new LeerBluetooth(handler);
        leer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        esVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        esVisible = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leer.interrupt();
        btDatos.cerrarConexiones();
    }

    private void enviarHoraMilis() {
        long unixTime = (System.currentTimeMillis() - 10800000) / 1000;
        salida.escribir(Mensaje.SET_HORA, Long.toString(unixTime));
    }
}