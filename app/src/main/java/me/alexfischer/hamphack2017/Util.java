package me.alexfischer.hamphack2017;

import android.content.Context;
import android.content.pm.PackageManager;


public class Util {
    /** Check if this device has a camera */
    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static final String logtag = "hamphacklog";
    public static final String mediaDir = "hamphack2017";
    public static final String numPeoplePresentText = "{0} out of {1} people present";
    public static final int maxDelay = 5;
}
