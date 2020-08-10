package com.practica02.proyectonpa.Controller.ScannerQR;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.practica02.proyectonpa.Model.Entidades.Logica.LPaciente;
import com.practica02.proyectonpa.Model.Persistencia.PacienteDAO;
import com.practica02.proyectonpa.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPacienteActivity extends AppCompatActivity {

    private String KEY_RECEPTOR;
    private CircleImageView fotoPaciente;
    private ImageView fotoCodigoQR;
    private TextView tvNombre, tvFechaNacimiento, tvGenero, tvAltura, tvPeso, tvPresion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scannerqr_perfil_paciente);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            KEY_RECEPTOR = bundle.getString("key_receptor");
        }else{
            finish();
        }

        tvNombre = findViewById(R.id.txtPacienteNombrePerfil);
        tvFechaNacimiento = findViewById(R.id.txtPacienteFechaDeNacimientoPerfil);
        tvGenero = findViewById(R.id.txtPacienteGeneroPerfil);
        tvAltura = findViewById(R.id.txtPacienteAlturaPerfil);
        tvPeso = findViewById(R.id.txtPacientePesoPerfil);
        tvPresion = findViewById(R.id.txtPacientePresionPerfil);

        fotoCodigoQR = findViewById(R.id.fotoQrcodePerfil);
        fotoPaciente = findViewById(R.id.fotoPacientePerfil);

        PacienteDAO.getInstancia().obtenerInformacionDePacientePorLlave(KEY_RECEPTOR, new PacienteDAO.IDevolverPaciente() {
            @Override
            public void devolverPaciente(LPaciente lPaciente) {
                String urlFoto = lPaciente.getPaciente().getFotoPerfilURL();
                loadImageFromURL(urlFoto);
                String urlCodigoQR = lPaciente.getPaciente().getFotoCodigoQR();
                loadImageFromURLQR(urlCodigoQR);
                tvNombre.setText(lPaciente.getPaciente().getNombre());
                tvFechaNacimiento.setText(LPaciente.obtenerFechaDeNacimiento(lPaciente.getPaciente().getFechaDeNacimiento()));
                tvGenero.setText(lPaciente.getPaciente().getGenero());
                tvAltura.setText(""+lPaciente.getPaciente().getAltura());
                tvPeso.setText(""+lPaciente.getPaciente().getPeso());
                tvPresion.setText(""+lPaciente.getPaciente().getPresion());
            }

            @Override
            public void devolverError(String error) {

            }
        });

    }

    private void loadImageFromURL(String url){
        Picasso.with(this).load(url).into(fotoPaciente,new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError() {
            }
        });
    }

    private void loadImageFromURLQR(String url){
        Picasso.with(this).load(url).into(fotoCodigoQR,new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError() {
            }
        });
    }

}