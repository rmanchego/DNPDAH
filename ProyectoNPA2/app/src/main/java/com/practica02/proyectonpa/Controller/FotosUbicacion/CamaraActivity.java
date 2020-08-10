package com.practica02.proyectonpa.Controller.FotosUbicacion;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.practica02.proyectonpa.Model.Sensores.Acelerometro.Acelerometro;
import com.practica02.proyectonpa.Model.Sensores.Acelerometro.ListenerAcelerometro;
import com.practica02.proyectonpa.Model.Sensores.Giroscopio.Giroscopio;
import com.practica02.proyectonpa.Model.Sensores.Giroscopio.ListenerGiroscopio;
import com.practica02.proyectonpa.Model.Sensores.SensorLuz.LightSensor;
import com.practica02.proyectonpa.Model.Sensores.SensorLuz.ListenerLightSensor;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Foto;
import com.practica02.proyectonpa.Model.Persistencia.FotoDAO;
import com.practica02.proyectonpa.R;

public class CamaraActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Path para guardar las fotos
    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";
    //

    private static final String TAG = "MainActivity";
    private static final int CAMERAID = 0;
    private SurfaceView mSurfaceView;
    private CameraDevice mCamera;
    private ImageView capturarImagen;
    private SurfaceHolder mSurfaceHolder;
    private ImageReader mImageReader;
    private CameraManager cameraManager;
    private String cameraId = null;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewCaptureRequest;
    private File outputFile;
    Button btnTakePicture;
    private String latitud;
    private String longitud;
    private String dirección;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    // Objetos para usar el sensor giroscopio y acelerómetor
    private Giroscopio giroscopio;
    private Acelerometro acelerometro;
    private LightSensor sensorLuz;
    private boolean flashEncendido;

    //Objetos para el calculo del movimiento del celular en base al acelerometro
    private float ejexActualX, ejeActualY, ejeActualZ,lastX,lastY,lastZ;
    private float difdeX, difdeY, difdeZ;
    private boolean noesPrimeravez;
    private float limiteDeMovimiento = 0.2f;
    private Vibrator vibrator;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_ubicacion_camara);
        verifyStoragePermissions(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                Log.d(TAG,"foto tomada");
            }
        });
        //checkcameras();

        init();
        latitud = getIntent().getExtras().getString("latitud");
        longitud = getIntent().getExtras().getString("longitud");
        dirección = getIntent().getExtras().getString("direccion");


        //Para reconocer la orientación del dispositivo
        giroscopio = new Giroscopio(this);
        acelerometro = new Acelerometro(this);
        sensorLuz = new LightSensor(this);

    }

    private void init() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view_camera2_activity);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(surfaceHolderCallback);
        mSurfaceHolder.setFixedSize(1080, 2000);

    }

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startCameraCaptureSession();
            Log.d(TAG, "start camera session");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
        }
    };

    private void startCameraCaptureSession() {
        int ancho = 1024;
        int largo = 768;
        mImageReader = ImageReader.newInstance(ancho, largo, ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                try (Image imagen = reader.acquireNextImage()) {
                    Image.Plane[] planes = imagen.getPlanes();
                    if (planes.length > 0) {
                        ByteBuffer buffer = planes[0].getBuffer();
                        byte[] datos = new byte[buffer.capacity()];
                        buffer.get(datos);
                        saveImage(datos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[CAMERAID];

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },100);
                return;
            }
            cameraManager.openCamera(cameraId, cameraDeviceCallback, null);
            Log.e(TAG, "Camara abierta");

            //Método para detectar movimiento con ayuda del acelerómetro para detectar que no se realicen movimientos
            //            //bruscos al momento de tomar una foto

            detectarMovimientoCamara();

            //Agregando el reconocimiento al momento de rotar el telefono
            detectarOrientacion();

            //Agregando el método que permite usar el flash automáticamente en un lugar oscuro al momento de tomar una foto
            detectarIluminosidad();

        } catch (Exception e) {
            Log.e(TAG, "No se puede abrir la cámara", e);
        }
    }

