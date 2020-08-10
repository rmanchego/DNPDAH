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
import com.practica02.proyectonpa.Model.Entidades.Firebase.Paciente;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Usuario;
import com.practica02.proyectonpa.Model.Entidades.Logica.LPaciente;
import com.practica02.proyectonpa.Model.Entidades.Logica.LUsuario;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PacienteDAO { //acceso de la app a la bd

    //Interfaz para devolver un objeto LPaciente
    public interface IDevolverPaciente{
        public void devolverPaciente(LPaciente lPaciente);
        public void devolverError(String error);
    }

    //Interfaz para devolver la URL de la foto del Paciente
    public interface IDevolverURLFoto{
        public void devolverUrlString(String url1, String url2);
    }

    private static PacienteDAO pacienteDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referencePaciente;
    private StorageReference referenceFotoDePaciente;
    private StorageReference referenceFotoQR;

    //Patron singleton para instanciar una vez la clase PacienteDAO
    public static PacienteDAO getInstancia(){
        if(pacienteDAO==null) pacienteDAO = new PacienteDAO();
        return pacienteDAO;
    }

    //Constructor donde se obtiene las referencias de los nodos del paciente de la BD y del Storage de Firebase
    private PacienteDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referencePaciente = database.getReference(Constantes.NODO_DE_PACIENTES_QR);
        referenceFotoDePaciente = storage.getReference("Fotos/FotoPaciente/" + getKeyUsuario());
        referenceFotoQR = storage.getReference("Fotos/FotoQR/" + getKeyUsuario());
    }

    //Metodo para obtener la Key del usuario Logueado
    public String getKeyUsuario(){
        return FirebaseAuth.getInstance().getUid();
    }

    //Metodo para obtener los datos del paciente almacenados en la BD a travez de una KEY
    public void obtenerInformacionDePacientePorLlave(final String key, final IDevolverPaciente iDevolverPaciente){
        //Se navega a travez de los nodos de la BD para llegar al nodo que deseamos a travez de la clave
        referencePaciente.child(UsuarioDAO.getInstancia().getKeyUsuario()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Se transforma los datos de la BD a un objeto Paciente
                Paciente paciente = dataSnapshot.getValue(Paciente.class);
                //Se almacena en un objeto LPaciente que tiene la clave y los datos del paciente
                LPaciente lPaciente = new LPaciente(key,paciente);
                //Metodo de la interfaz que se creo para devolver un objeto LPaciente
                iDevolverPaciente.devolverPaciente(lPaciente);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                iDevolverPaciente.devolverError(databaseError.getMessage());
            }
        });  //ejecuta una sola vez, no tiene listener por si ocurre cambio

    }

    //Metodo para Subir un archivo foto al Storage de Firebase
    public void subirFotoAndQRUri(Uri uri1, final Uri uri2, final IDevolverURLFoto iDevolverURLFoto){   //Uri -> foto que se elige con el celular
        //Se obtiene la fecha actual que sera el nombre del archivo foto que se almacenara en el storage de Firebase
        String nombreFoto = "";
        String nombreFotoQR = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreFoto = simpleDateFormat.format(date);
        nombreFotoQR = simpleDateFormat.format(date);
        final String finalNombreFoto = nombreFoto;

        //Moverse al nodo Foto del paciente
        final StorageReference fotoReferencia = referenceFotoDePaciente.child(nombreFoto);

        //Metodo de FireBase para guardar un archivo en el storage
        fotoReferencia.putFile(uri1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    final Uri uri1 = task.getResult(); //Uri de la foto que se sube a la BS

                    //Se repite el proceso para subir la foto del QR
                    final StorageReference fotoReferencia2 = referenceFotoQR.child(finalNombreFoto);
                    fotoReferencia2.putFile(uri2).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return fotoReferencia2.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri uri2 = task.getResult();
                            //Metodo de la interfaz que se creo para devolver la URL del foto del paciente y el codigo QR
                            iDevolverURLFoto.devolverUrlString(uri1.toString(),uri2.toString());
                        }
                    });
                }
            }
        });
    }
}
