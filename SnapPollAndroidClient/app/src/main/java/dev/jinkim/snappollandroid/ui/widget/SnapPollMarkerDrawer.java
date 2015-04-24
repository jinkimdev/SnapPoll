package dev.jinkim.snappollandroid.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Response;
import dev.jinkim.snappollandroid.util.ColorUtil;

/**
 * Created by Jin on 4/16/15.
 */
public class SnapPollMarkerDrawer {

    private Context context;

    private Paint responseCirclePaint;
    private Paint responseCircleBorderIn;

    private Paint responsePinPaint;

    private Bitmap spScaledSelectorBmp;
    private float spScaledSelectorWidth;
    private float spScaledSelectorHeight;

    private String spResponseMarkerColorHex;
    private TouchImageView touchImageView;

    private final float RESPONSE_CIRCLE_STROKE_WIDTH = 14f;
    private final float RESPONSE_CIRCLE_RADIUS = 28f;
    private final int CIRCLE_STROKE_ALPHA = 170;

    private final float CIRCLE_BORDER_IN_STROKE_WIDTH = 2f;
    private final float CIRCLE_BORDER_IN_RADIUS = RESPONSE_CIRCLE_RADIUS - RESPONSE_CIRCLE_STROKE_WIDTH / 2;

    public SnapPollMarkerDrawer(Context context, View touchImageView) {
        this.context = context;
        setDefaultColor();
        this.touchImageView = (TouchImageView) touchImageView;

        setupResponseCirclePaint();
        setupResponseCircleBorderIn();
        setupResponsePinPaint();
        setDefaultColor();
    }

    private void setupResponseCirclePaint() {
        responseCirclePaint = new Paint();
        responseCirclePaint.setStyle(Paint.Style.STROKE);
        responseCirclePaint.setColor(Color.parseColor(spResponseMarkerColorHex));
        responseCirclePaint.setStrokeWidth(RESPONSE_CIRCLE_STROKE_WIDTH);
        responseCirclePaint.setAlpha(CIRCLE_STROKE_ALPHA);
        responseCirclePaint.setAntiAlias(true);
    }

    private void setupResponseCircleBorderIn() {
        responseCircleBorderIn = new Paint();
        responseCircleBorderIn.setStyle(Paint.Style.STROKE);
        responseCircleBorderIn.setColor(Color.parseColor("#555555"));
        responseCircleBorderIn.setStrokeWidth(CIRCLE_BORDER_IN_STROKE_WIDTH);
        responseCircleBorderIn.setAntiAlias(true);
    }

    public void updateMarkerColor(String colorHex) {
        spResponseMarkerColorHex = colorHex;
        responseCirclePaint.setColor(Color.parseColor(colorHex));
        touchImageView.invalidate();
    }

    public void drawResponseCircle(Canvas canvas, PointF scrCoords) {
        canvas.drawCircle(scrCoords.x, scrCoords.y, RESPONSE_CIRCLE_RADIUS, responseCirclePaint);
        canvas.drawCircle(scrCoords.x, scrCoords.y, CIRCLE_BORDER_IN_RADIUS, responseCircleBorderIn);

        touchImageView.invalidate();
    }

    private void setupResponsePinPaint() {
        // draw selector marker pin above touch (selection) point
        responsePinPaint = new Paint();
        responsePinPaint.setAntiAlias(true);
        responsePinPaint.setFilterBitmap(true);
        responsePinPaint.setDither(true);
    }

    private void setupSelectorBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap spSelectorBmp = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_marker_red, options);

        // TODO: Find optimal marker size (scale) based on screen
        spScaledSelectorBmp = Bitmap.createScaledBitmap(
                spSelectorBmp, 196, 196, true);

        spScaledSelectorWidth = spScaledSelectorBmp.getWidth();
        spScaledSelectorHeight = spScaledSelectorBmp.getHeight();
    }

    public void drawResponsePin(Canvas canvas, PointF scrCoords) {

        if (spScaledSelectorBmp == null) {
            setupSelectorBitmap();
        }

        canvas.drawBitmap(spScaledSelectorBmp,
                scrCoords.x - (spScaledSelectorWidth / 2),
                scrCoords.y - spScaledSelectorHeight,
                responsePinPaint);
    }

    public void drawResultResponses(Canvas canvas, List<Response> pollResponses) {
        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.parseColor(spResponseMarkerColorHex));
        circlePaint.setStrokeWidth(12f);
        circlePaint.setAlpha(100);
        circlePaint.setAntiAlias(true);

        if (pollResponses != null) {
            for (Response re : pollResponses) {
                PointF scrCoords = touchImageView.transformCoordBitmapToTouch(re.getX(), re.getY());

                // set color of each response according to attribute selection
                String colorHex = re.getAttributeColorHex();
                if (colorHex == null) {
                    colorHex = context.getResources().getString(R.string.color_default_marker);
                }
                circlePaint.setColor(Color.parseColor(colorHex));

                canvas.drawCircle(
                        scrCoords.x,
                        scrCoords.y,
                        RESPONSE_CIRCLE_RADIUS, circlePaint);
                canvas.drawCircle(scrCoords.x,
                        scrCoords.y,
                        CIRCLE_BORDER_IN_RADIUS,
                        responseCircleBorderIn);
            }
        }
    }

    private void setDefaultColor() {
        String colorHex;
        colorHex = ColorUtil.convertToHex(context.getResources().getColor(R.color.attribute_default_marker_color));
        if (colorHex == null) {
            int c = context.getResources().getColor(R.color.app_primary);
            colorHex = ColorUtil.convertToHex(c);
        }
        spResponseMarkerColorHex = colorHex;
    }


}
