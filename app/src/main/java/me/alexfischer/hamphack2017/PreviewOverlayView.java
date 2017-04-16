package me.alexfischer.hamphack2017;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PreviewOverlayView extends View implements View.OnTouchListener {

    private final Camera camera;
    private final Paint faceRectanglePaint = new Paint();
    private final Paint focusPaint1 = new Paint(), focusPaint2 = new Paint();
    private boolean manualFocus = false;
    private float focusX = 0, focusY = 0;
    private float focusCircleRadius;

    public PreviewOverlayView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.faceRectanglePaint.setColor(Color.RED);
        this.faceRectanglePaint.setStyle(Paint.Style.STROKE);
        this.focusPaint1.setColor(Color.WHITE);
        this.focusPaint1.setStyle(Paint.Style.STROKE);
        this.focusPaint1.setStrokeWidth(4);
        this.focusPaint2.setColor(Color.BLACK);
        this.focusPaint2.setStyle(Paint.Style.STROKE);
        this.focusPaint2.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Camera.Face[] faces = ((CameraActivity)getContext()).faces;
        for (Camera.Face face : faces)
        {
            this.drawFaceRectangle(canvas, face);
        }
        if (manualFocus)
        {
            this.focusCircleRadius = 0.1f * Math.max(canvas.getWidth(), canvas.getHeight());
            canvas.drawCircle(focusX, focusY, focusCircleRadius, this.focusPaint1);
            canvas.drawCircle(focusX, focusY, focusCircleRadius - 2, this.focusPaint2);
            canvas.drawCircle(focusX, focusY, focusCircleRadius + 2, this.focusPaint2);
        }
    }

    private void drawFaceRectangle(Canvas c, Camera.Face face)
    {
        c.drawRect(
                faceDimToCanvasDim(face.rect.left, c.getWidth()),
                faceDimToCanvasDim(face.rect.top, c.getHeight()),
                faceDimToCanvasDim(face.rect.right, c.getWidth()),
                faceDimToCanvasDim(face.rect.bottom, c.getHeight()),
                this.faceRectanglePaint
        );
    }

    private float faceDimToCanvasDim(int faceDim, int canvasDim)
    {
        return (faceDim + 1000f) / 2000f * canvasDim;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e)
    {
        this.focusX = e.getX();
        this.focusY = e.getY();
        this.manualFocus = true;
        this.invalidate();
        Camera.Parameters params = this.camera.getParameters();
        Rect focusRect = new Rect((int)(focusX * 2000f / v.getWidth() - 1000 - 100),
                (int)(focusY * 2000f / v.getHeight() - 1000 - 100),
                (int)(focusX * 2000f / v.getWidth() - 1000 + 100),
                (int)(focusY * 2000f / v.getHeight() - 1000 + 100));
        Log.d(Util.logtag, focusRect.toShortString());
        ArrayList<Camera.Area> focusAreas = new ArrayList<>();
        focusAreas.add(new Camera.Area(focusRect, 1000));
        params.setFocusAreas(focusAreas);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        return false;
    }
}
