package com.practica02.proyectonpa.Model.Persistencia;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Foto;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Usuario;
import com.practica02.proyectonpa.Model.Entidades.Logica.LFoto;
import com.practica02.proyectonpa.Model.Entidades.Logica.LUsuario;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsuarioDAO { //acceso de la app a la bd

    //Interfaz para devolver un objeto Usuario
    public interface IDevolverUsuario {
        public void devolverUsuario(LUsuario lUsuario);

        public void devolverError(String error);
    }

    //Interfaz para devolver la URL de la Foto
    public interface IDevolverURLFoto {
        public void devolverUrlString(String url);
    }

    private static UsuarioDAO usuarioDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceUsuarios;
    private StorageReference referenceFotoDePerfil;

    //Patron singleton para instanciar una vez la clase UsuarioDAO
    public static UsuarioDAO getInstancia() {
        if (usuarioDAO == null) usuarioDAO = new UsuarioDAO();
        return usuarioDAO;
    }

    //Constructor donde se obtiene las referencias de los nodos del Usuario de la BD y del Storage de Firebase
    private UsuarioDAO() {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceUsuarios = database.getReference(Constantes.NODO_DE_USUARIOS);
        referenceFotoDePerfil = storage.getReference("Fotos/FotoPerfil/" + getKeyUsuario());
    }

    //Metodo para obtener la Key del usuario Logueado
    public String getKeyUsuario() {
        return FirebaseAuth.getInstance().getUid();
    }

    //Metodo para saber si el usuario esta logueado
    public boolean isUsuarioLogeado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser != null;
    }

    //Metodo que devuelve un long con la fecha de creacion del usuario
    public long fechaDeCreacionLong() {
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp();
    }

    //Metodo que devuelve un long con la fecha del ultimo logueo del usuario
    public long fechaDeUltimaVezQueSeLogeoLong() {
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
    }

    //Metodo para obtener los datos de un Usuario almacenados en la BD a travez de una KEY
    public void obtenerInformacionDeUsuarioPorLlave(final String key, final IDevolverUsuario iDevolverUsuario) {
        //Se navega a travez de los nodos de la BD para llegar al nodo que deseamos a travez de la clave
        referenceUsuarios.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Se transforma los datos de la BD a un objeto Usuario
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                //Se almacena en un objeto LUsuario que tiene la clave y los datos del usuario
                LUsuario lUsuario = new LUsuario(key, usuario);
                //Metodo de la interfaz que se creo para devolver un objeto LUsuario
                iDevolverUsuario.devolverUsuario(lUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iDevolverUsuario.devolverError(databaseError.getMessage());
            }
        });  //ejecuta una sola vez, no tiene listener por si ocurre cambio

    }

    //Metodo para Subir una foto por defecto a todos los usuarios registrados en la BD
    public void añadirFotoDePerfilALosUsuariosQueNoTienenFoto() {
        referenceUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LUsuario> lUsuariosLista = new ArrayList<>();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = childDataSnapshot.getValue(Usuario.class);
                    LUsuario lUsuario = new LUsuario(childDataSnapshot.getKey(), usuario);
                    lUsuariosLista.add(lUsuario);
                }

                for (LUsuario lUsuario : lUsuariosLista) {
                    if (lUsuario.getUsuario().getFotoPerfilURL() == null) {
                        referenceUsuarios.child(lUsuario.getKey()).child("fotoPerfilURL").setValue(Constantes.URL_FOTO_POR_DEFECTO_USUARIOS);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); //Trae a todos los usuarios solo una vez.
    }

    //Metodo para Subir un archivo foto al Storage de Firebase
    public void subirFotoUri(Uri uri, final IDevolverURLFoto iDevolverURLFoto){   //Uri -> foto que se elige con el celular
        //Se obtiene la fecha actual que sera el nombre del archivo foto que se almacenara en el storage de Firebase
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreFoto = simpleDateFormat.format(date);

        //Moverse al nodo Foto
        final StorageReference fotoReferencia = referenceFotoDePerfil.child(nombreFoto);

        //Metodo de FireBase para guardar un archivo en el storage
        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException(); //throw -> llama a la excepcion
                }
                return fotoReferencia.getDownloadUrl(); //Si se eligio una foto y se subio a la BD, agarra la url del archivo
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {  //este metodo captura la url de la foto
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult(); //url de la foto que se sube a la BS
                    iDevolverURLFoto.devolverUrlString(uri.toString()); //Metodo de la interfaz que se creo para devolver la URL de la foto
                }
            }
        });
    }
}
