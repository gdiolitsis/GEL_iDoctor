package com.gel.cleaner;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Technician-side settings editor for the CUSTOMER iDoctor.
 *
 * This is intentionally separate from MainActivity's local settings dialog,
 * so remote mode never reads/writes the technician's local preferences by
 * mistake.
 */
public final class GELRemoteSettingsController {

    private GELRemoteSettingsController() {}

    public static void show(
            Activity activity
    ) {

        if (activity == null) {
            return;
        }

        if (!GELRemoteTargetManager
                .isRemoteMode(
                        activity
                )) {

            return;
        }

        final boolean gr =
                AppLang.isGreek(
                        activity
                );

        AlertDialog loading =
                new AlertDialog.Builder(
                        activity
                )
                        .setMessage(
                                gr
                                        ? "Ανάγνωση ρυθμίσεων από τη συσκευή πελάτη..."
                                        : "Reading settings from customer device..."
                        )
                        .setCancelable(
                                true
                        )
                        .create();

        loading.show();

        GELRemoteCommandClient.send(
                activity,
                "GET_IDOCTOR_SETTINGS",
                null,
                new GELRemoteCommandClient.Callback() {

                    @Override
                    public void onCompleted(
                            boolean success,
                            Map<String, Object> result,
                            String message
                    ) {

                        try {
                            loading.dismiss();
                        } catch (Throwable ignore) {}

                        if (!success) {

                            Toast.makeText(
                                    activity,
                                    message != null
                                            ? message
                                            : (
                                            gr
                                                    ? "Δεν διαβάστηκαν οι remote ρυθμίσεις."
                                                    : "Could not load remote settings."
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        buildAndShow(
                                activity,
                                result
                        );
                    }
                }
        );
    }

    private static void buildAndShow(
            Activity activity,
            Map<String, Object> state
    ) {

        final boolean gr =
                AppLang.isGreek(
                        activity
                );

        LinearLayout box =
                new LinearLayout(
                        activity
                );

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        int pad =
                dp(
                        activity,
                        18
                );

        box.setPadding(
                pad,
                pad,
                pad,
                pad
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                0xFF0B0B0B
        );

        bg.setCornerRadius(
                dp(activity, 18)
        );

        bg.setStroke(
                dp(activity, 3),
                0xFFFFD700
        );

        box.setBackground(
                bg
        );

        TextView title =
                new TextView(
                        activity
                );

        title.setText(
                gr
                        ? "REMOTE SETTINGS — ΣΥΣΚΕΥΗ ΠΕΛΑΤΗ"
                        : "REMOTE SETTINGS — CUSTOMER DEVICE"
        );

        title.setTextColor(
                Color.WHITE
        );

        title.setTextSize(
                17f
        );

        title.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setPadding(
                0,
                0,
                0,
                dp(activity, 12)
        );

        box.addView(
                title
        );

        TextView langLabel =
                label(
                        activity,
                        gr
                                ? "Γλώσσα iDoctor"
                                : "iDoctor Language"
                );

        box.addView(
                langLabel
        );

        RadioGroup langGroup =
                new RadioGroup(
                        activity
                );

        langGroup.setOrientation(
                LinearLayout.HORIZONTAL
        );

        RadioButton langEl =
                radio(
                        activity,
                        "Ελληνικά"
                );

        RadioButton langEn =
                radio(
                        activity,
                        "English"
                );

        langGroup.addView(
                langEl
        );

        langGroup.addView(
                langEn
        );

        String currentLang =
                state != null &&
                        state.get("language") != null
                        ? String.valueOf(
                                state.get(
                                        "language"
                                )
                        )
                        : "en";

        langGroup.check(
                "el".equals(currentLang)
                        ? langEl.getId()
                        : langEn.getId()
        );

        box.addView(
                langGroup
        );

        CheckBox pulse =
                new CheckBox(
                        activity
                );

        pulse.setText(
                gr
                        ? "Mini Check — 3 φορές/ημέρα"
                        : "Mini Check — 3/day"
        );

        pulse.setTextColor(
                0xFF00FF7F
        );

        pulse.setChecked(
                asBoolean(
                        state,
                        "pulseEnabled",
                        false
                )
        );

        box.addView(
                pulse
        );

        CheckBox reminder =
                new CheckBox(
                        activity
                );

        reminder.setText(
                gr
                        ? "Guided Optimizer — Υπενθύμιση"
                        : "Guided Optimizer — Reminder"
        );

        reminder.setTextColor(
                0xFF00FF7F
        );

        reminder.setChecked(
                asBoolean(
                        state,
                        "optimizerReminderEnabled",
                        false
                )
        );

        box.addView(
                reminder
        );

        RadioGroup daysGroup =
                new RadioGroup(
                        activity
                );

        daysGroup.setOrientation(
                LinearLayout.HORIZONTAL
        );

        RadioButton d1 =
                radio(
                        activity,
                        gr ? "1 Ημέρα" : "1 Day"
                );

        RadioButton d7 =
                radio(
                        activity,
                        gr ? "1 Εβδομάδα" : "1 Week"
                );

        RadioButton d30 =
                radio(
                        activity,
                        gr ? "1 Μήνας" : "1 Month"
                );

        daysGroup.addView(d1);
        daysGroup.addView(d7);
        daysGroup.addView(d30);

        int days =
                asInt(
                        state,
                        "optimizerReminderDays",
                        7
                );

        daysGroup.check(
                days == 1
                        ? d1.getId()
                        : (
                        days == 30
                                ? d30.getId()
                                : d7.getId()
                )
        );

        setGroupEnabled(
                daysGroup,
                reminder.isChecked()
        );

        reminder.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setGroupEnabled(
                                daysGroup,
                                isChecked
                        )
        );

        box.addView(
                daysGroup
        );

        TextView note =
                new TextView(
                        activity
                );

        note.setText(
                gr
                        ? "Οι αλλαγές εφαρμόζονται στο iDoctor του πελάτη — όχι στη δική σας συσκευή."
                        : "Changes are applied to the customer's iDoctor — not this device."
        );

        note.setTextColor(
                0xFFFFD700
        );

        note.setTextSize(
                12f
        );

        note.setGravity(
                Gravity.CENTER
        );

        note.setPadding(
                0,
                dp(activity, 10),
                0,
                dp(activity, 10)
        );

        box.addView(
                note
        );

        Button cleanCache =
                new Button(
                        activity
                );

        cleanCache.setText(
                gr
                        ? "ΚΑΘΑΡΙΣΜΟΣ CACHE ΤΟΥ iDoctor ΠΕΛΑΤΗ"
                        : "CLEAN CUSTOMER iDoctor CACHE"
        );

        cleanCache.setAllCaps(
                false
        );

        cleanCache.setTextColor(
                0xFF00FF7F
        );

        cleanCache.setBackgroundResource(
                R.drawable.gel_btn_outline_selector
        );

        box.addView(
                cleanCache
        );

        Button apply =
                new Button(
                        activity
                );

        apply.setText(
                gr
                        ? "ΕΦΑΡΜΟΓΗ ΣΤΗ ΣΥΣΚΕΥΗ ΠΕΛΑΤΗ"
                        : "APPLY TO CUSTOMER DEVICE"
        );

        apply.setAllCaps(
                false
        );

        apply.setTextColor(
                Color.WHITE
        );

        apply.setBackgroundResource(
                R.drawable.gel_btn_outline_selector
        );

        box.addView(
                apply
        );

        Button cancel =
                new Button(
                        activity
                );

        cancel.setText(
                gr
                        ? "Άκυρο"
                        : "Cancel"
        );

        cancel.setAllCaps(
                false
        );

        cancel.setTextColor(
                Color.WHITE
        );

        cancel.setBackgroundResource(
                R.drawable.gel_btn_outline_selector
        );

        box.addView(
                cancel
        );

        ScrollView scroll =
                new ScrollView(
                        activity
                );

        scroll.addView(
                box
        );

        AlertDialog dialog =
                new AlertDialog.Builder(
                        activity
                )
                        .setView(
                                scroll
                        )
                        .setCancelable(
                                true
                        )
                        .create();

        cancel.setOnClickListener(
                v -> dialog.dismiss()
        );

        cleanCache.setOnClickListener(
                v -> {

                    cleanCache.setEnabled(
                            false
                    );

                    GELRemoteCommandClient.send(
                            activity,
                            "CLEAN_IDOCTOR_CACHE",
                            null,
                            new GELRemoteCommandClient.Callback() {

                                @Override
                                public void onCompleted(
                                        boolean success,
                                        Map<String, Object> result,
                                        String message
                                ) {

                                    cleanCache.setEnabled(
                                            true
                                    );

                                    Toast.makeText(
                                            activity,
                                            success
                                                    ? (
                                                    gr
                                                            ? "Η cache του iDoctor καθαρίστηκε στη συσκευή πελάτη."
                                                            : "Customer iDoctor cache cleaned."
                                            )
                                                    : (
                                                    message != null
                                                            ? message
                                                            : "Remote cache clean failed."
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );
                }
        );

        apply.setOnClickListener(
                v -> {

                    String language =
                            langGroup.getCheckedRadioButtonId() ==
                                    langEl.getId()
                                    ? "el"
                                    : "en";

                    int selectedDays =
                            daysGroup.getCheckedRadioButtonId() ==
                                    d1.getId()
                                    ? 1
                                    : (
                                    daysGroup.getCheckedRadioButtonId() ==
                                            d30.getId()
                                            ? 30
                                            : 7
                            );

                    Map<String, Object> payload =
                            new HashMap<>();

                    payload.put(
                            "language",
                            language
                    );

                    payload.put(
                            "pulseEnabled",
                            pulse.isChecked()
                    );

                    payload.put(
                            "optimizerReminderEnabled",
                            reminder.isChecked()
                    );

                    payload.put(
                            "optimizerReminderDays",
                            selectedDays
                    );

                    apply.setEnabled(
                            false
                    );

                    GELRemoteCommandClient.send(
                            activity,
                            "APPLY_IDOCTOR_SETTINGS",
                            payload,
                            new GELRemoteCommandClient.Callback() {

                                @Override
                                public void onCompleted(
                                        boolean success,
                                        Map<String, Object> result,
                                        String message
                                ) {

                                    apply.setEnabled(
                                            true
                                    );

                                    if (success) {

                                        dialog.dismiss();

                                        Toast.makeText(
                                                activity,
                                                gr
                                                        ? "Οι ρυθμίσεις εφαρμόστηκαν στη συσκευή πελάτη."
                                                        : "Settings applied to customer device.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                    } else {

                                        Toast.makeText(
                                                activity,
                                                message != null
                                                        ? message
                                                        : (
                                                        gr
                                                                ? "Αποτυχία remote ρυθμίσεων."
                                                                : "Remote settings failed."
                                                ),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                            }
                    );
                }
        );

        dialog.show();
    }

    private static TextView label(
            Activity activity,
            String text
    ) {

        TextView tv =
                new TextView(
                        activity
                );

        tv.setText(
                text
        );

        tv.setTextColor(
                Color.WHITE
        );

        tv.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        tv.setPadding(
                0,
                dp(activity, 8),
                0,
                dp(activity, 4)
        );

        return tv;
    }

    private static RadioButton radio(
            Activity activity,
            String text
    ) {

        RadioButton b =
                new RadioButton(
                        activity
                );

        b.setId(
                android.view.View
                        .generateViewId()
        );

        b.setText(
                text
        );

        b.setTextColor(
                Color.WHITE
        );

        return b;
    }

    private static boolean asBoolean(
            Map<String, Object> state,
            String key,
            boolean fallback
    ) {

        if (state == null) {
            return fallback;
        }

        Object raw =
                state.get(
                        key
                );

        return raw instanceof Boolean
                ? (Boolean) raw
                : fallback;
    }

    private static int asInt(
            Map<String, Object> state,
            String key,
            int fallback
    ) {

        if (state == null) {
            return fallback;
        }

        Object raw =
                state.get(
                        key
                );

        return raw instanceof Number
                ? ((Number) raw).intValue()
                : fallback;
    }

    private static void setGroupEnabled(
            RadioGroup group,
            boolean enabled
    ) {

        group.setEnabled(
                enabled
        );

        for (int i = 0;
             i < group.getChildCount();
             i++) {

            group.getChildAt(i)
                    .setEnabled(
                            enabled
                    );
        }
    }

    private static int dp(
            Activity activity,
            int value
    ) {

        return Math.round(
                value *
                        activity
                                .getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}
