package com.example.daniel.bluetooth;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

public class ProgramacionActivity extends AppCompatActivity {

    private DatosBluetooth btDatos;
    private EscribirBluetooth salida;
    private LeerBluetooth leer;
    private TextView recibido;
    private boolean esVisible;

    private TextView infoDesafioBotones;
    private TextView infoDesafioMoverse;
    private TextView horaSeleccionada;

    private static final String TAG = "HANDLER";

    private static class MyHandler extends Handler {
        private final WeakReference<ProgramacionActivity> myClassWeakReference;

        private MyHandler(ProgramacionActivity instancia) {
            myClassWeakReference = new WeakReference<>(instancia);
        }

        @Override
        public void handleMessage(Message msg) {
            ProgramacionActivity myClass = myClassWeakReference.get();
            String key = "recibido", keyVisible = "visible";
            if (myClass != null) {
                Bundle bundle = msg.getData();
                String cad = bundle.getString(key);
                if (cad != null) {
                    String subCad = cad.substring(0, 1);
                    String info = cad.substring(1);
                    Integer msj = Integer.valueOf(subCad);

                    myClass.mostrarLog(cad);
                    //Si la cadena que recibe es "APAGAR", lanzo la actividad de gestos de desbloqueo
                    if (msj == MensajeRx.ACTIVAR_SENSORES.ordinal()) {
                        Intent intent = new Intent(myClass, SensoresActivity.class);
                        intent.putExtra(keyVisible, myClass.getEsVisible());
                        myClass.startActivity(intent);
                        myClass.limpiarPantalla("");
                    } else if (msj == MensajeRx.INFO_BOTONES.ordinal()) {
                        myClass.setInfoBotones(info);
                    } else {
                        myClass.setInfoMoverse(info);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programacion);

        //Handler handler = new Handler(Looper.getMainLooper());
        btDatos = DatosBluetooth.getInstance();
        salida = EscribirBluetooth.getInstance();

        Button btn = findViewById(R.id.buttonEnviarHora);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarHoraMilis();
            }
        });

        infoDesafioBotones = findViewById(R.id.textDesafioBotones);
        infoDesafioBotones.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        infoDesafioMoverse = findViewById(R.id.textDesafioMoverse);
        infoDesafioMoverse.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        horaSeleccionada = findViewById(R.id.textHora);

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
                                String formato = "%02d:%02d";
                                String hora = String.format(Locale.US, formato, hourOfDay, minute);
                                CharSequence texto = "Alarma programada: " + hora;
                                horaSeleccionada.setText(texto);
                                salida.escribir(MensajeTx.SET_ALARMA, hora);
                            }
                        }, mHour, mMinute, true);
                tpd.show();
            }
        });

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Handler handler = new MyHandler(this);
        recibido = findViewById(R.id.textViewData);
        leer = new LeerBluetooth(handler);
        leer.start();
    }

    protected boolean getEsVisible() {
        return esVisible;
    }

    void mostrarLog(String texto) {
        Log.d("Mensaje", texto);
    }

    void setInfoBotones(String texto) {
        infoDesafioBotones.setText(texto);
    }

    void setInfoMoverse(String texto) {
        infoDesafioMoverse.setText(texto);
    }

    void limpiarPantalla(String texto) {
        horaSeleccionada.setText(texto);
        infoDesafioBotones.setText(texto);
        infoDesafioMoverse.setText(texto);
    }

    public Handler getHandler() {
        return new MyHandler(this);
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
        salida.escribir(MensajeTx.SET_HORA, Long.toString(unixTime));
    }
}