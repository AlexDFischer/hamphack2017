package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.hardware.*;
import android.util.Log;
import android.widget.TextView;

public class FaceDetector implements Camera.FaceDetectionListener
{
    private final PreviewOverlayView previewOverlayView;
    private final int numPeople;
    private final Camera.PictureCallback pictureCallback;
    private final CameraActivity activity;
    public boolean tookPicture = false;
    private final TextView numPeopleTextView;

    public FaceDetector(PreviewOverlayView previewOverlayView, int numPeople, Camera.PictureCallback pictureCallback, CameraActivity activity)
    {
        this.previewOverlayView = previewOverlayView;
        this.numPeople = numPeople;
        this.pictureCallback = pictureCallback;
        this.activity = activity;
        this.numPeopleTextView = (TextView)activity.findViewById(R.id.numPeopleTextView);
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera)
    {
        if (!tookPicture) {
            this.previewOverlayView.postInvalidate();
            numPeopleTextView.setText(Util.numPeoplePresentText.replace("{0}", Integer.toString(faces.length)).replace("{1}", Integer.toString(numPeople)));
            activity.faces = faces;
            if (faces.length == numPeople) {
                tookPicture = true;
                Log.i(Util.logtag, "detected right number of faces: " + faces.length);
                camera.takePicture(null, null, new MyPictureCallback(activity));
                camera.stopFaceDetection();
            }
        }
    }
}
