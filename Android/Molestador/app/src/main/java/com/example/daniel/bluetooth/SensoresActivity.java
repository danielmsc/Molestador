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

public class SensoresActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorProximity;
    private Sensor mSensorLight;
    private Sensor mSensorAcelerometer;
    private Sensor mSensorMagneticField;
    private TextView txvProximidad;
    private TextView txvPasadas;
    private TextView txvGiros;
    private TextView txvGrados;
    private TextView txvLuz;
    private TextView txvRoll;
    private TextView txvMensaje;
    private float rotationMatrix[] = new float[9];
    private float orientationAngles[] = new float[3];
    private float accelerometerData[] = new float[3];
    private float magnetometerData[] = new float[3];
    private float lux;

    private boolean mediaPasada;
    private int pasadasProximidad;
    private ProgressBar barraPasadas;
    private boolean isPasadas_completadas = false;

    private int contadorGiros;
    private int angulo;
    private Double angle;
    private boolean izquierda;
    private boolean derecha;
    private ProgressBar barraIzq;
    private ProgressBar barraDer;
    private ProgressBar barraCircular;
    private boolean isGiros_completados = false;

    private boolean isSuficienteLuz = false;

    private static final int LUZ_MINIMA = 50;
    private static final int LUZ_MAXIMA = 300;
    private static final int CANT_PASADAS = 10;
    private static final int CANT_GIRO = 5;
    private static final int GRADOS_GIRO = 30;

    private EscribirBluetooth salida;
    private boolean yaTermino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        Bundle bundle = getIntent().getExtras();
        boolean visible = bundle.getBoolean("visible");
        if(!visible) {
            moveTaskToBack(true);
        }
        lanzarNotificacion();

        salida = EscribirBluetooth.getInstance();
        yaTermino = true;

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        txvLuz = findViewById(R.id.TextLuz);
        txvRoll = findViewById(R.id.TextRoll);
        txvGiros = findViewById(R.id.TextGiros);
        txvGrados = findViewById(R.id.TextGrados);
        txvPasadas = findViewById(R.id.TextPasadas);
        txvProximidad = findViewById(R.id.TextProximidad);

        txvMensaje = findViewById(R.id.TextMensaje);
        txvMensaje.setVisibility(View.INVISIBLE);
        txvMensaje.setTextColor(Color.RED);

        barraPasadas = findViewById(R.id.circularProgressbar2);
        barraPasadas.setProgress(0);
        barraPasadas.setMax(CANT_PASADAS);

        barraIzq = findViewById(R.id.progressIzq);
        barraIzq.setMax(GRADOS_GIRO);
        barraIzq.setScaleY(3f);

        barraDer = findViewById(R.id.progressDer);
        barraDer.setMax(GRADOS_GIRO);
        barraDer.setScaleY(3f);

        barraCircular = findViewById(R.id.circularProgressbar);
        barraCircular.setProgress(0);
        barraCircular.setMax(CANT_GIRO);

        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorAcelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(mSensorLight == null) {
            txvLuz.setText(sensor_error);
        }
        if(mSensorProximity == null) {
            txvProximidad.setText(sensor_error);
        }
        if(mSensorAcelerometer == null) {
            txvRoll.setText(sensor_error);
        }
        if(mSensorMagneticField == null) {
            txvRoll.setText(sensor_error);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        txvGiros.setText(getResources().getString(R.string.girosContador, contadorGiros));
        txvRoll.setText(getResources().getString(R.string.label_proximity, angle));
        txvGrados.setText(getResources().getString(R.string.progreso, angulo));

        if(mSensorProximity != null) {
            mSensorManager.registerListener(this, mSensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(mSensorAcelerometer != null) {
            mSensorManager.registerListener(this, mSensorAcelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(mSensorMagneticField != null) {
            mSensorManager.registerListener(this, mSensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        switch(sensorType) {
            case Sensor.TYPE_LIGHT:
                txvLuz.setText(getResources().getString(R.string.label_light, event.values[0]));
                lux = event.values[0];
                if(isSuficienteLuz == false && lux > LUZ_MAXIMA) {
                    isSuficienteLuz = true;
                } else if(isSuficienteLuz == true && lux < LUZ_MINIMA) {
                    isSuficienteLuz = false;
                }
                break;

            case Sensor.TYPE_PROXIMITY:
                if (!isPasadas_completadas) {
                    contarPasadas(event.values[0]);
                }
                break;

            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, accelerometerData,0, 3);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, magnetometerData, 0, 3);
                break;

            default:
                return;
        }

        if(sensorType != Sensor.TYPE_LIGHT && sensorType != Sensor.TYPE_PROXIMITY) {

            computeOrientation();

            if (!isGiros_completados) {
                printRoll(lux);
            }
        }
    }

    private void contarPasadas(float valorLuz) {
        if( !mediaPasada && (valorLuz == 3 || valorLuz == 1)) {
            mediaPasada = true;
        } else if(valorLuz == 100 && mediaPasada){
            mediaPasada = false;
            pasadasProximidad++;
            salida.escribir(MensajeTx.INFO_DESAFIO, generarStringInfoSensores(pasadasProximidad, contadorGiros));
            barraPasadas.setProgress(pasadasProximidad);
            isPasadas_completadas = pasadasProximidad == CANT_PASADAS;
        }

        txvProximidad.setText(getResources().getString(R.string.label_proximity, valorLuz));
        txvPasadas.setText(getResources().getString(R.string.pasadasProximidad, pasadasProximidad));

        if (isPasadas_completadas){
            Context contexto = getApplicationContext();
            CharSequence texto = "¡Pasadas completadas!";
            int duracion = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(contexto,texto, duracion);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL,0,-15);
            toast.show();

            if(isGiros_completados) {
                salir();
            }
        }
    }

    private void computeOrientation() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
    }

    private void printRoll(float lux) {
        if (!isSuficienteLuz){
            txvMensaje.setVisibility(View.VISIBLE);
        } else {
            txvMensaje.setVisibility(View.INVISIBLE);

            angle = Math.toDegrees(orientationAngles[2]);
            angulo = Math.abs(angle.intValue()); // angulo lo paso a entero y saco el modulo

            int progresoIzq = angle < 0 ? angulo : 0 ; // Valor negativo: inclinacion hacia la izquierda
            int progresoDer = angle > 0 ? angulo : 0 ; // Valor positivo: inclinacion hacia la derecha

            barraIzq.setProgress(izquierda ? GRADOS_GIRO : progresoIzq);
            barraDer.setProgress(derecha ? GRADOS_GIRO : progresoDer);

            if( !izquierda && angle < -GRADOS_GIRO ) {
                izquierda = true;
            } else if( !derecha && angle > GRADOS_GIRO) {
                derecha = true;
            }

            if ( izquierda && derecha ){ // && contadorGiros < CANT_GIRO
                contadorGiros++;
                salida.escribir(MensajeTx.INFO_DESAFIO, generarStringInfoSensores(pasadasProximidad, contadorGiros));
                izquierda = false;
                derecha = false;
                isGiros_completados = contadorGiros == CANT_GIRO;
            }

            barraCircular.setProgress(contadorGiros);
            txvRoll.setText(getResources().getString(R.string.roll, angle));
            txvGrados.setText(getResources().getString(R.string.progreso, angle.intValue()));
            txvGiros.setText(getResources().getString(R.string.girosContador,contadorGiros));

            if (isGiros_completados){
                Context contexto = getApplicationContext();
                CharSequence texto = "¡Giros completados!";
                int duracion = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(contexto,texto, duracion);
                toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,55);
                toast.show();

                if(isPasadas_completadas) {
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

    private String generarStringInfoSensores(int pasadasProximidad, int contadorGiros) {
        return new String("Pasadas: " + pasadasProximidad + " - Giros: " + contadorGiros);
    }

    private void lanzarNotificacion() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notifAlarma")
                .setSmallIcon(R.drawable.ic_stat_access_alarm)
                .setContentTitle("Desactivar alarma")
                .setContentText("Abrir Molestador para desactivar Arduino")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setVibrate(new long[] {0, 1000, 1000, 1000});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, mBuilder.build());
    }

    private void borrarNotificacion() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    private void salir() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        salida.escribir(MensajeTx.APAGAR_ALARMA, MensajeTx.APAGAR_ALARMA.toString());
        vibrator.vibrate(new long[]{0, 100, 100, 100}, -1);
        Toast.makeText(SensoresActivity.this, "Alarma desactivada", Toast.LENGTH_SHORT).show();
        finish();
    }
}
