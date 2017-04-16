package me.alexfischer.hamphack2017;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.icu.util.Output;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import io.indico.Indico;
import io.indico.network.*;
import io.indico.results.*;
import io.indico.utils.*;

public class FaceEmotions
{
    private static void test(Context context)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    String url = "https://apiv2.indico.io/sentiment/batch/";
                    String charset = "UTF-8";
                    HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Accept-Charset", charset);
                    connection.setRequestProperty("X-ApiKey", Util.indicoAPIKey);
                    OutputStream output = connection.getOutputStream();
                    output.write("{\"data\":[\"I love writing code!\", \"Alexander and the Terrible, Horrible, No Good, Very Bad Day\"]}".getBytes());
                    output.flush();
                    InputStream input = connection.getInputStream();
                    int c;
                    String result = "";
                    while ((c = input.read()) != -1)
                    {
                        result += (char)c;
                    }
                    Log.i(Util.logtag, "Indico response is " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public static boolean fer(Bitmap image, final Camera.Face[] faces)
    {
        final String[] base64FaceImages = new String[faces.length];
        for (int i = 0; i < faces.length; i++)
        {
            Bitmap faceBitmap = Util.getFaceBitmap(image, faces[i]);
            base64FaceImages[i] = Util.base64EncodedJpPEG(faceBitmap);
        }
        boolean allFacesHappy = false;
        try
        {
            String url = "https://apiv2.indico.io/fer/batch/";
            String charset = "UTF-8";
            HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("X-ApiKey", Util.indicoAPIKey);
            OutputStream output = connection.getOutputStream();
            output.write("{\"data\": [".getBytes());
            for (int i = 0; i < base64FaceImages.length; i++)
            {
                output.write('"');
                output.write(base64FaceImages[i].getBytes());
                output.write('"');
                if (i < base64FaceImages.length - 1)
                {
                    output.write(',');
                }
                output.write('\n');
            }
            output.write("]}\n".getBytes());
            output.flush();
            Scanner s = new Scanner(connection.getInputStream());
            StringBuilder sb = new StringBuilder();
            String line;
            while (s.hasNextLine())
            {
                line = s.nextLine();
                sb.append(line);
                sb.append('\n');
            }
            //Log.i(Util.logtag, "Indico response is " + result);
            allFacesHappy = Util.allFacesHappy(sb.toString(), faces.length);
            Log.i(Util.logtag, "Are all faces happy? " + Util.allFacesHappy(sb.toString(), faces.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allFacesHappy;
    }
}
