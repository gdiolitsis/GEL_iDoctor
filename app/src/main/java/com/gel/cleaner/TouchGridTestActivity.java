package com.gel.cleaner;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * ============================================================
 * LAB 6 — Display / Touch Grid Test
 * FINAL — TEST ONLY (NO LOGS HERE)
 * ============================================================
 */
public class TouchGridTestActivity extends GELAutoActivityHook {

    // ========================================================
    // STATIC RESULTS (READ BY ManualTestsActivity)
    // ========================================================
    public static final String EXTRA_TOTAL_ZONES = "lab6_total_zones";
    public static final String EXTRA_REMAINING_ZONES = "lab6_remaining_zones";
    public static int lastTotalZones = 0;
    public static int lastRemainingZones = 0;

    public static int getTotalZones() {
        return lastTotalZones;
    }

    public static int getRemainingZones() {
        return lastRemainingZones;
    }

    // ========================================================
    // ACTIVITY
    // ========================================================
    private TouchGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // A new session must not inherit the previous grid's static counters.
        lastTotalZones = 0;
        lastRemainingZones = 0;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        gridView = new TouchGridView(this);
        root.addView(gridView);

        Button endButton = new com.gel.cleaner.GelButton(this);
        endButton.setText(AppLang.isGreek(this) ? "ΤΕΛΟΣ ΤΕΣΤ" : "END TEST");
        endButton.setAllCaps(false);
        endButton.setTextColor(Color.WHITE);
        endButton.setTypeface(null, android.graphics.Typeface.BOLD);
        android.graphics.drawable.GradientDrawable endBackground =
                new android.graphics.drawable.GradientDrawable();
        endBackground.setColor(0xFFE60000);
        endBackground.setCornerRadius(dp(10));
        endBackground.setStroke(dp(2), 0xFFFFD700);
        endButton.setBackground(endBackground);

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = dp(20);
        endButton.setLayoutParams(lp);

        endButton.setOnClickListener(v -> finishTouchGrid(false));

        root.addView(endButton);
        setContentView(root);
    }

    private void finishTouchGrid(boolean allTouched) {
        int total = gridView.getTotalZones();
        int remaining = gridView.countUncleared();
        lastTotalZones = total;
        lastRemainingZones = remaining;
        Intent result = new Intent();
        result.putExtra(EXTRA_TOTAL_ZONES, total);
        result.putExtra(EXTRA_REMAINING_ZONES, remaining);
        setResult(allTouched && remaining == 0 ? RESULT_OK : RESULT_CANCELED, result);
        finish();
    }

    public int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }

    // ========================================================
    // TOUCH GRID VIEW
    // ========================================================
    private class TouchGridView extends View {

        private final int COLS = 8;
        private final int ROWS = 12;

        private boolean[][] cleared;
        private Paint dotPaint;

        private float cellW, cellH;
        private float radius;

        TouchGridView(Activity ctx) {
            super(ctx);
            cleared = new boolean[ROWS][COLS];
            dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dotPaint.setColor(Color.YELLOW);
        }

        int getTotalZones() {
            return ROWS * COLS;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            cellW = w / (float) COLS;
            cellH = h / (float) ROWS;
            radius = Math.min(cellW, cellH) * 0.25f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (!cleared[r][c]) {
                        float cx = c * cellW + cellW / 2f;
                        float cy = r * cellH + cellH / 2f;
                        canvas.drawCircle(cx, cy, radius, dotPaint);
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_MOVE) {

                int c = (int) (event.getX() / cellW);
                int r = (int) (event.getY() / cellH);

                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    if (!cleared[r][c]) {
                        cleared[r][c] = true;
                        invalidate();

                        if (allCleared()) {
                            finishTouchGrid(true);
                        }
                    }
                }
                return true;
            }
            return false;
        }

        private boolean allCleared() {
            for (int r = 0; r < ROWS; r++)
                for (int c = 0; c < COLS; c++)
                    if (!cleared[r][c]) return false;
            return true;
        }

        int countUncleared() {
            int n = 0;
            for (int r = 0; r < ROWS; r++)
                for (int c = 0; c < COLS; c++)
                    if (!cleared[r][c]) n++;
            return n;
        }
    }
}
