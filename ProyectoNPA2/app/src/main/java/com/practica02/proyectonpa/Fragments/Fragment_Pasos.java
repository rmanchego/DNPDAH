package com.practica02.proyectonpa.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.practica02.proyectonpa.Controller.Pasos.HistorialPasosActivity;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Pasos;
import com.practica02.proyectonpa.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.SENSOR_SERVICE;

public class Fragment_Pasos extends Fragment {
    //Contador de Pasos V 1.0
    private double valoranterior=0;
    private int contarPasos=0;
    private TextView viewPasos;
    private Button btnComenzar, btnDetener, btnReiniciar,btnContinuar, btnGuardar, btnVerHistorial;
    private Chronometer cronometro;
    private boolean caminando = false;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    long detenerse;

    private GifImageView personacaminando;

    private GifDrawable gifDrawable;
    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_pasos_layout,container,false);

        viewPasos = v.findViewById(R.id.id_viewpasosFrag);
        cronometro = v.findViewById(R.id.cronometro);
        btnComenzar = v.findViewById(R.id.btnComenzarFrag);
        btnDetener = v.findViewById(R.id.btnDetenerFrag);
        btnReiniciar = v.findViewById(R.id.btnReiniciarFrag);
        btnContinuar = v.findViewById(R.id.btnContinuarFrag);
        btnGuardar = v.findViewById(R.id.btnGuardarFrag);
        btnVerHistorial = v.findViewById(R.id.btnVerHistorialFrag);

        personacaminando = v.findViewById(R.id.personaCaminando);


        gifDrawable = (GifDrawable) personacaminando.getDrawable();

        btnEnabled(btnDetener,false);
        btnEnabled(btnReiniciar,false);
        btnEnabled(btnContinuar,false);
        btnEnabled(btnGuardar,false);
        gifDrawable.stop();



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        final Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifDrawable.start();
                startCronometro();
                caminando = true;
                contarPasos = 0;
                btnEnabled(btnComenzar, false);
                btnEnabled(btnContinuar, false);
                btnEnabled(btnDetener, true);
                btnEnabled(btnReiniciar, true);
                btnEnabled(btnGuardar, false);

            }
        });
        btnDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCronometro();
                caminando = false;
                gifDrawable.stop();
                btnEnabled(btnContinuar, true);
                btnEnabled(btnReiniciar, true);
                btnEnabled(btnGuardar, true);

            }
        });

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarCronometro();
                caminando = false;
                contarPasos = 0;
                viewPasos.setText("0");
                gifDrawable.stop();
                btnEnabled(btnComenzar, true);
                btnEnabled(btnContinuar, false);
                btnEnabled(btnDetener, false);
                btnEnabled(btnReiniciar, false);
                btnEnabled(btnGuardar, false);

            }
        });
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCronometro();
                caminando = true;
                gifDrawable.start();
                btnEnabled(btnComenzar, false);
                btnEnabled(btnContinuar, false);
                btnEnabled(btnDetener, true);
                btnEnabled(btnReiniciar, true);
                btnEnabled(btnGuardar, false);

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caminando = false;

                btnComenzar.setEnabled(false);
                btnContinuar.setEnabled(true);
                btnDetener.setEnabled(true);
                btnReiniciar.setEnabled(true);
                btnGuardar.setEnabled(true);

                String nombrePasos = "";
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
                nombrePasos = simpleDateFormat.format(date);

                Calendar calendarResultado = Calendar.getInstance();
                Date date2 = calendarResultado.getTime();

                Pasos pasos = new Pasos();
                pasos.setPasos(contarPasos);
                pasos.setFecha(date2.getTime());
                long time = (SystemClock.elapsedRealtime()-cronometro.getBase());
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                pasos.setDuracion(t);

                FirebaseUser currentUser = mAuth.getCurrentUser(); //esto funciona cuando esta registrado correctamente
                DatabaseReference reference = database.getReference("ContadorPasos/" + currentUser.getUid()+"/"+nombrePasos); //guarda el mismo uid del usuario en la database
                reference.setValue(pasos);

                Toast.makeText(getActivity(), "Se guardo el dato", Toast.LENGTH_SHORT).show();

            }
        });

        btnVerHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HistorialPasosActivity.class));
            }
        });

        SensorEventListener contadorPasos = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (caminando){
                    if (event != null) {
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        double valor = Math.sqrt(x * x + y * y + z * z);
                        double val2 = valor - valoranterior;
                        valoranterior = valor;

                        if (val2 > 6) {
                            contarPasos++;
                        }
                        viewPasos.setText(String.valueOf(contarPasos));

                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(contadorPasos,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        return v;
    }

    public void startCronometro(){
        if(!caminando){
            cronometro.setBase(SystemClock.elapsedRealtime() - detenerse);
            cronometro.start();
        }
    }

    public void stopCronometro(){
        if (caminando){
            cronometro.stop();
            detenerse = SystemClock.elapsedRealtime() - cronometro.getBase();
        }
    }

    public void reiniciarCronometro(){
        cronometro.setBase(SystemClock.elapsedRealtime());
        detenerse = 0;
        cronometro.stop();
    }

    public void btnEnabled(Button btn, boolean state){
        if(state == false) {
            btn.setEnabled(false);
            btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            btn.setEnabled(true);
            btn.getBackground().setColorFilter(null);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", contarPasos);
        editor.apply();
    }

        @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", contarPasos);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        contarPasos = sharedPreferences.getInt("stepCount",0);
    }
}
