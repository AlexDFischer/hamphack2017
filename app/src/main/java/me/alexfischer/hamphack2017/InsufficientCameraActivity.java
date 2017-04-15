package me.alexfischer.hamphack2017;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class InsufficientCameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insufficient_camera);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    public void onOKButtonClicked(View view)
    {
        System.exit(0);
    }
}
