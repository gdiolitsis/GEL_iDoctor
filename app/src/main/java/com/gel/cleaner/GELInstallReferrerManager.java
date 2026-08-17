// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELInstallReferrerManager.java
// Deferred Smart Service QR recovery after Google Play installation.

package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.util.concurrent.atomic.AtomicBoolean;

final class GELInstallReferrerManager {

    private static final String PREFS =
            "GEL_SMART_SERVICE_QR";

    private static final String KEY_REFERRER_CHECKED =
            "install_referrer_checked";

    private static final AtomicBoolean CHECK_IN_PROGRESS =
            new AtomicBoolean(false);

    interface Callback {
        void onServicePayload(String payload);
    }

    private GELInstallReferrerManager() {
    }

    static void checkOnce(
            Context context,
            Callback callback
    ) {

        if (context == null) {
            return;
        }

        Context appContext = context.getApplicationContext();

        SharedPreferences prefs =
                appContext.getSharedPreferences(
                        PREFS,
                        Context.MODE_PRIVATE
                );

        if (prefs.getBoolean(KEY_REFERRER_CHECKED, false)) {
            return;
        }

        if (!CHECK_IN_PROGRESS.compareAndSet(false, true)) {
            return;
        }

        final InstallReferrerClient client;

        try {
            client = InstallReferrerClient.newBuilder(appContext).build();
        } catch (Throwable ignored) {
            CHECK_IN_PROGRESS.set(false);
            return;
        }

        try {

            client.startConnection(
                    new InstallReferrerStateListener() {

                        @Override
                        public void onInstallReferrerSetupFinished(int responseCode) {

                            try {

                                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {

                                    ReferrerDetails details =
                                            client.getInstallReferrer();

                                    String raw =
                                            details != null
                                                    ? details.getInstallReferrer()
                                                    : null;

                                    String payload =
                                            GELSmartServiceLink.smartPayloadFromInstallReferrer(raw);

                                    // A successful Play response is stable for this installation.
                                    // Mark as checked so we do not query the service every launch.
                                    prefs.edit()
                                            .putBoolean(KEY_REFERRER_CHECKED, true)
                                            .apply();

                                    if (payload != null && callback != null) {
                                        callback.onServicePayload(payload);
                                    }
                                }

                            } catch (Throwable ignored) {
                                // Do not mark checked when the read itself fails.
                                // A later foreground launch may retry.
                            } finally {
                                try {
                                    client.endConnection();
                                } catch (Throwable ignored) {
                                }
                                CHECK_IN_PROGRESS.set(false);
                            }
                        }

                        @Override
                        public void onInstallReferrerServiceDisconnected() {
                            CHECK_IN_PROGRESS.set(false);
                        }
                    }
            );

        } catch (Throwable ignored) {

            try {
                client.endConnection();
            } catch (Throwable ignored2) {
            }

            CHECK_IN_PROGRESS.set(false);
        }
    }
}
