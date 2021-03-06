package com.practica02.proyectonpa.Controller.FotosUbicacion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.practica02.proyectonpa.Model.Entidades.Logica.LFoto;
import com.practica02.proyectonpa.Model.Persistencia.FotoDAO;
import com.practica02.proyectonpa.R;
import com.squareup.picasso.Picasso;

public class VerFotoDeHistorialActivity extends AppCompatActivity {

    private String KEY_RECEPTOR;
    private ImageView fotoLugar;
    private TextView tvFecha;
    private TextView tvUbicacion;
    private TextView tvLatitud;
    private TextView tvLongitud;
    private TextView tvLuz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_ubicacion_ver_foto_de_historial);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            KEY_RECEPTOR = bundle.getString("key_receptor");
        }else{
            finish();
        }

        fotoLugar = findViewById(R.id.fotoHistorial);
        tvFecha= findViewById(R.id.idFechaFotoHistorial);
        tvUbicacion = findViewById(R.id.idUbicacionFotoHistorial);
        tvLatitud = findViewById(R.id.idLatitudFotoHistorial);
        tvLongitud = findViewById(R.id.idLongitudFotoHistorial);
        tvLuz = findViewById(R.id.idvalorLuzFotohistorial);
        FotoDAO.getInstancia().obtenerInformacionDeFotoPorLlave(KEY_RECEPTOR, new FotoDAO.IDevolverFoto() {
            @Override
            public void devolverFoto(LFoto lFoto) {
                String urlFoto = lFoto.getFoto().getFotoFotoURL();
                loadImageFromURL(urlFoto);
                tvFecha.setText(lFoto.getFoto().getNombre());
                tvUbicacion.setText(lFoto.getFoto().getDireccion());
                tvLatitud.setText(lFoto.getFoto().getLatitud());
                tvLongitud.setText(lFoto.getFoto().getLongitud());
                tvLuz.setText(lFoto.getFoto().getValorLuz());
            }

            @Override
            public void devolverError(String error) {

            }
        });
    }

    private void loadImageFromURL(String url){
        Picasso.with(this).load(url).into(fotoLugar,new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError() {
            }
        });
    }

}