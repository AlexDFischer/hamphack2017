package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CameraActivity extends Activity {

    private static boolean cameraOpen = false;
    private Camera camera;
    private CameraPreviewView previewView;
    private int numPeople;
    private int numPicturesToTake;
    private int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        numPeople = getIntent().getIntExtra("numPeople", 1);
        delay = getIntent().getIntExtra("delay", 1);
        TextView numPeopleTextView = (TextView)findViewById(R.id.numPeopleTextView);
        numPeopleTextView.setText(Util.numPeoplePresentText.replace("{0}", "0").replace("{1}", Integer.toString(numPeople)));
        Log.d(Util.logtag, "number of people we're looking for is " + numPeople + " and delay is " + delay);
        try
        {
            this.camera = Camera.open();
        } catch (Exception e)
        {
            Log.e(Util.logtag, "exception when opening camera");
            finish();
        }
        this.cameraOpen = true;
        Camera.Parameters params = this.camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        this.camera.setParameters(params);

        FaceIdentifierView faceIdentifierView = new FaceIdentifierView(this);
        this.previewView = new CameraPreviewView(this, camera, faceIdentifierView, numPeople, delay);
        FrameLayout previewLayout = (FrameLayout)findViewById(R.id.previewLayout);
        previewLayout.addView(this.previewView);
        previewLayout.addView(faceIdentifierView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
        if (cameraOpen)
        {
            camera.release();
            Log.i(Util.logtag, "released camera");
            this.cameraOpen = false;
            //finish();
        }
    }
}
