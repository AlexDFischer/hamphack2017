package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.hardware.*;
import android.util.Log;
import android.widget.TextView;

public class FaceDetector implements Camera.FaceDetectionListener
{
    private final FaceIdentifierView faceIdentifierView;
    private final int numPeople;
    private final Camera.PictureCallback pictureCallback;
    private final Activity activity;
    private boolean tookPicture = false;
    private final TextView numPeopleTextView;
    private final int delay;

    public FaceDetector(FaceIdentifierView faceIdentifierView, int numPeople, Camera.PictureCallback pictureCallback, Activity activity, int delay)
    {
        this.faceIdentifierView = faceIdentifierView;
        this.numPeople = numPeople;
        this.pictureCallback = pictureCallback;
        this.activity = activity;
        this.numPeopleTextView = (TextView)activity.findViewById(R.id.numPeopleTextView);
        this.delay = delay;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera)
    {
        this.faceIdentifierView.setFaces(faces);
        numPeopleTextView.setText(Util.numPeoplePresentText.replace("{0}", Integer.toString(faces.length)).replace("{1}", Integer.toString(numPeople)));
        if (faces.length == numPeople && !tookPicture)
        {
            Log.i(Util.logtag, "detected right number of faces");
            new PictureCountdownTimer(1000L * delay, 1000L * delay, camera, new MyPictureCallback(activity)).start();
            this.tookPicture = true;
        }
    }
}
