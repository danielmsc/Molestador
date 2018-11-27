package com.example.daniel.bluetooth;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class SensoresActivity extends AppCompatActivity implements SensorEventListener {

    private Toast toast;
    private Context contexto;

    private SensorManager sensorManager;

    private Sensor sensorProximidad;
    private Sensor sensorLuz;
    private Sensor sensorAcelerometro;
    private Sensor sensorCampoMagnetico;

    private TextView txvProximidad;
    private TextView txvPasadas;
    private TextView txvGiros;
    private TextView txvGrados;
    private TextView txvLuz;
    private TextView txvRoll;
    private TextView txvMensaje;

    private float matrixI[] = null;
    private float matrixR[] = new float[9];
    private float values[] = new float[3];
    private float gravity[] = new float[3];
    private float geomagnetic[] = new float[3];

    private int duracion = Toast.LENGTH_SHORT;
    private int contadorPasadas;
    private int contadorGiros;
    private int angulo;
    private int proximity = 100;
    private Double angle = 0.0;

    private CharSequence texto;

    private ProgressBar pbIzquierda;
    private ProgressBar pbDerecha;
    private ProgressBar pbGiros;
    private ProgressBar pbPasadas;

    private boolean derecha;
    private boolean izquierda;
    private boolean mediaPasada;
    private boolean isSuficienteLuz;
    private boolean isGirosCompletados;
    private boolean isPasadasCompletadas;

    private static final int LUZ_MINIMA = 50;
    private static final int LUZ_MAXIMA = 100;
    private static final int GRADOS_GIRO = 30;
    private static final int CANT_PASADAS = 10;
    private static final int CANT_GIRO = 5;

    private EscribirBluetooth salida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        Bundle bundle = getIntent().getExtras();
        boolean visible = false;
        if (bundle != null) {
            visible = bundle.getBoolean("visible");
        }
        if (!visible) {
            moveTaskToBack(true);
        }
        lanzarNotificacion();

        salida = EscribirBluetooth.getInstance();

        contexto = getApplicationContext();

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        txvLuz = findViewById(R.id.TextLuz);
        txvRoll = findViewById(R.id.TextRoll);
        txvGiros = findViewById(R.id.TextGiros);
        txvGrados = findViewById(R.id.TextGrados);
        txvPasadas = findViewById(R.id.TextPasadas);
        txvProximidad = findViewById(R.id.TextProximidad);

        txvMensaje = findViewById(R.id.TextMensaje);
        txvMensaje.setVisibility(View.INVISIBLE);
        txvMensaje.setTextColor(Color.RED);

        pbPasadas = findViewById(R.id.circularProgressbarPasadas);
        pbPasadas.setProgress(0);
        pbPasadas.setMax(CANT_PASADAS);

        pbIzquierda = findViewById(R.id.progressIzq);
        pbIzquierda.setMax(GRADOS_GIRO);
        pbIzquierda.setScaleY(3f);

        pbDerecha = findViewById(R.id.progressDer);
        pbDerecha.setMax(GRADOS_GIRO);
        pbDerecha.setScaleY(3f);

        pbGiros = findViewById(R.id.circularProgressbarGiros);
        pbGiros.setProgress(0);
        pbGiros.setMax(CANT_GIRO);

        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorCampoMagnetico = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (sensorLuz == null) {
            txvLuz.setText(sensor_error);
        }
        if (sensorProximidad == null) {
            txvProximidad.setText(sensor_error);
        }
        if (sensorAcelerometro == null) {
            txvRoll.setText(sensor_error);
        }
        if (sensorCampoMagnetico == null) {
            txvRoll.setText(sensor_error);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        txvPasadas.setText(getResources().getString(R.string.pasadasProximidad, contadorPasadas));
        txvGiros.setText(getResources().getString(R.string.girosContador, contadorGiros));
        txvProximidad.setText(getResources().getString(R.string.proximidad, proximity));
        txvGrados.setText(getResources().getString(R.string.progreso, angulo));
        txvRoll.setText(getResources().getString(R.string.roll, angle));

        int samplingFrequency = SensorManager.SENSOR_DELAY_NORMAL;
        SensorEventListener listener = this;

        if (sensorProximidad != null) {
            sensorManager.registerListener(listener, sensorProximidad, samplingFrequency);
        }

        if (sensorLuz != null) {
            sensorManager.registerListener(listener, sensorLuz, samplingFrequency);
        }

        if (sensorAcelerometro != null) {
            sensorManager.registerListener(listener, sensorAcelerometro, samplingFrequency);
        }

        if (sensorCampoMagnetico != null) {
            sensorManager.registerListener(listener, sensorCampoMagnetico, samplingFrequency);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] valores = event.values;

        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                int valorLuz = (int) valores[0];
                texto = getResources().getString(R.string.luzAmbiente, valorLuz);
                txvLuz.setText(texto);

                if (!isSuficienteLuz && valorLuz > LUZ_MAXIMA) {
                    isSuficienteLuz = true;
                } else if (isSuficienteLuz && valorLuz < LUZ_MINIMA) {
                    isSuficienteLuz = false;
                }
                break;

            case Sensor.TYPE_PROXIMITY:
                proximity = (int) valores[0];
                if (!isPasadasCompletadas) {
                    contarPasadas();
                }
                break;

            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(valores, 0, gravity, 0, 3);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(valores, 0, geomagnetic, 0, 3);
                break;

            default:
                return;
        }

        //if(System.currentTimeMillis() - tiempoAnterior > 5000) {
        //    salida.escribir(MensajeTx.INFO_DESAFIO, infoPasadasGiros());
        //    tiempoAnterior = System.currentTimeMillis();
        //}

        if (sensorType != Sensor.TYPE_LIGHT && sensorType != Sensor.TYPE_PROXIMITY) {

            computeOrientation();

            if (!isGirosCompletados) {
                contarGiros();
            }
        }
    }

    private void contarPasadas() {
        if (!isSuficienteLuz) {
            txvMensaje.setVisibility(View.VISIBLE);
        } else {
            txvMensaje.setVisibility(View.INVISIBLE);

            if (!mediaPasada && proximity < 4) {
                mediaPasada = true;
            } else if (proximity == 100 && mediaPasada) {
                mediaPasada = false;
                contadorPasadas++;
                salida.escribir(MensajeTx.INFO_DESAFIO, infoPasadasGiros());
                pbPasadas.setProgress(contadorPasadas);
                isPasadasCompletadas = contadorPasadas == CANT_PASADAS;
            }

            texto = getResources().getString(R.string.proximidad, proximity);
            txvProximidad.setText(texto);

            texto = getResources().getString(R.string.pasadasProximidad, contadorPasadas);
            txvPasadas.setText(texto);

            if (isPasadasCompletadas) {
                texto = "Pasadas completadas!";
                int offsetX = 0, offsetY = -15;
                int gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

                toast = Toast.makeText(contexto, texto, duracion);
                toast.setGravity(gravity, offsetX, offsetY);
                toast.show();

                if (isGirosCompletados) {
                    salir();
                }
            }
        }
    }

    private void computeOrientation() {
        SensorManager.getRotationMatrix(matrixR, matrixI, gravity, geomagnetic);
        SensorManager.getOrientation(matrixR, values);
    }

    private void contarGiros() {
        if (!isSuficienteLuz) {
            txvMensaje.setVisibility(View.VISIBLE);
        } else {
            txvMensaje.setVisibility(View.INVISIBLE);

            angle = Math.toDegrees(values[2]);
            angulo = Math.abs(angle.intValue()); // angulo lo paso a entero y saco el modulo

            // Valor negativo: inclinacion hacia la izquierda
            int progresoIzq = angle < 0 ? angulo : 0;

            // Valor positivo: inclinacion hacia la derecha
            int progresoDer = angle > 0 ? angulo : 0;

            pbIzquierda.setProgress(izquierda ? GRADOS_GIRO : progresoIzq);
            pbDerecha.setProgress(derecha ? GRADOS_GIRO : progresoDer);

            if (!izquierda && angle < -GRADOS_GIRO) {
                izquierda = true;
            } else if (!derecha && angle > GRADOS_GIRO) {
                derecha = true;
            }

            if (izquierda && derecha) {
                contadorGiros++;
                salida.escribir(MensajeTx.INFO_DESAFIO, infoPasadasGiros());
                izquierda = false;
                derecha = false;
                isGirosCompletados = contadorGiros == CANT_GIRO;
            }

            pbGiros.setProgress(contadorGiros);
            texto = getResources().getString(R.string.roll, angle);
            txvRoll.setText(texto);

            texto = getResources().getString(R.string.progreso, angle.intValue());
            txvGrados.setText(texto);

            texto = getResources().getString(R.string.girosContador, contadorGiros);
            txvGiros.setText(texto);

            if (isGirosCompletados) {
                texto = "Giros completados!";
                int offsetX = 0, offsetY = 55;
                int gravity = Gravity.CENTER | Gravity.BOTTOM;

                toast = Toast.makeText(contexto, texto, duracion);
                toast.setGravity(gravity, offsetX, offsetY);
                toast.show();

                if (isPasadasCompletadas) {
                    salir();
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        borrarNotificacion();
    }

    private String infoPasadasGiros() {
        return String.format(Locale.US, "Pasadas: %02d - Giros: %02d", contadorPasadas, contadorGiros);
    }

    private void lanzarNotificacion() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notifAlarma")
                .setSmallIcon(R.drawable.ic_stat_access_alarm)
                .setContentTitle("Desactivar alarma")
                .setContentText("Abrir Molestador para desactivar Arduino")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setVibrate(new long[]{0, 1000, 1000, 1000});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, mBuilder.build());
    }

    private void borrarNotificacion() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(0);
        }
    }

    private void salir() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        salida.escribir(MensajeTx.SIG_DESAFIO, MensajeTx.SIG_DESAFIO.toString());

        if (vibrator != null) {
            vibrator.vibrate(new long[]{0, 100, 100, 100}, -1);
        }

        texto = "Alarma desactivada";

        toast = Toast.makeText(contexto, texto, duracion);
        toast.setText(texto);
        toast.show();
        finish();
    }
}
