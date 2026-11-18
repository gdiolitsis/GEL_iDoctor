package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * LAB 6 — actual two-finger pinch-to-zoom verification.
 * One-finger grid and color/uniformity stages remain separate and unchanged.
 * Android MotionEvent supplies distinct simultaneous pointer IDs. No Shizuku,
 * privileged service, raw touch coordinates in exported logs, or fake PASS.
 */
public final class PinchZoomTestActivity extends GELAutoActivityHook {
    public static final String EXTRA_DUAL_TOUCH = "lab6_pinch_dual_touch";
    public static final String EXTRA_ZOOM_IN = "lab6_pinch_zoom_in";
    public static final String EXTRA_ZOOM_OUT = "lab6_pinch_zoom_out";

    private GelPinchZoomEvidence evidence;
    private TextView expandStatus;
    private TextView contractStatus;
    private Button continueButton;

    private int dp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        evidence = new GelPinchZoomEvidence(dp(42), dp(30));
        final boolean gr = AppLang.isGreek(this);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);

        ZoomSurface surface = new ZoomSurface(this);
        FrameLayout.LayoutParams zone = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        zone.topMargin = dp(150);
        zone.bottomMargin = dp(140);
        root.addView(surface, zone);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18), dp(10), dp(18), dp(8));
        TextView title = new TextView(this);
        title.setText(gr ? "LAB 6 — Ζουμ δύο δαχτύλων"
                         : "LAB 6 — Two-finger pinch-to-zoom");
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(18f);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(title);

        TextView guide = new TextView(this);
        guide.setText(gr
                ? "Ακούμπησε ΔΥΟ δάχτυλα στον κύκλο. Άνοιξέ τα για μεγέθυνση και κλείσ' τα για σμίκρυνση."
                : "Put TWO fingers on the circle. Spread them apart to zoom in, then pinch together to zoom out.");
        guide.setTextColor(Color.WHITE);
        guide.setTextSize(14f);
        guide.setGravity(Gravity.CENTER);
        guide.setPadding(0, dp(7), 0, dp(6));
        header.addView(guide);

        expandStatus = new TextView(this);
        contractStatus = new TextView(this);
        for (TextView status : new TextView[]{expandStatus, contractStatus}) {
            status.setGravity(Gravity.CENTER);
            status.setTextSize(15f);
            header.addView(status);
        }
        FrameLayout.LayoutParams top = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP);
        root.addView(header, top);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setPadding(dp(18), dp(4), dp(18), dp(14));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        continueButton = makeButton(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE", true);
        Button skipButton = makeButton(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP", false);
        LinearLayout.LayoutParams half = new LinearLayout.LayoutParams(
                0, dp(52), 1f);
        half.rightMargin = dp(7);
        row.addView(continueButton, half);
        LinearLayout.LayoutParams otherHalf = new LinearLayout.LayoutParams(
                0, dp(52), 1f);
        otherHalf.leftMargin = dp(7);
        row.addView(skipButton, otherHalf);
        actions.addView(row);

        Button exitButton = makeButton(gr ? "ΕΞΟΔΟΣ" : "EXIT", false);
        LinearLayout.LayoutParams exitParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
        exitParams.topMargin = dp(8);
        actions.addView(exitButton, exitParams);
        FrameLayout.LayoutParams bottom = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        root.addView(actions, bottom);

        continueButton.setOnClickListener(v -> {
            if (evidence.isComplete()) finishWith(RESULT_OK);
        });
        skipButton.setOnClickListener(v -> finishWith(RESULT_FIRST_USER));
        exitButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        setContentView(root);
        refresh(gr);
    }

    private Button makeButton(String label, boolean green) {
        Button b = new com.gel.cleaner.GelButton(this);
        b.setAllCaps(false);
        b.setText(label);
        b.setTextSize(15f);
        b.setTextColor(green ? Color.BLACK : Color.WHITE);
        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setColor(green ? 0xFF39FF14 : 0xFFB02020);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(2), 0xFFFFD700);
        b.setBackground(bg);
        return b;
    }

    private void refresh(boolean gr) {
        boolean zin = evidence.hasZoomIn();
        boolean zout = evidence.hasZoomOut();
        expandStatus.setText(gr
                ? "Μεγέθυνση: " + (zin ? "ΕΠΑΛΗΘΕΥΤΗΚΕ" : "Αναμονή")
                : "Zoom in: " + (zin ? "VERIFIED" : "Waiting"));
        contractStatus.setText(gr
                ? "Σμίκρυνση: " + (zout ? "ΕΠΑΛΗΘΕΥΤΗΚΕ" : "Αναμονή")
                : "Zoom out: " + (zout ? "VERIFIED" : "Waiting"));
        expandStatus.setTextColor(zin ? 0xFF39FF14 : Color.WHITE);
        contractStatus.setTextColor(zout ? 0xFF39FF14 : Color.WHITE);
        continueButton.setEnabled(evidence.isComplete());
        continueButton.setAlpha(evidence.isComplete() ? 1f : 0.45f);
    }

    private void finishWith(int code) {
        Intent result = new Intent();
        result.putExtra(EXTRA_DUAL_TOUCH, evidence.hasDualTouch());
        result.putExtra(EXTRA_ZOOM_IN, evidence.hasZoomIn());
        result.putExtra(EXTRA_ZOOM_OUT, evidence.hasZoomOut());
        setResult(code, result);
        finish();
    }

    private final class ZoomSurface extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int firstId = -1;
        private int secondId = -1;
        private float previousSpan = 0f;
        private float previewScale = 1f;

        ZoomSurface(Activity activity) {
            super(activity);
            setBackgroundColor(0xFF101010);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() * 0.5f;
            float cy = getHeight() * 0.5f;
            float r = Math.min(getWidth(), getHeight()) * 0.21f * previewScale;
            paint.setColor(0xFFFFD700);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            canvas.drawCircle(cx, cy, r, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(dp(22));
            canvas.drawText("GEL", cx, cy + dp(7), paint);
        }

        private float span(MotionEvent event, int firstIndex, int secondIndex) {
            return (float) Math.hypot(
                    event.getX(firstIndex) - event.getX(secondIndex),
                    event.getY(firstIndex) - event.getY(secondIndex));
        }

        private void endGesture() {
            evidence.endGesture();
            firstId = secondId = -1;
            previousSpan = 0f;
        }

        @Override public boolean onTouchEvent(MotionEvent event) {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                endGesture();
                return true;
            }
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                endGesture();
                // Exactly two simultaneous, independently identified fingers.
                if (event.getPointerCount() == 2) {
                    firstId = event.getPointerId(0);
                    secondId = event.getPointerId(1);
                    if (firstId != secondId) {
                        previousSpan = span(event, 0, 1);
                        evidence.beginTwoFingerGesture(previousSpan);
                    }
                }
                refresh(AppLang.isGreek(PinchZoomTestActivity.this));
                return true;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() != 2 || firstId < 0 || secondId < 0)
                    return true;
                int a = event.findPointerIndex(firstId);
                int b = event.findPointerIndex(secondId);
                if (a < 0 || b < 0 || a == b) {
                    endGesture();
                    return true;
                }
                float now = span(event, a, b);
                evidence.observeTwoFingerSpan(now);
                if (previousSpan > 1f && Float.isFinite(now) && now > 0f) {
                    float factor = now / previousSpan;
                    // Visual feedback only: this does not determine PASS.
                    previewScale = Math.max(0.6f, Math.min(2.1f, previewScale * factor));
                    invalidate();
                }
                previousSpan = now;
                refresh(AppLang.isGreek(PinchZoomTestActivity.this));
                return true;
            }
            if (action == MotionEvent.ACTION_POINTER_UP
                    || action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL) {
                endGesture();
                return true;
            }
            return true;
        }
    }
}
