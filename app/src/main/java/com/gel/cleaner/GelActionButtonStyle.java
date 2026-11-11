package com.gel.cleaner;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.text.Normalizer;
import java.util.Locale;

/**
 * GEL button palette: ALL red actions get bright red/white; ALL green actions
 * get neon green/black, irrespective of wording. Start/Exit action labels also
 * receive their semantic palette if their former background was neutral.
 *
 * Visual-only: this class MUST NOT alter listeners, result state, permissions,
 * diagnostic control flow, or non-red/non-green action types.
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
                || startsWithWord(normalized, "ΛΗΞΗ ΔΟΚΙΜΗΣ")) return Role.EXIT;
        if (startsWithWord(normalized, "ΕΝΑΡΞΗ")
                || startsWithWord(normalized, "START")) return Role.START;
        return Role.NONE;
    }

    private static boolean startsWithWord(String value, String token) {
        if (value.equals(token)) return true;
        if (!value.startsWith(token) || value.length() <= token.length()) return false;
        char next = value.charAt(token.length());
        return next == ' ' || next == ':' || next == '-' || next == '—';
    }

    /**
     * Use actual opaque button fill, not label or text color, to recognise
     * other red and green buttons. HSV bounds exclude orange/yellow, teal,
     * purple, translucent decoration, and disabled grey states.
     */
    public static Role colorRole(int color) {
        if (Color.alpha(color) < 190) return Role.NONE;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (hsv[1] < 0.47f || hsv[2] < 0.25f) return Role.NONE;
        if (hsv[0] <= 15f || hsv[0] >= 345f) return Role.EXIT;
        if (hsv[0] >= 68f && hsv[0] <= 165f) return Role.START;
        return Role.NONE;
    }

    private static Role drawableRole(Drawable drawable) {
        if (drawable == null) return Role.NONE;
        if (drawable instanceof GradientDrawable) {
            ColorStateList fill = ((GradientDrawable) drawable).getColor();
            if (fill != null) {
                return colorRole(fill.getColorForState(
                        drawable.getState(), fill.getDefaultColor()));
            }
        }
        if (drawable instanceof ColorDrawable) {
            return colorRole(((ColorDrawable) drawable).getColor());
        }
        if (drawable instanceof InsetDrawable) {
            return drawableRole(((InsetDrawable) drawable).getDrawable());
        }
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layers = (LayerDrawable) drawable;
            for (int i = 0; i < layers.getNumberOfLayers(); i++) {
                Role role = drawableRole(layers.getDrawable(i));
                if (role != Role.NONE) return role;
            }
        }
        if (drawable instanceof DrawableContainer) {
            Drawable current = ((DrawableContainer) drawable).getCurrent();
            if (current != drawable) return drawableRole(current);
        }
        return Role.NONE;
    }

    private static Role buttonRole(Button button) {
        Role semantic = roleOf(button.getText());
        if (semantic != Role.NONE) return semantic;
        ColorStateList tint = button.getBackgroundTintList();
        if (tint != null) {
            Role fromTint = colorRole(tint.getColorForState(
                    button.getDrawableState(), tint.getDefaultColor()));
            if (fromTint != Role.NONE) return fromTint;
        }
        return drawableRole(button.getBackground());
    }

    private static int dp(View view, int dps) {
        return Math.round(dps * view.getResources().getDisplayMetrics().density);
    }

    public static boolean apply(Button button) {
        Role role = buttonRole(button);
        if (role == Role.NONE) return false;

        // Keep the user's font scaling and accessible hit area; never cut text.
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
        button.setMinHeight(dp(button, 56));
        button.setMinWidth(dp(button, 104));
        button.setPadding(dp(button, 12), dp(button, 11),
                dp(button, 12), dp(button, 11));

        GradientDrawable background = new GradientDrawable();
        background.setColor(role == Role.EXIT ? EXIT_RED : START_GREEN);
        background.setCornerRadius(dp(button, 10));
        background.setStroke(dp(button, 3), GOLD);
        // Preserve a visible pressed response and never recolor the app's
        // non-button UI (charts, logs, sensor signals or app branding).
        RippleDrawable ripple = new RippleDrawable(
                ColorStateList.valueOf(0x44FFD700), background, null);
        // An old AppCompat/Material tint must not recolor the new solid fill.
        button.setBackgroundTintList(null);
        button.setBackground(ripple);

        ViewGroup.LayoutParams params = button.getLayoutParams();
        if (params != null && params.height > 0) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            button.setLayoutParams(params);
        }
        return true;
    }

    /** Match widths for exactly one red and one green action in a 2-button row. */
    private static void fitPair(ViewGroup group) {
        if (!(group instanceof LinearLayout)
                || ((LinearLayout) group).getOrientation() != LinearLayout.HORIZONTAL
                || group.getChildCount() != 2
                || !(group.getChildAt(0) instanceof Button)
                || !(group.getChildAt(1) instanceof Button)) return;
        Button first = (Button) group.getChildAt(0);
        Button second = (Button) group.getChildAt(1);
        Role a = buttonRole(first), b = buttonRole(second);
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

    /** Called on attachment, dialog fitting and window focus; safe to repeat. */
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
