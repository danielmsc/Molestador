package com.example.daniel.bluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AyudaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        TextView tvAyuda = findViewById(R.id.tvAyuda);
        tvAyuda.setMovementMethod(new ScrollingMovementMethod());
        String textoAyuda = "<b>BIENVENIDOS A MOLESTADOR!</b><br>" +
                "<br>" +
                "En esta sección se explicará el uso de la aplicación Molestador.<br>" +
                "<br>" +
                "<b>--- PRIMER PASO: EMPAREJAMIENTO CON MOLESTADOR ---</b><br>" +
                "<br>" +
                "Una vez iniciada la app, es necesario que se conecte con el Bluetooth del Molestador.<br>" +
                "Para realizarlo, se debe tocar en el botón \"Dispositivos emparejados\", el cual desplegará una lista de dispositivos emparejados con el dispositivo en el cual corre la aplicación. <br>" +
                "<br>" +
                "<b>NOTA:</b> es obligatorio realizar el emparejamiento del dispositivo con el módulo Bluetooth previamente.<br><br>" +
                "<b>NOTA:</b> si el Bluetooth no se activa, o bien no está disponible en el dispositivo, la aplicación no podrá ser usada.<br>" +
                "<br>" +
                "Se debe seleccionar \"Molestador\" para realizar la conexión.<br>" +
                "<br>" +
                "<b>--- SEGUNDO PASO: CONFIGURAR HORA ---</b><br>" +
                "<br>" +
                "Una vez hecha la conexión, se debe enviar la hora actual al Molestador para su correcto funcionamiento. La misma es tomada del dispositivo.<br>" +
                "Para ello, se debe presionar en el botón \"Enviar Hora\". En la pantalla del Molestador aparecerá la hora actualizada.<br>" +
                "<br>" +
                "<b>--- TERCER PASO: CONFIGURAR UNA ALARMA ---</b><br>" +
                "<br>" +
                "El Molestador ya se encuentra a la espera de una alarma. Para programarla, se debe presionar en \"Programar Alarma\". <br>" +
                "Se desplega un selector circular de hora y minuto, en el cual el usuario puede elegir la hora exacta en la que desea que se dispare.<br>" +
                "<br>" +
                "<b>NOTA:</b> si el horario seleccionado es posterior al actual, la alarma se encuentra programada para el día actual. Caso contrario, para el día siguiente.<br>" +
                "<br>" +
                "Una vez seleccionada la hora, la misma queda registrada tanto en la pantalla del Molestador, como en la ventana de la aplicación.<br>" +
                "<br>" +
                "<b>--- CUARTO PASO: DESACTIVAR UNA ALARMA ---</b><br>" +
                "<br>" +
                "Cuando sea la hora indicada, el Molestador comenzará a sonar mediante su parlante.<br>" +
                "Para realizar el apagado, Molestador propone una serie de desafíos que deben ser resueltos, en primer lugar, en el Molestador, y en segundo lugar, en el dispositivo donde corre la app.<br>" +
                "<br>" +
                "<b>NOTA</b>: para completar todos los pasos, es obligatorio que haya luz disponible en el ambiente donde se realiza el apagado. En caso contrario, no se puede avanzar.<br>" +
                " <br>" +
                "El primer paso para apagar la alarma es realizar los dos desafíos propuestos por el Molestador. Para ello, se deben usar los sensores y la pantalla del mismo.<br>" +
                "<br>" +
                "<u>Primer desafío:</u> completar la secuencia de botones apretados.<br>" +
                "<u>Segundo desafío:</u> lograr que el píxel quede dentro del cuadrado.<br>" +
                "<br>" +
                "Una vez completados ambos desafíos, el Molestador le avisará al dispositivo para continuar la segunda etapa de apagado. En el dispositivo aparecerá una notificación indicando que se encuentran disponibles.<br>" +
                "<br>" +
                "<u>Tercer desafío:</u> completar pasadas con la mano utilizando el sensor de proximidad.<br>" +
                "<u>Cuarto desafío:</u> realizar giros en el dispositivo.<br>" +
                "<br>" +
                "Una vez completados, el dispositivo le avisará al Molestador que la alarma puede ser apagada.<br>" +
                "<br>" +
                "<b>NOTA:</b> el tercer y el cuarto desafío pueden ser realizados en cualquier orden.";

        tvAyuda.setText(Html.fromHtml(textoAyuda));
    }
}
