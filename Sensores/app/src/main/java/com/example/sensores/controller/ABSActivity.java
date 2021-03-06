package com.example.sensores.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensores.MainActivity;
import com.example.sensores.R;
import com.example.sensores.model.ABS;
import com.example.sensores.utilities.LowPass;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.android.service.MqttAndroidClient;

public class ABSActivity extends AppCompatActivity {
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
    private boolean down;
    private MqttAndroidClient client;
    private LowPass lowPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_b_s);
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
        down = false;
        lowPass = new LowPass();

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
        instructions.setMessage("Para empezar los ejercicios deber??s colocar tu celular pantalla arriba en el suelo, luego presionar el bot??n Empezar y continuar haciendo Planchas sobre el celular que registrar?? cada vez que llegues cerca del suelo usando el sensor de proximidad del dispositivo.").show();
    }
    private void  question_to_send_to_server(){
        builder_question.setTitle("Guardar");
        builder_question.setMessage("Has realizado : "+ n+" planchas, Deseas guardarlas?")
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
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (sensor == null){
            Toast.makeText(getApplicationContext(),"No hay sensor de proximidad",Toast.LENGTH_LONG).show();
        }else{
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    double value = lowPass.add_pass(event.values[0]);
                    if(value<sensor.getMaximumRange()){
                        if (!down){
                            n = n + 1;
                            down = true;
                            count.setText(n+"");
                        }
                    }else{
                        down = false;
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        }

    }
    private  void send_to_server(){
        ABS abs = new ABS(n,"21:35","21:40","2021-06-3");
        if (client.isConnected()){
            try{
                int qos =0;
                client.publish(MqttConectionShiftio.ABS, abs.toString().getBytes(),qos,false);
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