public void detectarMovimientoCamara(){
    acelerometro.setListenerAcelerometro(new ListenerAcelerometro() {
        @Override
        public void ejes(float ejex, float ejey, float ejez) {

            //Se aplica un filtrado de datos del sensor acelerómetro con la finalidad de tener una detección de movimiento
            //más certera. Es similar al filtro de paso alto, toma los valores bruscos (Aquellos que mandan un "pico" o
            //señal alta y luego se le pasa la resta de sus valores abs.

            //Se guardan los datos del sensor en los temporales ejeActual, para luego utilizarlos en la diferencia del
            //último valor menos el primero
            ejexActualX = ejex;
            ejeActualY = ejey;
            ejeActualZ = ejez;

            //noesPrimeravez se encarga de verificar si se está moviendo el celular, TRUE si lo está haciendo
            if(noesPrimeravez){

                //Valores de los ejes filtrados, difde es el valor final, el filtrado, el cual se usará para
                //la comparación entre estos valores y el límite de movimiento establecido en la variable
                //limiteDeMovimiento.
                difdeX = Math.abs(lastX- ejexActualX);
                difdeY = Math.abs(lastY- ejeActualY);
                difdeZ = Math.abs(lastZ- ejeActualZ);

                //Esta condición se encarga de preguntar si el valor filtrado es mayor que el valor establecido como
                //límite, si el valor filtrado sobrepasa este valor establecido, entonces se manda un evento, el cual
                //es Vibrar, con el objetivo de avisarle al usuario para regular su movimiento y tomar una mejor foto.

                if((difdeX > limiteDeMovimiento && difdeY > limiteDeMovimiento) ||
                        (difdeX > limiteDeMovimiento && difdeZ > limiteDeMovimiento)||
                        (difdeY > limiteDeMovimiento && difdeZ > limiteDeMovimiento)){
                    vibrar();
                   // Toast.makeText(CamaraActivity.this,"No mueva mucho la cámara para una mejor foto",Toast.LENGTH_LONG).show();

                }

            }
            lastX = ejexActualX;
            lastY = ejeActualY;
            lastZ = ejeActualZ;
            noesPrimeravez = true;
        }
    });
}


public void detectarOrientacion(){
    // En base a la orientación, si se gira de lado izquierdo se abre un toast y nos manda el texto "Orientación Cambiada"
    // Si se vuelve a girar al lado contrario, se manda el mismo mensaje
    giroscopio.setListenerGiroscopio(new ListenerGiroscopio() {
        @Override
        public void rotando(float ejex, float ejey, float ejez) {
            if(ejez > 0.5f){
                Toast.makeText(CamaraActivity.this,"Orientación cambiada",Toast.LENGTH_LONG).show();
            }else if(ejez <-0.5f){
                Toast.makeText(CamaraActivity.this,"Orientación cambiada",Toast.LENGTH_LONG).show();

            }
        }
    });
}

