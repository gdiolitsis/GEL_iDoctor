package com.gel.cleaner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;

/** Text actions grow with translated text and the user's font size. */
public class GelButton extends AppCompatButton {
    public GelButton(Context context) { this(context, null); }
    public GelButton(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.buttonStyle);
    }
    public GelButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        setSingleLine(false);
        setMaxLines(Integer.MAX_VALUE);
        setEllipsize(null);
        setHorizontallyScrolling(false);
        setIncludeFontPadding(true);
        setMinimumHeight(Math.max(getMinimumHeight(), dp(48)));
        setPadding(getPaddingLeft(), Math.max(getPaddingTop(), dp(12)),
                getPaddingRight(), Math.max(getPaddingBottom(), dp(12)));
    }
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Apply after screen/dialog code has assigned its local legacy colours.
        // This changes the presentation only, not any OnClickListener.
        post(() -> GelActionButtonStyle.apply(this));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
    @Override public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null && params.height > 0) {
            setMinimumHeight(Math.max(dp(48), params.height));
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        super.setLayoutParams(params);
    }
    @Override public void setSingleLine(boolean singleLine) { super.setSingleLine(false); }
    @Override public void setMaxLines(int lines) { super.setMaxLines(Integer.MAX_VALUE); }
    @Override public void setEllipsize(android.text.TextUtils.TruncateAt where) { super.setEllipsize(null); }
    @Override public void setIncludeFontPadding(boolean include) { super.setIncludeFontPadding(true); }
    @Override public void setHeight(int pixels) {
        setMinHeight(pixels);
        super.setMaxHeight(Integer.MAX_VALUE);
    }
    @Override public void setMinHeight(int pixels) { super.setMinHeight(Math.max(dp(48), pixels)); }
    @Override public void setMinimumHeight(int pixels) { super.setMinimumHeight(Math.max(dp(48), pixels)); }
    @Override public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, Math.max(dp(12), top), right, Math.max(dp(12), bottom));
    }
}
