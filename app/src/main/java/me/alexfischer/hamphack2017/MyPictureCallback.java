package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public MyPictureCallback(CameraActivity activity)
    {
        this.activity = activity;
    }

    private CameraActivity activity;

    @Override
    public void onPictureTaken(final byte[] data, final Camera camera) {
        new Thread()
        {
            @Override
            public void run()
            {
                Log.d(Util.logtag, "entering onPictureTaken Thread");
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
                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.d(Util.logtag, "about to ask Indico if they're all happy");
                boolean allFacesHappy = FaceEmotions.fer(image, activity.faces);
                if (allFacesHappy)
                {
                    Log.d(Util.logtag, "all faces happy, writing to file and exiting");
                    /*
                    for (int i = 0; i < activity.faces.length; i++)
                    {
                        Bitmap faceBitmap = Util.getFaceBitmap(image, activity.faces[i]);
                        File faceFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + "_FACE_" + i + ".jpg");
                        try {
                            FileOutputStream fos = new FileOutputStream(faceFile);
                            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d(Util.logtag, "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d(Util.logtag, "Error accessing file: " + e.getMessage());
                        }
                    }
                    */
                    File pictureFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        image.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        //fos.write(data);
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
                    activity.startActivity(intent);
                    activity.camera.release();
                    activity.finish();
                } else
                {
                    Log.d(Util.logtag, "not all faces happy");

                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    //if (!activity.cameraOpen)
                    //{
                    //    activity.camera = Camera.open();
                    //}
                    //camera.takePicture(null, null, MyPictureCallback.this);
                    activity.camera.startPreview();
                    activity.camera.startFaceDetection();
                    activity.faceDetector.tookPicture = false;
                }
            }
        }.start();
    }
}
