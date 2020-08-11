package com.practica02.proyectonpa.Controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.practica02.proyectonpa.Controller.LoginApp.LoginActivity;
import com.practica02.proyectonpa.Controller.Menu.VerInfoUsuario;
import com.practica02.proyectonpa.Controller.Menu.MapsActivity;
import com.practica02.proyectonpa.Model.AudioRecorder;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Audio;
import com.practica02.proyectonpa.Model.Persistencia.AudioDAO;
import com.practica02.proyectonpa.R;
import com.practica02.proyectonpa.ui.main.SectionsPagerAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Menu menu;
    private View v;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 0;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private AudioRecorder audioRecorder;
    private File recordingDir;
    private TextView txtRuta;
    File root;
    private File workingDir;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permisos();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        toolbar = (Toolbar) findViewById(R.id.titletoolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_superior,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_menu_microfono:
                final AlertDialog.Builder grabar = new AlertDialog.Builder(MainActivity.this);
                grabar.setMessage("Desea realizar una grabación?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_microfono));
                                startRecording(v);
                                // dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog titulo = grabar.create();
                        titulo.setTitle("Grabación");
                        titulo.show();

                return true;

            case R.id.item_menu_stop:
                stopRecording(v);
                menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_microphone_off));

                Toast.makeText(this,"Las grabaciones se encuentran en: " + recordingDir.getPath(),Toast.LENGTH_LONG).show();
                return true;

            case R.id.item_ver_info:
                Intent intent = new Intent(MainActivity.this, VerInfoUsuario.class);
                startActivity(intent);
                return true;

            case R.id.item_ver_mapa:
                final AlertDialog.Builder map = new AlertDialog.Builder(MainActivity.this);
                map.setMessage("Esta a punto de acceder al historial de ubicaciones en el mapa, verá la ubicación de donde se tomaron las fotos")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intents = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(intents);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog mapIr = map.create();
                mapIr.setTitle("Historial de Ubicación en el Mapa");
                mapIr.show();

                return true;

            case R.id.item_logout:
                final AlertDialog.Builder salir = new AlertDialog.Builder(MainActivity.this);
                salir.setMessage("Desea salir del aplicativo?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                returnLogin();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog avisoLogOut = salir.create();
                avisoLogOut.setTitle("LogOut");
                avisoLogOut.show();







                return true;

        }


        return super.onOptionsItemSelected(item);
    }

    private void returnLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    ///////////////////////////////Microfono/////////////////////////////////////////////

    public void permisos(){
        /////////// Permisos para la aplicación///////////
        int permissionWriteToStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWriteToStorage == PackageManager.PERMISSION_DENIED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            setUpWorkingDirectory();
        }
        int permissionAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permissionAudio == PackageManager.PERMISSION_DENIED) {
            requestPermission(Manifest.permission.RECORD_AUDIO, REQUEST_PERMISSION_RECORD_AUDIO);
        } else {
            audioRecorder = new AudioRecorder();
        }
        //////////////////////////////////////////////////
    }

    private void requestPermission(String permission, int permissionCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, permissionCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                setUpWorkingDirectory();
                break;
            case REQUEST_PERMISSION_RECORD_AUDIO:
                if (audioRecorder == null) {
                    audioRecorder = new AudioRecorder();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Verificar los permisos para crear un archivo en el celular, crearlo si se tiene los permisos necesarios
     */
    private void setUpWorkingDirectory() {
        int permissionStorageWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStorageWrite == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Debe permitir guardar archivos en su directorio", Toast.LENGTH_SHORT).show();
            return;
        }
        File externalStorage = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
        root = new File(externalStorage.getAbsolutePath() + "/android_audio_record_stereo");

        if (root.mkdirs()) {
            Log.d(TAG, "Creando la ruta del archivo");
        }
        // Creación del directorio
        Date date = new Date();
        String dia = (String) android.text.format.DateFormat.format("dd", date);
        String mes = (String) android.text.format.DateFormat.format("MM", date);
        recordingDir = new File(root.getAbsolutePath() + "/wav_samples_" + mes + "_" + dia);
        if (recordingDir.mkdirs()) {
            Log.d(TAG, "Directorio creado qeu contiene todos los archivos grabados el día de hoy");
        }




    }

    public void startRecording(View view) {
        // Verificando los permisos para la grabacion
        int permissionAudioRecord = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permissionAudioRecord == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Debe permitir el acceso al micrófono", Toast.LENGTH_SHORT).show();
            return;
        }

        workingDir = new File(recordingDir.getAbsolutePath() + "/sample_" + SystemClock.elapsedRealtime());
        if (workingDir.mkdir()) {
            Log.d(TAG, "Dirección del directorio: " + workingDir.getAbsolutePath());
        }
        audioRecorder.start(workingDir);

    }
    public void stopRecording(View view) {
        int grabaciones = audioRecorder.stop();
    }
///////////////////////////////////// / M    E   T   O   D   O   P  A  R  A  S  U  B  I  R  E  L  A  U  D  I  O /////////////////
    public void saveAudio(File outputFile){

        Uri audioUri = Uri.fromFile(outputFile);

        AudioDAO.getInstancia().subirAudioUri(audioUri, new AudioDAO.IDevolverURLAudio() {
            @Override
            public void devolverUrlString(String url) {
                //   Toast.makeText(MainActivity.this, "Se guardo el audio correctamente", Toast.LENGTH_SHORT).show();
                String nombreAudio = "";
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
                nombreAudio = simpleDateFormat.format(date);
                Audio audio = new Audio();
                audio.setURLAudio(url);
                audio.setNombre(nombreAudio);
                FirebaseUser currentUser = mAuth.getCurrentUser(); //esto funciona cuando esta registrado correctamente
                DatabaseReference reference = database.getReference("Audios/" + currentUser.getUid()+"/"+nombreAudio); //guarda el mismo uid del usuario en la database
                reference.setValue(audio);
            }
        });
    }
///////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecorder.stop();
        audioRecorder.cleanUp();
    }
}