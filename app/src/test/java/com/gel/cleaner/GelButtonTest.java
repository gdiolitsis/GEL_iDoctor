package com.gel.cleaner;

import android.content.Context;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.util.Locale;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class GelButtonTest {
    private Context context(float fontScale, String language) {
        Configuration config = new Configuration(RuntimeEnvironment.getApplication().getResources().getConfiguration());
        config.fontScale = fontScale; config.setLocale(Locale.forLanguageTag(language));
        return new ContextThemeWrapper(RuntimeEnvironment.getApplication().createConfigurationContext(config), R.style.Theme_GEL_DarkGold);
    }
    private void assertFits(Button button) {
        assertNotNull(button.getLayout());
        int lines = button.getLayout().getLineCount();
        assertTrue("Text is clipped: " + button.getText(), button.getLayout().getLineBottom(lines-1)
                + button.getCompoundPaddingTop() + button.getCompoundPaddingBottom() <= button.getMeasuredHeight());
        assertTrue(button.getMeasuredHeight() >= Math.round(48 * button.getResources().getDisplayMetrics().density));
        assertNull(button.getEllipsize());
    }
    @Test public void programmaticActionsGrowAtLargeFontsAndNarrowWidths() {
        for (String language : new String[]{"el","en"}) for(float scale : new float[]{1f,1.5f,2f}) {
            Context context = context(scale, language);
            LinearLayout root = new LinearLayout(context); root.setOrientation(LinearLayout.VERTICAL);
            GelButton button = new GelButton(context);
            button.setText(language.equals("el") ? "ΕΝΑΡΞΗ ΕΛΕΓΧΟΥ ΤΑΧΥΤΗΤΑΣ INTERNET" : "START INTERNET SPEED TEST");
            button.setTextSize(20); button.setSingleLine(true); button.setMaxLines(1);
            button.setMinHeight(0); button.setPadding(12,0,12,0);
            root.addView(button,new LinearLayout.LayoutParams(240,54));
            root.measure(View.MeasureSpec.makeMeasureSpec(240,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            root.layout(0,0,root.getMeasuredWidth(),root.getMeasuredHeight());
            assertFits(button);
        }
    }
    @Test public void allMainMenuButtonsFitWithGreekEnglishAndLargeFonts() {
        for (String language : new String[]{"el","en"}) for (float scale : new float[]{1f,2f}) {
            Context context=context(scale,language);
            View root=LayoutInflater.from(context).inflate(R.layout.activity_main,null);
            int width=Math.round(320*context.getResources().getDisplayMetrics().density);
            root.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1400,View.MeasureSpec.EXACTLY));
            root.layout(0,0,width,1400); inspect(root);
        }
    }
    private void inspect(View view) {
        if(view instanceof Button && view.getVisibility()==View.VISIBLE) assertFits((Button)view);
        if(view instanceof ViewGroup) for(int i=0;i<((ViewGroup)view).getChildCount();i++) inspect(((ViewGroup)view).getChildAt(i));
    }
}
