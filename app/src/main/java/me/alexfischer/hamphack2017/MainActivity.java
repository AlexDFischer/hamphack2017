package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private int maxFaces = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter<Integer> adapter;
        // determine if camera can run & if facial recognition works
        Camera camera;
        try
        {
            camera = Camera.open();
            maxFaces = camera.getParameters().getMaxNumDetectedFaces();
            camera.release();
            Log.i(Util.logtag, "number of faces is " + maxFaces);
            if (maxFaces < 1)
            {
                this.insufficientCameraExit();
            }
            Integer[] numPeopleOptions = new Integer[maxFaces];
            for (int i = 0; i < maxFaces; i++)
            {
                numPeopleOptions[i] = i + 1;
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numPeopleOptions);
            Spinner numPeopleSpinner = (Spinner)findViewById(R.id.numPeopleSpinner);
            numPeopleSpinner.setAdapter(adapter);
        } catch (Exception e)
        {
            Log.e(Util.logtag, "exception when opening camera");
            this.insufficientCameraExit();
        }
        // set up delay spinner
        Integer[] delayOptions = new Integer[Util.maxDelay];
        for (int i = 0; i < Util.maxDelay; i++)
        {
            delayOptions[i] = i + 1;
        }
        adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, delayOptions);
        Spinner delaySpinner = (Spinner)findViewById(R.id.delaySpinner);
        delaySpinner.setAdapter(adapter);
    }

    public void onCameraButtonClick(View view)
    {
        Intent intent = new Intent(this, CameraActivity.class);

        Spinner spinner = (Spinner)findViewById(R.id.numPeopleSpinner);
        int numPeople = (Integer)spinner.getSelectedItem();
        intent.putExtra("numPeople", numPeople);

        spinner = (Spinner)findViewById(R.id.delaySpinner);
        int delay = (Integer)spinner.getSelectedItem();
        intent.putExtra("delay", delay);

        startActivity(intent);
    }

    private void insufficientCameraExit()
    {
        Intent intent = new Intent(this, InsufficientCameraActivity.class);
        startActivity(intent);
    }
}
