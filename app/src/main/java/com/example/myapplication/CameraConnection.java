package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraConnection {
    String ipString = "http://192.168.1.31:5000/";//http://192.168.250.219:5000/";//"http://192.168.250.219:5000/ "; // http://192.168.241.219:5000/ "http://192.168.1.23:5000/"; //"http://77.125.87.0:5000/";//"147.161.13.194";

    int port = 5000;
    private final Uri mImgUriArr;
    private boolean success = false;
    private boolean finished = false;
    private JSONObject res;
    Context mContext;

    private static final String TAG = "PhotoConnectionDebug";

    public CameraConnection(Uri ImgUri, Context context){
        res = null;
        mImgUriArr = ImgUri;
        mContext = context;
    }

    public JSONObject getRes(){
        return res;
    }

    public boolean getSuccess() {
        return success;
    }

    public boolean isFinished(){
        return finished;
    }

    public void connect(){

        String postUrl = ipString;

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            // Read BitMap by file path.
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mImgUriArr);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!found picture from uri!!!!!!!!!!!!!!!!!");
        }catch(Exception e){
            //todo: handle failure
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!problem with picture!!!!!!!!!!!!!!!!!!!");
            return;
        }
        byte[] byteArray = stream.toByteArray();
        Random rand = new Random();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        multipartBodyBuilder.addFormDataPart("image", "Android_Flasktry"  + timeStamp + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
        RequestBody postBodyImage = multipartBodyBuilder.build();
        postRequest(postUrl, postBodyImage);

    }
    void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                finished = true;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try {
                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!onResponse!!!!!!!!!!!!!!!!!!!");

                    success = true;

                    res = new JSONObject(Objects.requireNonNull(response.body()).string());
                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!onResponseresresresresres!!!!!!!!!!!!!!!!!!!");

                    finished = true;
                } catch (IOException | JSONException e) {
                    success = false;
                    finished = true;
                }
            }
        });
    }
}
