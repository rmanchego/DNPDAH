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

    public interface IDevolverFoto{
        public void devolverFoto(LFoto lFoto);
        public void devolverError(String error);
    }

    public interface IDevolverURLFoto{
        public void devolverUrlString(String url);
    }

    private static FotoDAO fotoDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceFotosUbicacionDB;
    private StorageReference referenceFotoDeUbicacionStorage;

    public static FotoDAO getInstancia(){
        if(fotoDAO==null) fotoDAO = new FotoDAO();
        return fotoDAO;
    }

    private FotoDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceFotosUbicacionDB = database.getReference(Constantes.NODO_DE_FOTOS_UBICACION);
        referenceFotoDeUbicacionStorage = storage.getReference("Fotos/Ubicacion/" + UsuarioDAO.getInstancia().getKeyUsuario());
    }

    public void obtenerInformacionDeFotoPorLlave(final String key, final FotoDAO.IDevolverFoto iDevolverFoto){
        referenceFotosUbicacionDB.child(UsuarioDAO.getInstancia().getKeyUsuario()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Foto foto = dataSnapshot.getValue(Foto.class);
                LFoto lFoto = new LFoto(key,foto);
                iDevolverFoto.devolverFoto(lFoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                iDevolverFoto.devolverError(databaseError.getMessage());
            }
        });  //ejecuta una sola vez, no tiene listener por si ocurre cambio

    }


    public void subirFotoUri(Uri uri, final IDevolverURLFoto iDevolverURLFoto){   //Uri -> foto que se elige con el celular
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreFoto = simpleDateFormat.format(date);
        final StorageReference fotoReferencia = referenceFotoDeUbicacionStorage.child(nombreFoto);
        //Uri u = taskSnapshot.getDownloadUrl();
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
                    iDevolverURLFoto.devolverUrlString(uri.toString());
                }
            }
        });
    }




}
