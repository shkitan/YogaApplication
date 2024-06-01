package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class guideActivity extends Activity implements  LoaderManager.LoaderCallbacks<CameraConnection>, LifecycleOwner,ImageAnalysis.Analyzer, View.OnClickListener {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //
    private LifecycleRegistry mLifecycleRegistry;
    PreviewView previewView;
    private ImageCapture imageCapture;
    //    private VideoCapture videoCapture;
    private Button bRecord;
    private Button bCapture;
    private JSONObject mCurData;
    private boolean firstTime;
    Uri uriArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        bCapture = findViewById(R.id.bCapture);
        bCapture.setOnClickListener(this);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());


    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(getExecutor(), this);
//
//        //bind to lifecycle:
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        // image processing here for the current frame
        Log.d("TAG", "analyze: got the frame at: " + image.getImageInfo().getTimestamp());
        image.close();
    }

    @Override
    public void onClick(View view) {
        TextView tv = (TextView)findViewById(R.id.textView1);
        tv.setText("");
        capturePhoto();
        tv.setText("");
////        new CountDownTimer(5000, 1000) {
//        CountDownTimer countDownTimer = new CountDownTimer(500000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.d("TAG", "inClock !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            }
//
//            public void onFinish() {
//            }
//        }.start();
//        capturePhoto();
//        tv.setText("");
//        countDownTimer = new CountDownTimer(500000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.d("TAG", "inClock !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            }
//
//            public void onFinish() {
//            }
//        }.start();
//        capturePhoto();

    }


//    @SuppressLint("RestrictedApi")
    private void capturePhoto() {
        Log.d("TAG", "!!!!!!!!!!!!!!!!!!!!!ooooooo!!!!!!!!!!!!!!!!!!!");

        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Log.d("TAG", "!!!!!!!!!!!!!!!!!!!!!ininininininininin!!!!!!!!!!!!!!!!!!!");

                Bitmap bitmap = getBitmap(image);
//                ImageView takenImage.setImageBitmap(bitmap);
                super.onCaptureSuccess(image);


                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String name = "JPEG_" + timeStamp + "_";
                OutputStream fos = null;
                String sdDir = Environment.DIRECTORY_DCIM + File.separator + getResources()
                        .getString(R.string.app_name);
                File dir = new File(sdDir);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, sdDir);
                }
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                uriArr = imageUri;
//                mImgUri = imageUri;
                try {
                    fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", imageUri.getPath() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                try {
                    Objects.requireNonNull(fos).close();
                    LoaderManager.LoaderCallbacks<CameraConnection> callback = (LoaderManager.LoaderCallbacks<CameraConnection>) guideActivity.this;
                    Bundle bundleForLoader = null;
                    getLoaderManager().initLoader(1, null, callback); // ERROR IS HERE in 3rd Argument
                } catch (IOException e) {
                    Log.d("TAG", " !!!!!!!!!!printStackTrace!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ImageCaptureException exception) {
                Log.d("PHOTO", "There is a problem with the server");
            }

        });

    }

    private Bitmap getBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<CameraConnection> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<CameraConnection>(this) {

            @SuppressLint("StaticFieldLeak")
            CameraConnection cameraConnection = null;

            @Override
            protected void onStartLoading() {
                cameraConnection = new CameraConnection(uriArr, this.getContext());
                forceLoad();
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            public CameraConnection loadInBackground() {
                Log.d("TAG", "!!!!!!!!!!!!!!!!!!!!!connectStart!!!!!!!!!!!!!!!!!!!");

                cameraConnection.connect();
                Log.d("TAG", "!!!!!!!!!!!!!!!!!!!!!connectend!!!!!!!!!!!!!!!!!!!");

                while(!cameraConnection.isFinished()){}
                return cameraConnection;
            }

            public void deliverResult(CameraConnection data) {
                cameraConnection = data;
                Log.d("debug", "deliverResult!!!!!!!!!!!!!!!! "+data.getRes() + " !!!!!!!!!!!!!!!!!!!");

                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<CameraConnection> loader, CameraConnection data) {
        Log.d("debug", "onLoadFinished!!!!!!!!!!!!!!!! "+data.getRes() + " !!!!!!!!!!!!!!!!!!!");
        if (null == data || !data.getSuccess()) {
            TextView tv = (TextView)findViewById(R.id.textView1);
//            tv.setText("The pose you trying to fo is Tree\nYou should straight your left hand more about 123 degree\nYou should straight your right hand more about 131 degree\nYou need to raise your left hand more about 48 degrees\nTYou need to raise your right hand more about 55 degrees\n");
            Toast.makeText(this, "there is a problem with the sever", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        } else {
            TextView tv = (TextView)findViewById(R.id.textView1);
            String s = "";
            Iterator<String> keys = data.getRes().keys();
            while(keys.hasNext()) {
                String key = keys.next();
                s += key;
                s += " ";
                try {
                    s += data.getRes().get(key);
                    s +="\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            tv.setText(s);
            mCurData = data.getRes();
            if(mCurData.length() == 0){
                if(firstTime) {
                    firstTime = false;
                    Toast.makeText(this, "err", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    firstTime = true;
                    onLoaderReset(loader);
                    return;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CameraConnection> loader) {
        loader.startLoading();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        if (mLifecycleRegistry==null){

            mLifecycleRegistry= new LifecycleRegistry(this);
        }
        return mLifecycleRegistry;
    }
}