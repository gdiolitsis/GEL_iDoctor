package com.gel.cleaner;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Single visual contract for Start / Exit actions, based on Lab 3.
 * This class NEVER changes click listeners or diagnostic control flow.
 * Other action types (delete, retry, warning, buy, etc.) retain their styles.
 */
public final class GelActionButtonStyle {
    public static final int EXIT_RED = 0xFFE60000;
    public static final int START_GREEN = 0xFF39FF14;
    public static final int GOLD = 0xFFFFD700;

    public enum Role { NONE, EXIT, START }

    private GelActionButtonStyle() {}

    public static Role roleOf(CharSequence label) {
        if (label == null) return Role.NONE;
        String normalized = Normalizer.normalize(label.toString().trim(),
                Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
        if (startsWithWord(normalized, "ΕΞΟΔΟΣ")
                || startsWithWord(normalized, "EXIT")
                || startsWithWord(normalized, "ΤΕΡΜΑΤΙΣΜΟΣ")
                || startsWithWord(normalized, "END TEST")
                || startsWithWord(normalized, "ΛΗΞΗ ΔΟΚΙΜΗΣ")) {
            return Role.EXIT;
        }
        if (startsWithWord(normalized, "ΕΝΑΡΞΗ")
                || startsWithWord(normalized, "START")) {
            return Role.START;
        }
        return Role.NONE;
    }

    private static boolean startsWithWord(String value, String token) {
        if (value.equals(token)) return true;
        if (!value.startsWith(token) || value.length() <= token.length()) return false;
        char next = value.charAt(token.length());
        return next == ' ' || next == ':' || next == '-' || next == '—';
    }

    private static int dp(View view, int dps) {
        return Math.round(dps * view.getResources().getDisplayMetrics().density);
    }

    public static boolean apply(Button button) {
        Role role = roleOf(button.getText());
        if (role == Role.NONE) return false;

        // Retain a compact 2-column button row, but let translated text wrap.
        button.setAllCaps(false);
        button.setTextColor(role == Role.EXIT ? Color.WHITE : Color.BLACK);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f);
        button.setGravity(Gravity.CENTER);
        button.setSingleLine(false);
        button.setMaxLines(Integer.MAX_VALUE);
        button.setEllipsize(null);
        button.setHorizontallyScrolling(false);
        button.setIncludeFontPadding(true);
        button.setMinimumHeight(dp(button, 56));
        button.setMinWidth(dp(button, 104));
        button.setPadding(dp(button, 12), dp(button, 11),
                dp(button, 12), dp(button, 11));

        GradientDrawable background = new GradientDrawable();
        background.setColor(role == Role.EXIT ? EXIT_RED : START_GREEN);
        background.setCornerRadius(dp(button, 10));
        background.setStroke(dp(button, 3), GOLD);
        button.setBackground(background);

        ViewGroup.LayoutParams params = button.getLayoutParams();
        if (params != null && params.height > 0) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            button.setLayoutParams(params);
        }
        return true;
    }

    /** Unify heights and visual widths for a horizontal Start/Exit pair. */
    private static void fitPair(ViewGroup group) {
        if (!(group instanceof LinearLayout)
                || ((LinearLayout) group).getOrientation() != LinearLayout.HORIZONTAL
                || group.getChildCount() != 2
                || !(group.getChildAt(0) instanceof Button)
                || !(group.getChildAt(1) instanceof Button)) return;
        Button first = (Button) group.getChildAt(0);
        Button second = (Button) group.getChildAt(1);
        Role a = roleOf(first.getText()), b = roleOf(second.getText());
        if (!((a == Role.EXIT && b == Role.START)
                || (a == Role.START && b == Role.EXIT))) return;
        for (Button button : new Button[]{first, second}) {
            ViewGroup.LayoutParams raw = button.getLayoutParams();
            if (!(raw instanceof LinearLayout.LayoutParams)) continue;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) raw;
            if (lp.width != 0 || lp.weight != 1f
                    || lp.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                lp.width = 0;
                lp.weight = 1f;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                button.setLayoutParams(lp);
            }
        }
    }

    /** Idempotent and safe for activity roots and custom/platform dialogs. */
    public static void fit(View root) {
        if (root == null) return;
        if (root instanceof Button) apply((Button) root);
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++)
                fit(group.getChildAt(i));
            fitPair(group);
        }
    }
}
