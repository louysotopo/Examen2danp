package com.example.sensores.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensores.R;
import com.example.sensores.model.ABS;
import com.example.sensores.model.DistanceTraveled;
import com.example.sensores.utilities.Reload;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class DistanceTraveledActivity extends AppCompatActivity {
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    private ImageButton question;
    private TextView count;
    private Button star_end;
    private  Button send;
    private AlertDialog.Builder builder_question;
    private AlertDialog.Builder instructions;
    private int n;
    private  int state;
    //private boolean down;
    private MqttAndroidClient client;
    private int PHYISCAL_ACTIVITY;
    private boolean init;
    private int bach;
    private Reload a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_traveled);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initComponents();
        initMethods();
        connect();
        initSensor();
    }
    private void initComponents() {
        question = findViewById(R.id.imageButton_question_abs);
        count = findViewById(R.id.textView_count_abs);
        star_end = findViewById(R.id.buttonstart_end_abs);
        send = findViewById(R.id.buttonguardarabs);
        builder_question = new AlertDialog.Builder(this);
        instructions = new AlertDialog.Builder(this);
        n = 0;
        state = 0;
        init = true;
        bach = 0;
        //down = false;

    }

    private void initMethods() {
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInstructions();
            }
        });
        star_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state ==0){
                    state =1;
                    star_end.setText("Detener");
                    start_sensor();
                }else{
                    stop_sensor();
                    star_end.setText("Continuar");
                    state =0;
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question_to_send_to_server();
            }
        });

    }
    private void  ShowInstructions(){
        instructions.setTitle("Instrucciones");
        instructions.setMessage("Para empezar los ejercicios deberás presionar el botón Empezar, colocar el celular en tu bolsillo  y empezar a trotar, la aplicación contará los pasos haciendo uso del sensor de contador de pasos de tu teléfono.").show();
    }
    private void  question_to_send_to_server(){
        builder_question.setTitle("Guardar");
        builder_question.setMessage("Has realizado : "+ n+" pasos , Deseas guardarlos?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Guardando ...",Toast.LENGTH_LONG).show();
                        send_to_server();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Cancelado...",Toast.LENGTH_LONG).show();
                    }
                }).show();
    }
    private void start_sensor(){
        sensorManager.registerListener(sensorEventListener,sensor,2000*1000);
    }
    private void stop_sensor(){
        sensorManager.unregisterListener(sensorEventListener);
    }
    private  void initSensor(){
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null){
            Toast.makeText(getApplicationContext(),"No hay sensor de contado de pasos",Toast.LENGTH_LONG).show();
        }else{
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if ( init ){
                        bach = (int)event.values[0];
                        init = false;
                    }
                    n  = ((int) event.values[0]) - bach;
                    count.setText(String.valueOf(n));
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        }

    }
    private  void send_to_server(){
        DistanceTraveled distanceTraveled = new DistanceTraveled(n,"21:35","21:40","2021-06-3");
        if (client.isConnected()){
            try{
                int qos =0; // solo el 0 por que solo se quiere enviar
                client.publish(MqttConectionShiftio.STEPS, distanceTraveled.toString().getBytes(),qos,false);
            }catch (Exception e){

            }
        }else{
            Toast.makeText(getBaseContext(), "No esta conectado al servidor",Toast.LENGTH_SHORT).show();
        }
    }
    private void connect(){
        client = new MqttAndroidClient(this.getApplicationContext(),MqttConectionShiftio.MQTTHOST,MqttConectionShiftio.ID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(MqttConectionShiftio.USERNAME);
        options.setPassword(MqttConectionShiftio.PASSWORD.toCharArray());
        try{
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getBaseContext(), "Conectado",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getBaseContext(), "No Conectado",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){

        }
    }
}