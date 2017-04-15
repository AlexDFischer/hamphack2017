package me.alexfischer.hamphack2017;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import io.indico.api.*;

/**
 * TODO: document your custom view class.
 */
public class FaceIdentifierView extends View {

    private Camera.Face[] faces = new Camera.Face[0];

    private Paint faceRectanglePaint;

    public FaceIdentifierView(Context context) {
        super(context);
        init(null, 0);
    }

    public FaceIdentifierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FaceIdentifierView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.FaceIdentifierView, defStyle, 0);

        this.faceRectanglePaint = new Paint();
        this.faceRectanglePaint.setColor(Color.RED);
        this.faceRectanglePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw rectangles around faces
        for (Camera.Face face : this.faces)
        {
            //this.drawFaceRectangle(canvas, face);
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

    public void setFaces(Camera.Face[] faces)
    {
        this.faces = faces;
        this.invalidate();
    }
}
