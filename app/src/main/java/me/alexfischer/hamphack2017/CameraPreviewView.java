package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.*;

import java.io.IOException;

/** A basic Camera preview class */
public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;
    private FaceIdentifierView faceIdentifierView;
    private final int numPeople;
    private final int delay;

    public CameraPreviewView(Context context, Camera camera, FaceIdentifierView faceIdentifierView, int numPeople, int delay) {
        super(context);
        this.camera = camera;
        this.faceIdentifierView = faceIdentifierView;
        Log.d(Util.logtag, "CameraPreviewView constructor called");

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.numPeople = numPeople;
        this.delay = delay;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(Util.logtag, "surfaceCreated called");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            setCameraDisplayOrientation((Activity)getContext(), backFacingCameraId(), camera);

            camera.setPreviewDisplay(holder);
            camera.startPreview();

            this.camera.setFaceDetectionListener(new FaceDetector(this.faceIdentifierView, this.numPeople, new MyPictureCallback((Activity)getContext()), (Activity)getContext(), delay));
            camera.startFaceDetection();
        } catch (Exception e) {
            Log.d(Util.logtag, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            setCameraDisplayOrientation((Activity)getContext(), backFacingCameraId(), camera);
            camera.setPreviewDisplay(holder);
            camera.startPreview();

            this.camera.setFaceDetectionListener(new FaceDetector(this.faceIdentifierView, this.numPeople, new MyPictureCallback((Activity)getContext()), (Activity)getContext(), delay));
            camera.startFaceDetection();
        } catch (Exception e){
            Log.d(Util.logtag, "Error starting camera preview: " + e.getMessage());
        }
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    private int backFacingCameraId()
    {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return -1;
    }
}