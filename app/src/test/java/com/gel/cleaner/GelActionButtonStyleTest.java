package com.gel.cleaner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
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
        assertEquals(GelActionButtonStyle.EXIT_RED, buttonFill(exit));
        assertEquals(GelActionButtonStyle.START_GREEN, buttonFill(start));
        assertTrue(exit.getMinHeight() > 0);
        assertEquals(exit.getMinHeight(), start.getMinHeight());
    }

    private int buttonFill(Button button) {
        assertTrue(button.getBackground() instanceof RippleDrawable);
        RippleDrawable ripple = (RippleDrawable) button.getBackground();
        assertTrue(ripple.getDrawable(0) instanceof GradientDrawable);
        return ((GradientDrawable) ripple.getDrawable(0)).getColor().getDefaultColor();
    }

    @Test public void allRedAndGreenButtonsGetUnifiedPaletteRegardlessOfText() {
        Context ctx = themedContext();
        Button no = new GelButton(ctx);
        no.setText("ΟΧΙ");
        GradientDrawable oldRed = new GradientDrawable();
        oldRed.setColor(0xFF8B0000);
        no.setBackground(oldRed);

        Button yes = new GelButton(ctx);
        yes.setText("ΝΑΙ");
        GradientDrawable oldGreen = new GradientDrawable();
        oldGreen.setColor(0xFF0B5F3B);
        yes.setBackground(oldGreen);

        assertTrue(GelActionButtonStyle.apply(no));
        assertTrue(GelActionButtonStyle.apply(yes));
        assertEquals(GelActionButtonStyle.EXIT_RED, buttonFill(no));
        assertEquals(GelActionButtonStyle.START_GREEN, buttonFill(yes));
        assertEquals(Color.WHITE, no.getCurrentTextColor());
        assertEquals(Color.BLACK, yes.getCurrentTextColor());
        assertEquals("ΟΧΙ", no.getText().toString());
        assertEquals("ΝΑΙ", yes.getText().toString());

        Button neutral = new GelButton(ctx);
        neutral.setText("ΛΕΠΤΟΜΕΡΕΙΕΣ");
        GradientDrawable blue = new GradientDrawable();
        blue.setColor(0xFF00BFFF);
        neutral.setBackground(blue);
        assertFalse(GelActionButtonStyle.apply(neutral));
        assertSame(blue, neutral.getBackground());
    }

    @Test public void shadesAndTransparentDecorationsDoNotProduceFalseActionColors() {
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.colorRole(0xFFB00020));
        assertEquals(GelActionButtonStyle.Role.EXIT,
                GelActionButtonStyle.colorRole(0xFFE60000));
        assertEquals(GelActionButtonStyle.Role.START,
                GelActionButtonStyle.colorRole(0xFF0F8A3B));
        assertEquals(GelActionButtonStyle.Role.START,
                GelActionButtonStyle.colorRole(0xFF39FF14));
        assertEquals(GelActionButtonStyle.Role.NONE,
                GelActionButtonStyle.colorRole(0xFFFFD700));
        assertEquals(GelActionButtonStyle.Role.NONE,
                GelActionButtonStyle.colorRole(0xFF00BFFF));
        assertEquals(GelActionButtonStyle.Role.NONE,
                GelActionButtonStyle.colorRole(0x4400FF9C));
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
