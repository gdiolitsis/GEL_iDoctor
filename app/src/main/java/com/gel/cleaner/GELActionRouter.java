package com.gel.cleaner;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Central LOCAL / REMOTE routing point.
 *
 * Every feature that becomes remote-capable should route through here.
 */
public final class GELActionRouter {

    private GELActionRouter() {}

    public static boolean isRemoteTarget(
            Activity activity
    ) {
        return activity != null &&
                GELRemoteTargetManager
                        .isRemoteMode(
                                activity
                        );
    }

    public static void route(
            Activity activity,
            String remoteAction,
            @Nullable Map<String, Object> payload,
            Runnable localAction
    ) {

        if (activity == null) {
            return;
        }

        if (!isRemoteTarget(activity)) {

            if (localAction != null) {
                localAction.run();
            }

            return;
        }

        GELRemoteCommandClient.send(
                activity,
                remoteAction,
                payload,
                new GELRemoteCommandClient.Callback() {

                    @Override
                    public void onQueued(
                            String commandId
                    ) {

                        Toast.makeText(
                                activity,
                                AppLang.isGreek(activity)
                                        ? "Remote εντολή στάλθηκε στη συσκευή πελάτη."
                                        : "Remote command sent to customer device.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onCompleted(
                            boolean success,
                            Map<String, Object> result,
                            String message
                    ) {

                        String text;

                        if (message != null &&
                                !message.trim().isEmpty()) {

                            text =
                                    message;

                        } else {

                            text =
                                    success
                                            ? (
                                            AppLang.isGreek(activity)
                                                    ? "Η remote εντολή ολοκληρώθηκε."
                                                    : "Remote command completed."
                                    )
                                            : (
                                            AppLang.isGreek(activity)
                                                    ? "Η remote εντολή απέτυχε."
                                                    : "Remote command failed."
                                    );
                        }

                        Toast.makeText(
                                activity,
                                text,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    public static void blockUnsupportedRemote(
            Activity activity,
            String featureName
    ) {

        if (activity == null) {
            return;
        }

        Toast.makeText(
                activity,
                AppLang.isGreek(activity)
                        ? "REMOTE DEVICE: Το «" + featureName
                        + "» δεν έχει συνδεθεί ακόμη με τον Remote Action Router. "
                        + "Δεν εκτελέστηκε τίποτα στο δικό σας κινητό."
                        : "REMOTE DEVICE: \"" + featureName
                        + "\" is not remote-enabled yet. "
                        + "Nothing was executed on this phone.",
                Toast.LENGTH_LONG
        ).show();
    }

    public static void sendRemoteAndShowResult(
            Activity activity,
            String remoteAction,
            @Nullable Map<String, Object> payload,
            String title
    ) {

        if (activity == null) {
            return;
        }

        if (!isRemoteTarget(activity)) {
            return;
        }

        Toast.makeText(
                activity,
                AppLang.isGreek(activity)
                        ? "Αποστολή εντολής στη συσκευή πελάτη..."
                        : "Sending command to customer device...",
                Toast.LENGTH_SHORT
        ).show();

        GELRemoteCommandClient.send(
                activity,
                remoteAction,
                payload,
                new GELRemoteCommandClient.Callback() {

                    @Override
                    public void onCompleted(
                            boolean success,
                            Map<String, Object> result,
                            String message
                    ) {

                        if (!success) {

                            Toast.makeText(
                                    activity,
                                    message != null
                                            ? message
                                            : "Remote command failed.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        StringBuilder body =
                                new StringBuilder();

                        if (message != null &&
                                !message.trim().isEmpty()) {

                            body.append(message)
                                    .append("\n\n");
                        }

                        if (result != null) {

                            for (Map.Entry<String, Object> e :
                                    result.entrySet()) {

                                body.append(e.getKey())
                                        .append(": ")
                                        .append(
                                                String.valueOf(
                                                        e.getValue()
                                                )
                                        )
                                        .append("\n");
                            }
                        }

                        showGelRemoteResultDialog(
                                activity,
                                title != null
                                        ? title
                                        : "REMOTE DEVICE",
                                body.toString()
                        );
                    }
                }
        );
    }

    private static void showGelRemoteResultDialog(
            Activity activity,
            String titleText,
            String messageText
    ) {

        if (activity == null) {
            return;
        }

        LinearLayout box =
                new LinearLayout(activity);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        int pad =
                dp(activity, 18);

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
                dp(activity, 16)
        );

        bg.setStroke(
                dp(activity, 2),
                0xFFFFD700
        );

        box.setBackground(bg);

        TextView title =
                new TextView(activity);

        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(
                0,
                0,
                0,
                dp(activity, 12)
        );

        box.addView(title);

        TextView message =
                new TextView(activity);

        message.setText(
                messageText != null
                        ? messageText
                        : ""
        );

        message.setTextColor(
                0xFFE6E6E6
        );

        message.setTextSize(15f);
        message.setLineSpacing(0f, 1.18f);
        message.setGravity(Gravity.START);
        message.setTextIsSelectable(true);
        message.setPadding(
                dp(activity, 2),
                0,
                dp(activity, 2),
                dp(activity, 16)
        );

        box.addView(message);

        Button ok =
                new Button(activity);

        ok.setText("OK");
        ok.setAllCaps(false);
        ok.setTextColor(0xFFFFD700);
        ok.setTextSize(16f);
        ok.setTypeface(Typeface.DEFAULT_BOLD);
        ok.setGravity(Gravity.CENTER);
        ok.setPadding(
                dp(activity, 10),
                dp(activity, 10),
                dp(activity, 10),
                dp(activity, 10)
        );

        GradientDrawable okBg =
                new GradientDrawable();

        okBg.setColor(
                0xFF0B0B0B
        );

        okBg.setCornerRadius(
                dp(activity, 10)
        );

        okBg.setStroke(
                dp(activity, 2),
                0xFFFFD700
        );

        ok.setBackground(okBg);

        box.addView(ok);

        AlertDialog dialog =
                new AlertDialog.Builder(activity)
                        .setView(box)
                        .setCancelable(true)
                        .create();

        ok.setOnClickListener(
                v -> dialog.dismiss()
        );

        dialog.show();

        Window window =
                dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            window.setLayout(
                    (int) (
                            activity
                                    .getResources()
                                    .getDisplayMetrics()
                                    .widthPixels *
                                    0.92f
                    ),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
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
