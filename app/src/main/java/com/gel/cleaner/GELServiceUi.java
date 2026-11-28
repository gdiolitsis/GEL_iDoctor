package com.gel.cleaner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

final class GELServiceUi {
    private GELServiceUi() {}

    static void styleButton(Button button, int background, int textColor) {
        button.setBackgroundTintList(null);
        button.setBackgroundResource(background);
        button.setTextColor(textColor);
        int padding = dp(button.getContext(), 12);
        button.setPadding(padding, button.getPaddingTop(), padding, button.getPaddingBottom());
    }

    static Dialog confirmation(Context context, String title, String message,
                               String cancel, String confirm, boolean destructive, Runnable action) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Dialog_NoActionBar);
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(context, 18);
        content.setPadding(padding, padding, padding, padding);

        TextView heading = new TextView(context);
        heading.setText(title);
        heading.setTextColor(0xFFFFD700);
        heading.setTextSize(18f);
        heading.setTypeface(Typeface.DEFAULT_BOLD);
        heading.setGravity(Gravity.CENTER);
        content.addView(heading);

        TextView body = new TextView(context);
        body.setText(message);
        body.setTextColor(Color.WHITE);
        body.setTextSize(15f);
        body.setPadding(0, dp(context, 12), 0, dp(context, 16));
        content.addView(body);

        GelButton confirmButton = new GelButton(context);
        confirmButton.setText(confirm);
        confirmButton.setAllCaps(false);
        styleButton(confirmButton, destructive ? R.drawable.gel_btn_danger_outline
                : R.drawable.gel_btn_green_gold, destructive ? Color.WHITE : Color.BLACK);
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            action.run();
        });
        content.addView(confirmButton, new LinearLayout.LayoutParams(-1, -2));

        GelButton cancelButton = new GelButton(context);
        cancelButton.setText(cancel);
        cancelButton.setAllCaps(false);
        styleButton(cancelButton, R.drawable.gel_btn_outline_selector, Color.WHITE);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(-1, -2);
        cancelParams.topMargin = dp(context, 8);
        content.addView(cancelButton, cancelParams);

        ScrollView scroll = new ScrollView(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xFF101010);
        background.setCornerRadius(dp(context, 14));
        background.setStroke(dp(context, 2), 0xFFFFD700);
        scroll.setBackground(background);
        scroll.setClipToOutline(true);
        scroll.addView(content);
        dialog.setContentView(scroll);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        return dialog;
    }

    static void appendDiagnosticRow(SpannableStringBuilder buffer, GELServiceLog.PdfRow row) {
        int start = buffer.length();
        buffer.append(row.text);
        int end = buffer.length();
        if (row.valueColor != null) {
            int valueStart = row.valueStart;
            if (valueStart < 0) {
                int separator = row.text.indexOf(':');
                if (separator >= 0) valueStart = separator + 1;
            }
            if (valueStart >= 0 && valueStart < row.text.length()) {
                buffer.setSpan(new ForegroundColorSpan(Color.parseColor(row.valueColor)),
                        start + valueStart, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            int severityColor = explicitSeverityColor(row.text);
            if (severityColor != Color.WHITE) {
                buffer.setSpan(new ForegroundColorSpan(severityColor),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        buffer.append('\n');
    }

    private static int explicitSeverityColor(String line) {
        if (line.startsWith("✔ ")) return 0xFF39FF14;
        if (line.startsWith("⚠ ")) return 0xFFFFD966;
        if (line.startsWith("✖ ")) return 0xFFFF5555;
        return Color.WHITE;
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
