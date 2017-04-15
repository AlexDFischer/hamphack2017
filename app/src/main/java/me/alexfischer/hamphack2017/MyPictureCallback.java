package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MyPictureCallback implements Camera.PictureCallback {

    public MyPictureCallback(Activity activity)
    {
        this.activity = activity;
    }

    private Activity activity;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        mediaStorageDir = new File(mediaStorageDir, Util.mediaDir);
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(Util.logtag, "failed to create directory");
                activity.finish();
                return;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pictureFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(Util.logtag, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(Util.logtag, "Error accessing file: " + e.getMessage());
        }
        Log.i(Util.logtag, "wrote picture to file: " + pictureFile.getAbsolutePath());
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, pictureFile.getAbsolutePath());

        activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + pictureFile.getAbsolutePath()), "image/*");
        this.activity.startActivity(intent);
        activity.finish();
    }

}
