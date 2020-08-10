package com.practica02.proyectonpa.Controller.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.practica02.proyectonpa.Model.Entidades.Logica.LUsuario;
import com.practica02.proyectonpa.Model.Persistencia.UsuarioDAO;
import com.practica02.proyectonpa.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class VerInfoUsuario extends AppCompatActivity {

    private CircleImageView imgPerfil;
    private ImageView imgPerfil2;
    private TextView txtNombre;
    private TextView txtCorreo;
    //private TextView txtContraseña;
    private TextView txtFechaDeNacimiento;
    private TextView txtGenero;

    private LUsuario usuarioTemporal;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_ver_info_usuario);

        imgPerfil = findViewById(R.id.fotoPerfilPerfil);
        txtNombre = findViewById(R.id.idPerfilNombre);
        txtCorreo = findViewById(R.id.idPerfilCorreo);
        //txtContraseña = findViewById(R.id.idPerfilContraseña);
        txtFechaDeNacimiento = findViewById(R.id.txtPerfilFechaDeNacimiento);
        txtGenero = findViewById(R.id.txtPerfilGenero);

        mAuth = FirebaseAuth.getInstance();

        UsuarioDAO.getInstancia().obtenerInformacionDeUsuarioPorLlave(UsuarioDAO.getInstancia().getKeyUsuario(), new UsuarioDAO.IDevolverUsuario() {
            @Override
            public void devolverUsuario(LUsuario lUsuario) {
                String url = lUsuario.getUsuario().getFotoPerfilURL();
                loadImageFromURL(url);
                txtNombre.setText(lUsuario.getUsuario().getNombre());
                txtCorreo.setText(lUsuario.getUsuario().getCorreo());
                txtFechaDeNacimiento.setText(LUsuario.obtenerFechaDeNacimiento(lUsuario.getUsuario().getFechaDeNacimiento()));
                txtGenero.setText(lUsuario.getUsuario().getGenero());
            }
            @Override
            public void devolverError(String error) {
            }
        });

    }

    private void loadImageFromURL(String url){
        Picasso.with(this).load(url).into(imgPerfil,new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError() {
            }
        });
    }

}