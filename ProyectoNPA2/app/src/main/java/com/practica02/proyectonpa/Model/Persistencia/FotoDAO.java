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
import com.practica02.proyectonpa.Model.Entidades.Firebase.Paciente;
import com.practica02.proyectonpa.Model.Entidades.Logica.LFoto;
import com.practica02.proyectonpa.Model.Entidades.Logica.LPaciente;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FotoDAO {

    //Interfaz para devolver un objeto Foto
    public interface IDevolverFoto{
        public void devolverFoto(LFoto lFoto);
        public void devolverError(String error);
    }

    //Interfaz para devolver la URL de la Foto
    public interface IDevolverURLFoto{
        public void devolverUrlString(String url);
    }

    private static FotoDAO fotoDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceFotosUbicacionDB;
    private StorageReference referenceFotoDeUbicacionStorage;

    //Patron singleton para instanciar una vez la clase FotoDAO
    public static FotoDAO getInstancia(){
        if(fotoDAO==null) fotoDAO = new FotoDAO();
        return fotoDAO;
    }

    //Constructor donde se obtiene las referencias de los nodos de la Foto de la BD y del Storage de Firebase
    private FotoDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceFotosUbicacionDB = database.getReference(Constantes.NODO_DE_FOTOS_UBICACION);
        referenceFotoDeUbicacionStorage = storage.getReference("Fotos/Ubicacion/" + UsuarioDAO.getInstancia().getKeyUsuario());
    }

    //Metodo para obtener los datos de un foto almacenados en la BD a travez de una KEY
    public void obtenerInformacionDeFotoPorLlave(final String key, final FotoDAO.IDevolverFoto iDevolverFoto){
        //Se navega a travez de los nodos de la BD para llegar al nodo que deseamos a travez de la clave
        referenceFotosUbicacionDB.child(UsuarioDAO.getInstancia().getKeyUsuario()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Se transforma los datos de la BD a un objeto Foto
                Foto foto = dataSnapshot.getValue(Foto.class);
                //Se almacena en un objeto LFoto que tiene la clave y los datos de la foto
                LFoto lFoto = new LFoto(key,foto);
                //Metodo de la interfaz que se creo para devolver un objeto LFoto
                iDevolverFoto.devolverFoto(lFoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                iDevolverFoto.devolverError(databaseError.getMessage());
            }
        });  //ejecuta una sola vez, no tiene listener por si ocurre cambio

    }

    //Metodo para Subir un archivo foto al Storage de Firebase
    public void subirFotoUri(Uri uri, final IDevolverURLFoto iDevolverURLFoto){
        //Se obtiene la fecha actual que sera el nombre del archivo foto que se almacenara en el storage de Firebase
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreFoto = simpleDateFormat.format(date);

        //Moverse al nodo Foto
        final StorageReference fotoReferencia = referenceFotoDeUbicacionStorage.child(nombreFoto);

        //Metodo de FireBase para guardar un archivo en el storage
        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException(); //throw -> llama a la excepcion
                }
                return fotoReferencia.getDownloadUrl(); //Si se subio la foto a la BD, agarra la url del archivo
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
