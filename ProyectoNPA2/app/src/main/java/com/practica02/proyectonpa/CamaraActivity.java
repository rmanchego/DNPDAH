package com.practica02.proyectonpa;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
        verifyStoragePermissions(this);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"foto tomada");
                takePicture();
            }
        });
        //checkcameras();
        init();

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
        } catch (Exception e) {
            Log.e(TAG, "No se puede abrir la cámara", e);
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
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }
        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
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

    public void checkcameras() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            String[] cameraIds = cameraManager.getCameraIdList();
            for (String cameraId : cameraIds) {
                Log.d(TAG, cameraId);
                CameraCharacteristics characteristics =
                        cameraManager.getCameraCharacteristics(cameraId);

                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    Log.d(TAG, "back camera");
                    // back camera
                } else if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    Log.d(TAG, "front camera");
                    // front camera
                } else {
                    Log.d(TAG, "external camera");
                    // external cameraCameraCharacteristics.LENS_FACING_EXTERNAL
                }

            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

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
}