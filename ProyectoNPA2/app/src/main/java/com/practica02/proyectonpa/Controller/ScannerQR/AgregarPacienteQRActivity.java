package com.practica02.proyectonpa.Controller.ScannerQR;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Paciente;
import com.practica02.proyectonpa.Model.Entidades.Logica.LPaciente;
import com.practica02.proyectonpa.Model.Persistencia.PacienteDAO;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;
import com.practica02.proyectonpa.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class AgregarPacienteQRActivity extends AppCompatActivity {

    private CircleImageView fotoPaciente;
    private ImageView fotoQR;

    private EditText txtNombre;
    private EditText txtAltura;
    private EditText txtPeso;
    private EditText txtPresion;
    private EditText txtFechaDeNacimiento;

    //private RadioGroup rgGenero;
    private RadioButton rdHombre;
    private RadioButton rdMujer;

    private Button btnGuardar;
    private Button btnGenerar;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;

    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;

    private String pickerPath;

    private Uri fotoPacienteUri;
    private Uri fotoCodigoUri;
    private long fechaDeNacimiento;

    private String TAG = "GeneradorQRCode";
    private String inputValue;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_paciente_q_r);

        fotoPaciente = findViewById(R.id.fotoPacienteRegistro);
        fotoQR = findViewById(R.id.fotoQrcodeRegistro);
        txtNombre = findViewById(R.id.txtPacienteNombre);
        txtFechaDeNacimiento = findViewById(R.id.txtPacienteFechaDeNacimiento);
        txtAltura = findViewById(R.id.txtPacienteAltura);
        txtPeso = findViewById(R.id.txtPacientePeso);
        txtPresion = findViewById(R.id.txtPacientePresion);

        rdHombre = findViewById(R.id.rdPacienteHombre);
        rdMujer = findViewById(R.id.rdPacienteMujer);

        btnGuardar = (Button) findViewById(R.id.btnPacienteGuardar);
        btnGenerar = findViewById(R.id.btnPacienteGenerar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        imagePicker = new ImagePicker(this);
        cameraPicker = new CameraImagePicker(this);

        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);

        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                if(!list.isEmpty()){
                    String path = list.get(0).getOriginalPath();
                    fotoPacienteUri = Uri.parse(path);
                    fotoPaciente.setImageURI(fotoPacienteUri);
                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(AgregarPacienteQRActivity.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        cameraPicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                String path = list.get(0).getOriginalPath();
                fotoPacienteUri = Uri.fromFile(new File(path));
                fotoPaciente.setImageURI(fotoPacienteUri);
            }

            @Override
            public void onError(String s) {
                Toast.makeText(AgregarPacienteQRActivity.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        fotoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AgregarPacienteQRActivity.this);
                dialog.setTitle("Foto de paciente");

                String[] items = {"Galeria", "Camara"};

                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                //Hizo click en la galeria
                                imagePicker.pickImage();
                                break;
                            case 1:
                                //Hizo click en la camara
                                pickerPath = cameraPicker.pickImage();
                                break;
                        }
                    }
                });

                AlertDialog dialogContruido = dialog.create();
                dialogContruido.show();
            }
        });

        txtFechaDeNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarPacienteQRActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int mes, int dia) {
                        Calendar calendarResultado = Calendar.getInstance();
                        calendarResultado.set(Calendar.YEAR, year);
                        calendarResultado.set(Calendar.MONTH, mes);
                        calendarResultado.set(Calendar.DAY_OF_MONTH, dia);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = calendarResultado.getTime();
                        String fechaDeNacimientoTexto = simpleDateFormat.format(date);
                        fechaDeNacimiento = date.getTime();
                        txtFechaDeNacimiento.setText(fechaDeNacimientoTexto);
                    }
                },calendar.get(Calendar.YEAR)-17,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombre = txtNombre.getText().toString();
                final String altura = txtAltura.getText().toString();
                final String peso = txtAltura.getText().toString();
                final String presion = txtAltura.getText().toString();
                if(validarString(nombre) && validarString(altura) && validarString(peso) && validarString(presion)) {
                    final String genero;
                    if(rdHombre.isChecked()){
                        genero = "Hombre";
                    } else {
                        genero = "Mujer";
                    }
                    Paciente paciente = new Paciente();
                    paciente.setNombre(nombre);
                    paciente.setFechaDeNacimiento(fechaDeNacimiento);
                    paciente.setGenero(genero);
                    paciente.setAltura(Float.parseFloat(altura));
                    paciente.setPeso(Float.parseFloat(peso));
                    paciente.setPresion(Float.parseFloat(presion));

                    inputValue = LPaciente.getReporte(paciente);
                    if(inputValue.length()>0){
                        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                        Display display = manager.getDefaultDisplay();
                        Point point = new Point();
                        display.getSize(point);
                        int width = point.x;
                        int height = point.y;
                        int smallerdimesion = width < height ? width : height;
                        smallerdimesion = smallerdimesion*3/4;
                        qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerdimesion);
                        try {
                            bitmap = qrgEncoder.encodeAsBitmap();
                            fotoQR.setImageBitmap(bitmap);
                            fotoCodigoUri = getImageUri(AgregarPacienteQRActivity.this,bitmap);

                        }catch (WriterException e){
                            Log.v(TAG, e.toString());
                        }
                        btnGuardar.setEnabled(true);
                    }else{
                        txtNombre.setError("Required");
                        txtFechaDeNacimiento.setError("Required");
                        txtPeso.setError("Required");
                        txtAltura.setError("Required");
                        txtPresion.setError("Required");
                    }

                }
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombre = txtNombre.getText().toString();
                final String altura = txtAltura.getText().toString();
                final String peso = txtAltura.getText().toString();
                final String presion = txtAltura.getText().toString();
                if(validarString(nombre) && validarString(altura) && validarString(peso) && validarString(presion)) {
                     final String genero;
                     if(rdHombre.isChecked()){
                         genero = "Hombre";
                     } else {
                         genero = "Mujer";
                     }

                     if(fotoPacienteUri!=null && fotoCodigoUri !=null) {
                         //Si selecciono una foto

                         PacienteDAO.getInstancia().subirFotoAndQRUri(fotoPacienteUri, fotoCodigoUri, new PacienteDAO.IDevolverURLFoto() {
                             @Override
                             public void devolverUrlString(String url1, String url2) {
                                 Toast.makeText(AgregarPacienteQRActivity.this, "Se registro correctamente", Toast.LENGTH_SHORT).show();
                                 Paciente paciente = new Paciente();
                                 paciente.setNombre(nombre);
                                 paciente.setFechaDeNacimiento(fechaDeNacimiento);
                                 paciente.setGenero(genero);
                                 paciente.setAltura(Float.parseFloat(altura));
                                 paciente.setPeso(Float.parseFloat(peso));
                                 paciente.setPresion(Float.parseFloat(presion));
                                 paciente.setFotoPerfilURL(url1);
                                 paciente.setFotoCodigoQR(url2);

                                 String nombrekey = "";
                                 Date date = new Date();
                                 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
                                 nombrekey = simpleDateFormat.format(date);

                                 FirebaseUser currentUser = mAuth.getCurrentUser(); //esto funciona cuando esta registrado correctamente
                                 DatabaseReference reference = database.getReference("PacientesQR/" + currentUser.getUid()+"/"+ nombrekey); //guarda el mismo uid del usuario en la database
                                 reference.setValue(paciente);
                                 finish();
                             }
                         });

                     }
                } else {
                    Toast.makeText(AgregarPacienteQRActivity.this,"Validaciones funcionando.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Glide.with(this).load(Constantes.URL_FOTO_POR_DEFECTO_USUARIOS).into(fotoPaciente); //Automaticamente se va a cargar una foto de perfil por defecto

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //data imagen que vamos a seleccionar
        if(requestCode == Picker.PICK_IMAGE_DEVICE && resultCode == RESULT_OK){  //Si no cancelo al seleccionar una foto
            imagePicker.submit(data);
        }else if(requestCode == Picker.PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
            cameraPicker.reinitialize(pickerPath);
            cameraPicker.submit(data);
        }

    }

    public boolean validarString(String string){
        return !string.isEmpty();
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}