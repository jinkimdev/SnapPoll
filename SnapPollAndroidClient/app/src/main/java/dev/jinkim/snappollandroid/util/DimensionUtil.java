package dev.jinkim.snappollandroid.util;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Jin on 1/14/15.
 */
public class DimensionUtil {
    private Context context;
    private Point screen;

    public static final String TAG = DimensionUtil.class.getSimpleName();

    public DimensionUtil(Context ctx) {
        context = ctx;
    }

    public Point getScreenDimensionPixel() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        screen = size;

        return size;
    }

    // find minZoom ratio that will fit the image within screen
    public float getMinZoomRatio(float screenWid, float screenHt, int imgWid, int imgHt) {

        //TODO: What if img res is bigger than screen?
        float widthRatio = screenWid / (float) imgWid;
        float heightRatio = screenHt / (float) imgHt;

        Log.d(TAG, "Screen dimen: " + screenWid + " x " + screenHt);
        Log.d(TAG, "Image dimen: " + imgWid + " x " + imgHt);
        Log.d(TAG, "Ratio: " + widthRatio + ", " + heightRatio);

        return Math.min(widthRatio, heightRatio);
    }

    // find maxZoom ratio that will fit the image within screen (centerInside) for no blank space
    public float getMaxZoomRatio(float screenWid, float screenHt, int imgWid, int imgHt) {

        //TODO: What if img res is bigger than screen?
        float widthRatio = screenWid / (float) imgWid;
        float heightRatio = screenHt / (float) imgHt;

        Log.d(TAG, "Screen dimen: " + screenWid + " x " + screenHt);
        Log.d(TAG, "Image dimen: " + imgWid + " x " + imgHt);
        Log.d(TAG, "Ratio: " + widthRatio + ", " + heightRatio);

        return Math.max(widthRatio, heightRatio);
    }

    public float getCenterZoomRatio(float containerWid, float containerHt, int imageWid, int imageHt) {

        // this min is applied to image view already
        float fitScaleRatio = getMinZoomRatio(containerWid, containerHt, imageWid, imageHt);

        float currentImgHt = imageHt * fitScaleRatio;
        float currentImgWid = imageWid * fitScaleRatio;

        float zoom;

        if (containerWid - currentImgWid > containerHt - currentImgHt) {
            zoom = containerWid / currentImgWid;
        } else {
            zoom = containerHt / currentImgHt;
        }

        return zoom;
    }
}
