package com.gel.cleaner;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/** Also covers buttons inflated by platform/AppCompat dialog templates. */
public final class GelButtonLayout {
    private GelButtonLayout() {}
    public static void fitDialog(Dialog dialog) {
        if (dialog.getWindow() != null) {
            View root = dialog.getWindow().getDecorView();
            fit(root);
            root.post(() -> fit(root));
        }
    }
    public static void fit(View view) {
        if (view instanceof Button && !(view instanceof android.widget.CompoundButton)) {
            TextView button = (TextView) view;
            int unit = Math.round(12 * view.getResources().getDisplayMetrics().density);
            button.setSingleLine(false);
            button.setMaxLines(Integer.MAX_VALUE);
            button.setEllipsize(null);
            button.setHorizontallyScrolling(false);
            button.setIncludeFontPadding(true);
            button.setMinHeight(unit * 4);
            button.setMinimumHeight(unit * 4);
            button.setPadding(button.getPaddingLeft(), Math.max(unit, button.getPaddingTop()),
                    button.getPaddingRight(), Math.max(unit, button.getPaddingBottom()));
            ViewGroup.LayoutParams lp = button.getLayoutParams();
            if (lp != null && lp.height > 0) {
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                button.setLayoutParams(lp);
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) fit(group.getChildAt(i));
        }
    }
}
