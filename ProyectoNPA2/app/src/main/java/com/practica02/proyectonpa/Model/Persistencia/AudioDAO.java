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

    public interface IDevolverURLAudio{
        public void devolverUrlString(String url);
    }

    private static AudioDAO audioDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceAudio;
    private StorageReference referenceAudioStorage;

    public static AudioDAO getInstancia(){
        if(audioDAO==null) audioDAO = new AudioDAO();
        return audioDAO;
    }

    private AudioDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceAudio = database.getReference(Constantes.NODO_DE_AUDIO_UBICACION);
        referenceAudioStorage = storage.getReference("Audio/" + UsuarioDAO.getInstancia().getKeyUsuario());
    }

    public void subirAudioUri(Uri uri, final IDevolverURLAudio iDevolverURLAudio){   //Uri -> foto que se elige con el celular
        String nombreAudio = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreAudio = simpleDateFormat.format(date);
        final StorageReference audioReferencia = referenceAudioStorage.child(nombreAudio);
        //Uri u = taskSnapshot.getDownloadUrl();
        audioReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException(); //throw -> llama a la excepcion
                }
                return audioReferencia.getDownloadUrl(); //Si se eligio una foto y se subio a la BD, agarra la url del archivo
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {  //este metodo captura la url de la foto
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult(); //url de la foto que se sube a la BS
                    iDevolverURLAudio.devolverUrlString(uri.toString());
                }
            }
        });
    }

}
