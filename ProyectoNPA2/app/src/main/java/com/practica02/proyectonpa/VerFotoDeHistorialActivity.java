package com.practica02.proyectonpa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.practica02.proyectonpa.Model.Entidades.Logica.LFoto;
import com.practica02.proyectonpa.Model.Persistencia.FotoDAO;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerFotoDeHistorialActivity extends AppCompatActivity {

    private String KEY_RECEPTOR;
    private ImageView fotoLugar;
    private TextView tvFecha;
    private TextView tvUbicacion;
    private TextView tvLatitud;
    private TextView tvLongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_foto_de_historial);

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

        FotoDAO.getInstancia().obtenerInformacionDeFotoPorLlave(KEY_RECEPTOR, new FotoDAO.IDevolverFoto() {
            @Override
            public void devolverFoto(LFoto lFoto) {
                String urlFoto = lFoto.getFoto().getFotoPerfilURL();
                loadImageFromURL(urlFoto);
                tvFecha.setText(lFoto.getFoto().getNombre());
                tvUbicacion.setText(lFoto.getFoto().getDireccion());
                tvLatitud.setText(lFoto.getFoto().getLatitud());
                tvLongitud.setText(lFoto.getFoto().getLongitud());
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