package com.gel.cleaner;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Canonical GEL window policy.
 *
 * Android 15+ enforces edge-to-edge for targetSdk 35+.
 * Android 16 large-screen windows can freely resize/rotate.
 *
 * The decor view always draws edge-to-edge, while its content receives
 * the safe system-bar, display-cutout and IME insets. The original
 * padding is preserved so individual screens can keep their own layout
 * spacing without knowing about system bars.
 */
public final class GELWindowInsets {

    private GELWindowInsets() {
        // Utility class.
    }

    public static void install(Activity activity) {

        final Window window = activity.getWindow();
        final View decorView = window.getDecorView();
        final View contentView =
                window.findViewById(android.R.id.content);

        WindowCompat.setDecorFitsSystemWindows(
                window,
                false
        );

        final WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        window,
                        decorView
                );

        // GEL uses a dark surface behind both system bars.
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        final int initialLeft = contentView.getPaddingLeft();
        final int initialTop = contentView.getPaddingTop();
        final int initialRight = contentView.getPaddingRight();
        final int initialBottom = contentView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                contentView,
                (view, windowInsets) -> {

                    final Insets safeInsets =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout()
                            );

                    view.setPadding(
                            initialLeft + safeInsets.left,
                            initialTop + safeInsets.top,
                            initialRight + safeInsets.right,
                            initialBottom + safeInsets.bottom
                    );

                    // Do not consume the insets. Child views that need
                    // specialized handling can still observe them.
                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(contentView);
    }
}
