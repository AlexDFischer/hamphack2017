package me.alexfischer.hamphack2017;


import android.hardware.Camera;
import android.os.CountDownTimer;

public class PictureCountdownTimer extends CountDownTimer
{
    private Camera camera;
    private Camera.PictureCallback callback;

    public PictureCountdownTimer(long millisInFuture, long countDownInterval, Camera camera, Camera.PictureCallback callback)
    {
        super(millisInFuture, countDownInterval);
        this.camera = camera;
        this.callback = callback;
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        camera.takePicture(null, null, callback);
    }

    @Override
    public void onFinish()
    {
        camera.takePicture(null, null, callback);
    }
}
