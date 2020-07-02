package com.practica02.proyectonpa.Model;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.practica02.proyectonpa.Controller.ContadorPasos;
import com.practica02.proyectonpa.R;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    long contador;
    private Context context;
    public FingerprintHandler(Context context){
        this.context = context;
    }
    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("Error de autenticación. " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Falló la autenticación. ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Accediendo a la aplicación. Redireccionando en ", true);
      /*Intent intent = new Intent(context,activity2.class);
        context.startActivity(intent);*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, ContadorPasos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        }, 5000);
    }

    private void update(String s, boolean b) {
        TextView paraLabel = (TextView) ((Activity)context).findViewById(R.id.paraLabel);
        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.fingerprintImage);
        final TextView counterRedireccionar = (TextView) ((Activity)context).findViewById(R.id.counterRedireccionar);
        new CountDownTimer(5000,1000){
            @Override
            public void onTick(long l) {
                contador=l/1000;
                //  Toast.makeText(context,""+contador,Toast.LENGTH_LONG).show();
                counterRedireccionar.setText(String.valueOf(contador) + " seconds");
                counterRedireccionar.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
            @Override
            public void onFinish() {
            }
        }.start();
        paraLabel.setText(s);
        if(b == false){
            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.action_done);
        }
    }
}
