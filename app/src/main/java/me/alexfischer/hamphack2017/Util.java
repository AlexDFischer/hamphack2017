package me.alexfischer.hamphack2017;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Scanner;


public class Util {
    /** Check if this device has a camera */
    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static final String logtag = "hamphacklog";
    public static final String mediaDir = "hamphack2017";
    public static final String numPeoplePresentText = "{0} out of {1} people present";
    public static final int maxDelay = 5;
    public static final String indicoAPIKey = "25b4ecc0c0518f9347d1151138cdfb44";

    public static Bitmap getFaceBitmap(Bitmap bitmap, Camera.Face face)
    {
        //Log.d(Util.logtag, "trying face coordinates: " + face.rect.toShortString());
        float x = ((float)bitmap.getWidth() * (face.rect.left + 1000) / 2000f);
        float y = ((float)bitmap.getHeight() * (face.rect.top + 1000) / 2000f);
        float width = (int)((float)bitmap.getWidth() * face.rect.width() / 2000f);
        float height = (int)((float)bitmap.getHeight() * face.rect.height() / 2000f);
        x -= 0.5f * width;
        y -= 0.5f * height;
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        width *= 2;
        height *= 2;
        width = Math.min(width, bitmap.getWidth() - x);
        height = Math.min(height, bitmap.getHeight() - y);
        return Bitmap.createBitmap(bitmap, (int)x, (int)y, (int)width, (int)height);
    }

    public static String base64EncodedJpPEG(Bitmap image)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static boolean allFacesHappy(String response, int numFaces)
    {
        try
        {
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < numFaces; i++)
            {
                double angry = results.getJSONObject(i).getDouble("Angry");
                double sad = results.getJSONObject(i).getDouble("Sad");
                double neutral = results.getJSONObject(i).getDouble("Neutral");
                double surprise = results.getJSONObject(i).getDouble("Surprise");
                double fear = results.getJSONObject(i).getDouble("Fear");
                double happy = results.getJSONObject(i).getDouble("Happy");
                Log.i(Util.logtag, "Person " + i + ":"
                        + "\nangry: " + angry
                        + "\nsad: " + sad
                        + "\nneutral: " + neutral
                        + "\nsurprise: " + surprise
                        + "\nfear: " + fear
                        + "\nhappy: " + happy);
                if (sad + neutral > (happy + surprise + fear))
                {
                    return false;
                }
            }
            return true;
        } catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