public void detectarIluminosidad(){
    sensorLuz.setListenerSensorLuz(new ListenerLightSensor() {
        @Override
        public void valorSensorLuz(float valsensorluz) {
            Toast.makeText(getApplicationContext(),"Valor: " + String.valueOf(valsensorluz), Toast.LENGTH_LONG).show();

            if(valsensorluz<5.0){
                // Toast.makeText(getApplicationContext(),"Valorssw", Toast.LENGTH_LONG).show();
                encenderFlash();
            }else{
                apagarFlash();
            }
        }
    });
}

    //Método que nos ayuda a controlar el encendido del flash, se añaden las líneas 294-299 debido a que
    //API Camera 2 necesita de configuraciones extra para poder realizar el manejo del flash, en la línea
    //296 se realiza la configuración de uso del flash por medio de FLASH_MODE, y como es el método para
    //activar el flash, entonces se elige la opción FLASH_MODE_TORCH
    public void encenderFlash(){
        mPreviewCaptureRequest.set(CaptureRequest.FLASH_MODE,CaptureRequest.FLASH_MODE_TORCH);
        try {
            mCaptureSession.setRepeatingRequest(mPreviewCaptureRequest.build(),null,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        sensorLuz.flashLightOn();
        flashEncendido=true;
    }

    //Método que nos ayuda a controlar el apagado del flash, la lógica es la misma que la del método
    //encenderFlash, este método desactiva el flash que se encuentra encendido utilizando la configuración
    //FLASH_MODE_OFF.

    public void apagarFlash(){
        mPreviewCaptureRequest.set(CaptureRequest.FLASH_MODE,CaptureRequest.FLASH_MODE_OFF);
        try {
            mCaptureSession.setRepeatingRequest(mPreviewCaptureRequest.build(),null,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        flashEncendido=false;
    }

    //Método que permite que un dispositivo vibre, en la condición se establece que según la API del dispositivo, en este caso
    //dispositivo con API's mayor a 26, usarán cierta forma de obtención de la vibración, caso contrario se utilizará otra forma
    //Esto se hace con la finalidad de abarcar más dispositivos.
    private void vibrar() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(250,1));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
    private CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCamera = camera;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCamera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCamera = null;
            Log.e(TAG, "Camera Error: " + error);
        }
    };

    private void takePreview() {
        if (mCamera == null || mSurfaceHolder.isCreating()) {
            return;
        }

        try {

            Surface previewSurface = mSurfaceHolder.getSurface();

            mPreviewCaptureRequest = mCamera.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW);

            mPreviewCaptureRequest.addTarget(previewSurface);

            mCamera.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),
                    captureSessionCallback,
                    null);


        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera Access Exception", e);
        }

    }
    //Orientación de la imagen tomada
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }
    private void takePicture() {
        if (mCamera == null) return;
        try {
            CaptureRequest.Builder takePictureBuilder = mCamera.createCaptureRequest(
                    CameraDevice.TEMPLATE_STILL_CAPTURE);

            takePictureBuilder.addTarget(mImageReader.getSurface());
            int rotacion = getWindowManager().getDefaultDisplay().getRotation();
            takePictureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotacion));
            CaptureRequest mCaptureRequest = takePictureBuilder.build();
            mCaptureSession.capture(mCaptureRequest, null, null);

        } catch (CameraAccessException e) {
            Log.e(TAG, "Error capturing the photo", e);
        }
    }

    private void saveImage(byte[] data) {
        // Save the image JPEG data to external storage
        FileOutputStream outStream = null;
        File fileImagen =new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
        nombreImagen = simpleDateFormat.format(date);
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }
        if(isCreada==true){
            nombreImagen += ".jpg";
        }
        try {
            // String path = Environment.getExternalStorageState().toString();
            String path = getApplicationContext().getCacheDir().toString();
            outputFile = new File(Environment.getExternalStorageDirectory()+File.separator + RUTA_IMAGEN + File.separator + nombreImagen);
            outStream = new FileOutputStream(outputFile);
            outStream.write(data);
            outStream.close();
            if(outputFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
                capturarImagen = (ImageView) findViewById(R.id.capturarImagen);
                capturarImagen.setImageBitmap(bitmap);

                Uri fotoUri = Uri.fromFile(outputFile);

                FotoDAO.getInstancia().subirFotoUri(fotoUri, new FotoDAO.IDevolverURLFoto() {
                    @Override
                    public void devolverUrlString(String url) {
                        Toast.makeText(CamaraActivity.this, "Se guardo la foto correctamente", Toast.LENGTH_SHORT).show();

                        String nombreFoto = "";
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss-mm-hh-dd-MM-yyyy", Locale.getDefault());  //Guardar en Firebase por fecha
                        nombreFoto = simpleDateFormat.format(date);

                        Foto fotoTomada = new Foto();
                        fotoTomada.setFotoPerfilURL(url);
                        fotoTomada.setNombre(nombreFoto);
                        fotoTomada.setLatitud(latitud);
                        fotoTomada.setLongitud(longitud);
                        fotoTomada.setDireccion(dirección);
                        fotoTomada.setValorLuz(String.valueOf(sensorLuz.getValorLuz()));

                        FirebaseUser currentUser = mAuth.getCurrentUser(); //esto funciona cuando esta registrado correctamente
                        DatabaseReference reference = database.getReference("FotosTomadas/" + currentUser.getUid()+"/"+nombreFoto); //guarda el mismo uid del usuario en la database
                        reference.setValue(fotoTomada);

                    }
                });


            }

            Log.d(TAG, "path:" + outputFile.getAbsolutePath());
            Toast.makeText(this , outputFile.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File Not Found", e);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
        }
    }



    private CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCaptureSession = session;
            try {
                mCaptureSession.setRepeatingRequest(
                        mPreviewCaptureRequest.build(),
                        null, // optional CaptureCallback
                        null); // optional Handler
            } catch (CameraAccessException | IllegalStateException e) {
                Log.e(TAG, "Capture Session Exception", e);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //Reconocer la orientación del dispositivo


    @Override
    protected void onResume() {
        super.onResume();
        giroscopio.register();
        acelerometro.register();
        sensorLuz.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        giroscopio.unregister();
        acelerometro.unregister();
        sensorLuz.unregister();
    }

}