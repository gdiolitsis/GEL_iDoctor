package com.gel.cleaner;

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

/**
 * Lab 6: explicit, per-run touch-grid sampling evidence.
 * A fully touched grid is a bounded screen-area test, not a certificate that
 * the entire digitizer has no dead zones, ghost touches, or timing defects.
 */
public class TouchGridTestActivity extends GELAutoActivityHook {
    public static final String EXTRA_TOTAL_ZONES = "lab6_touch_total_zones";
    public static final String EXTRA_REMAINING_ZONES = "lab6_touch_remaining_zones";

    // Retained for compatibility; do NOT consume these process-wide statics as
    // current-session evidence in another activity.
    public static int lastTotalZones = 0;
    public static int lastRemainingZones = 0;
    public static int getTotalZones() { return lastTotalZones; }
    public static int getRemainingZones() { return lastRemainingZones; }

    private TouchGridView gridView;
    private boolean resultCommitted = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        lastTotalZones = Lab6Evidence.TOTAL_ZONES;
        lastRemainingZones = Lab6Evidence.TOTAL_ZONES;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Place controls OUTSIDE the grid: an overlay previously occluded cells.
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setFitsSystemWindows(true);

        gridView = new TouchGridView();
        root.addView(gridView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        // Dedicated adaptive control row: never clip the localized stop label.
        // A fixed-height 72dp footer could clip a button when font scaling or
        // the general GelButton minimum/padding policy increased its height.
        FrameLayout controls = new FrameLayout(this);
        controls.setPadding(dp(12), dp(8), dp(12), dp(8));

        // Use a plain Android Button for this full-screen diagnostic action.
        // Its explicitly set text and contrast remain visible under theme changes.
        Button endButton = new Button(this);
        String stopLabel = AppLang.isGreek(this) ? "ΤΕΡΜΑΤΙΣΜΟΣ" : "END TEST";
        endButton.setText(stopLabel);
        endButton.setContentDescription(stopLabel);
        endButton.setAllCaps(false);
        endButton.setTextColor(Color.WHITE);
        endButton.setTextSize(16f);
        endButton.setTypeface(null, android.graphics.Typeface.BOLD);
        endButton.setGravity(Gravity.CENTER);
        endButton.setSingleLine(false);
        endButton.setHorizontallyScrolling(false);
        endButton.setMinHeight(dp(56));
        endButton.setMinWidth(dp(168));
        endButton.setPadding(dp(18), dp(14), dp(18), dp(14));

        android.graphics.drawable.GradientDrawable endBackground =
                new android.graphics.drawable.GradientDrawable();
        endBackground.setColor(0xFF8B0000);
        endBackground.setCornerRadius(dp(12));
        endBackground.setStroke(dp(3), 0xFFFFD700);
        endButton.setBackground(endBackground);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        controls.addView(endButton, lp);
        root.addView(controls, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        endButton.setOnClickListener(v -> finishTouch(false));
        setContentView(root);
    }

    private void finishTouch(boolean completed) {
        if (resultCommitted) return;
        resultCommitted = true;
        int remaining = gridView == null
                ? Lab6Evidence.TOTAL_ZONES : gridView.countUncleared();
        lastTotalZones = Lab6Evidence.TOTAL_ZONES;
        lastRemainingZones = remaining;
        Intent evidence = new Intent();
        evidence.putExtra(EXTRA_TOTAL_ZONES, Lab6Evidence.TOTAL_ZONES);
        evidence.putExtra(EXTRA_REMAINING_ZONES, remaining);
        setResult(completed && remaining == 0 ? RESULT_OK : RESULT_CANCELED, evidence);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishTouch(false);
    }

    // dp(int) is inherited from GELAutoActivityHook.

    private final class TouchGridView extends View {
        private final boolean[][] cleared =
                new boolean[Lab6Evidence.GRID_ROWS][Lab6Evidence.GRID_COLUMNS];
        private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float cellW;
        private float cellH;
        private float radius;

        TouchGridView() {
            super(TouchGridTestActivity.this);
            dotPaint.setColor(Color.YELLOW);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldW, int oldH) {
            super.onSizeChanged(w, h, oldW, oldH);
            cellW = w / (float) Lab6Evidence.GRID_COLUMNS;
            cellH = h / (float) Lab6Evidence.GRID_ROWS;
            radius = Math.min(cellW, cellH) * 0.28f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int row = 0; row < Lab6Evidence.GRID_ROWS; row++) {
                for (int col = 0; col < Lab6Evidence.GRID_COLUMNS; col++) {
                    if (!cleared[row][col]) {
                        float x = (col + 0.5f) * cellW;
                        float y = (row + 0.5f) * cellH;
                        canvas.drawCircle(x, y, radius, dotPaint);
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (resultCommitted) return true;
            // A tap obscured by an overlay is not trusted test evidence.
            if ((event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
                return true;
            }
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_POINTER_DOWN
                    || action == MotionEvent.ACTION_MOVE) {
                int pointerCount = event.getPointerCount();
                for (int p = 0; p < pointerCount; p++) {
                    if (action == MotionEvent.ACTION_POINTER_DOWN
                            && p != event.getActionIndex()) continue;
                    observePoint(event.getX(p), event.getY(p));
                }
                if (allCleared()) finishTouch(true);
            }
            return true;
        }

        private void observePoint(float x, float y) {
            if (!(cellW > 0f && cellH > 0f)
                    || !Float.isFinite(x) || !Float.isFinite(y)
                    || x < 0f || y < 0f || x >= getWidth() || y >= getHeight()) {
                return;
            }
            int col = (int) (x / cellW);
            int row = (int) (y / cellH);
            if (row >= Lab6Evidence.GRID_ROWS || col >= Lab6Evidence.GRID_COLUMNS) {
                return;
            }
            float dx = x - (col + 0.5f) * cellW;
            float dy = y - (row + 0.5f) * cellH;
            if (dx * dx + dy * dy <= radius * radius && !cleared[row][col]) {
                cleared[row][col] = true;
                invalidate();
            }
        }

        private boolean allCleared() {
            return countUncleared() == 0;
        }

        int countUncleared() {
            int n = 0;
            for (int row = 0; row < Lab6Evidence.GRID_ROWS; row++) {
                for (int col = 0; col < Lab6Evidence.GRID_COLUMNS; col++) {
                    if (!cleared[row][col]) n++;
                }
            }
            return n;
        }
    }
}
