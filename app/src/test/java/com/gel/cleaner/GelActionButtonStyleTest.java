package com.gel.cleaner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/** Presentation-only contract: EN/EL roles, colors and equal adaptive sizing. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class GelActionButtonStyleTest {
    private Context themedContext() {
        return new ContextThemeWrapper(RuntimeEnvironment.getApplication(),
                R.style.Theme_GEL_DarkGold);
    }

    @Test public void exitAndStartRolesCoverGreekEnglishAndTestActions() {
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("ΕΞΟΔΟΣ"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("Έξοδος"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("EXIT"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("ΕΞΟΔΟΣ ΤΕΣΤ"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("EXIT TEST"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("ΤΕΡΜΑΤΙΣΜΟΣ"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("ΛΗΞΗ ΔΟΚΙΜΗΣ"));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.roleOf("END TEST"));
        assertEquals(GelActionButtonStyle.Role.START,
                GelActionButtonStyle.roleOf("ΕΝΑΡΞΗ"));
        assertEquals(GelActionButtonStyle.Role.START,
                GelActionButtonStyle.roleOf("START"));
        assertEquals(GelActionButtonStyle.Role.START,
                GelActionButtonStyle.roleOf("START SPEED TEST"));
        assertEquals(GelActionButtonStyle.Role.NONE,
                GelActionButtonStyle.roleOf("RESTART SERVICE"));
        assertEquals(GelActionButtonStyle.Role.NONE,
                GelActionButtonStyle.roleOf("ΑΠΕΓΚΑΤΑΣΤΑΣΗ"));
        assertEquals(GelActionButtonStyle.Role.NONE,
                GelActionButtonStyle.roleOf("ΑΚΥΡΩΣΗ"));
    }

    @Test public void lab3AndLab4ButtonsShareColorsAndContrast() {
        Button exit = new GelButton(themedContext());
        Button start = new GelButton(themedContext());
        exit.setText("ΕΞΟΔΟΣ");
        start.setText("ΕΝΑΡΞΗ");

        assertTrue(GelActionButtonStyle.apply(exit));
        assertTrue(GelActionButtonStyle.apply(start));
        assertEquals(Color.WHITE, exit.getCurrentTextColor());
        assertEquals(Color.BLACK, start.getCurrentTextColor());
        assertTrue(exit.getBackground() instanceof GradientDrawable);
        assertTrue(start.getBackground() instanceof GradientDrawable);
        assertEquals(GelActionButtonStyle.EXIT_RED,
                ((GradientDrawable) exit.getBackground()).getColor().getDefaultColor());
        assertEquals(GelActionButtonStyle.START_GREEN,
                ((GradientDrawable) start.getBackground()).getColor().getDefaultColor());
        assertTrue(exit.getMinHeight() > 0);
        assertEquals(exit.getMinHeight(), start.getMinHeight());
    }

    @Test public void pairedButtonsGrowEquallyWithoutLosingClickListeners() {
        Context context = themedContext();
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button exit = new GelButton(context);
        Button start = new GelButton(context);
        exit.setText("EXIT");
        start.setText("START");
        final int[] clicked = {0};
        exit.setOnClickListener(v -> clicked[0]++);
        start.setOnClickListener(v -> clicked[0] += 10);

        row.addView(exit, new LinearLayout.LayoutParams(140, 48));
        row.addView(start, new LinearLayout.LayoutParams(90, 54));
        GelActionButtonStyle.fit(row);

        LinearLayout.LayoutParams a = (LinearLayout.LayoutParams) exit.getLayoutParams();
        LinearLayout.LayoutParams b = (LinearLayout.LayoutParams) start.getLayoutParams();
        assertEquals(0, a.width);
        assertEquals(0, b.width);
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, a.height);
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, b.height);
        assertEquals(1f, a.weight, 0.0001f);
        assertEquals(1f, b.weight, 0.0001f);
        exit.performClick();
        start.performClick();
        assertEquals(11, clicked[0]);
    }
}
