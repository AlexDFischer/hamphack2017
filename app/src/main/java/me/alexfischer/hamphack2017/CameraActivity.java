package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CameraActivity extends Activity {

    public boolean cameraOpen = false;
    public Camera camera;
    private CameraPreviewView previewView;
    private int numPeople;
    public FaceDetector faceDetector;
    public Camera.Face[] faces = new Camera.Face[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        numPeople = getIntent().getIntExtra("numPeople", 1);
        TextView numPeopleTextView = (TextView)findViewById(R.id.numPeopleTextView);
        numPeopleTextView.setText(Util.numPeoplePresentText.replace("{0}", "0").replace("{1}", Integer.toString(numPeople)));
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
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        this.camera.setParameters(params);

        PreviewOverlayView previewOverlayView = new PreviewOverlayView(this, camera);
        this.previewView = new CameraPreviewView(this, camera, previewOverlayView, numPeople);
        FrameLayout previewLayout = (FrameLayout)findViewById(R.id.previewLayout);
        previewLayout.addView(this.previewView);
        previewLayout.addView(previewOverlayView);
        previewOverlayView.setOnTouchListener(previewOverlayView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(Util.logtag, "called onPause for CameraActivity");

        if (cameraOpen)
        {
            camera.release();
            Log.i(Util.logtag, "released camera");
            this.cameraOpen = false;
            finish();
        }

    }
}
