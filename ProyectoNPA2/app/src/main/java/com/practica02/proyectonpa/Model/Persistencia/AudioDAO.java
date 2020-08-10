package com.practica02.proyectonpa.Model.Persistencia;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioDAO {

    //Interfaz para devolver la URL del Audio
    public interface IDevolverURLAudio{
        public void devolverUrlString(String url);
    }

    private static AudioDAO audioDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceAudio;
    private StorageReference referenceAudioStorage;

    //Patron singleton para instanciar una vez la clase AudioDAO
    public static AudioDAO getInstancia(){
        if(audioDAO==null) audioDAO = new AudioDAO();
        return audioDAO;
    }

    //Constructor donde se obtiene las referencias de los nodos del audio de la BD y del Storage de Firebase
    private AudioDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceAudio = database.getReference(Constantes.NODO_DE_AUDIO_UBICACION);
        referenceAudioStorage = storage.getReference("Audio/" + UsuarioDAO.getInstancia().getKeyUsuario());
    }

    //Metodo para Subir un archivo audio al Storage de Firebase
    public void subirAudioUri(Uri uri, final IDevolverURLAudio iDevolverURLAudio){
        //Se obtiene la fecha actual que sera el nombre del archivo audio que se almacenara en el storage de Firebase
        String nombreAudio = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreAudio = simpleDateFormat.format(date);

        //Moverse al nodo Audio
        final StorageReference audioReferencia = referenceAudioStorage.child(nombreAudio);

        //Metodo de FireBase para guardar un archivo en el storage
        audioReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException(); //throw -> llama a la excepcion
                }
                return audioReferencia.getDownloadUrl(); //Si se subio el audio a la BD, se obtiene la url del archivo
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {  //este metodo captura la url del audio
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult(); //url del audio que se subio a la BD
                    iDevolverURLAudio.devolverUrlString(uri.toString());    //Metodo de la interfaz que se creo para devolver la URL del audio
                }
            }
        });
    }

}
