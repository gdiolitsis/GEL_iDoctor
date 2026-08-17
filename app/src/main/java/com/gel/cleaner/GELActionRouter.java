package com.gel.cleaner;

import android.app.Activity;
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

                        new AlertDialog.Builder(
                                activity
                        )
                                .setTitle(
                                        title != null
                                                ? title
                                                : "REMOTE DEVICE"
                                )
                                .setMessage(
                                        body.toString()
                                )
                                .setPositiveButton(
                                        "OK",
                                        null
                                )
                                .show();
                    }
                }
        );
    }

}
