// GDiolitsis Engine Lab (GEL) — Author & Developer
// CoreMonitorActivity
//
// Pure-Java core-frequency monitor.
// No project-owned JNI/CMake dependency.

package com.gel.cleaner;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CoreMonitorActivity extends GELAutoActivityHook {

    private TextView txtInfo;

    private volatile boolean monitorRunning =
            false;

    private Thread monitorThread;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_core_monitor
        );

        txtInfo =
                findViewById(
                        R.id.txtCoreInfo
                );

        startLoop();
    }

    private void startLoop() {

        if (monitorRunning) {
            return;
        }

        monitorRunning = true;

        monitorThread =
                new Thread(
                        () -> {

                            while (monitorRunning) {

                                String raw =
                                        CoreFreqBridge
                                                .getCoreInfo();

                                raw =
                                        raw.replace(
                                                "GEL Core Monitor",
                                                ""
                                        );

                                raw =
                                        raw.replace(
                                                "C0:",
                                                "\nC0:"
                                        );

                                final String html =
                                        raw
                                                .replace(
                                                        "[OK]",
                                                        "<font color='#00FF66'>[OK]</font>"
                                                )
                                                .replace(
                                                        "[BOOST]",
                                                        "<font color='#00FF66'>[BOOST]</font>"
                                                )
                                                .replace(
                                                        "[MAX]",
                                                        "<font color='#00FF66'>[MAX]</font>"
                                                )
                                                .replace(
                                                        "[SUSPECT]",
                                                        "<font color='#FF3333'>[SUSPECT]</font>"
                                                )
                                                .replace(
                                                        "[OFFLINE]",
                                                        "<font color='#FF3333'>[OFFLINE]</font>"
                                                );

                                runOnUiThread(
                                        () -> {

                                            if (!monitorRunning
                                                    || isFinishing()
                                                    || isDestroyed()
                                                    || txtInfo == null) {
                                                return;
                                            }

                                            txtInfo.setText(
                                                    Html.fromHtml(
                                                            html,
                                                            Html.FROM_HTML_MODE_LEGACY
                                                    )
                                            );
                                        }
                                );

                                try {

                                    Thread.sleep(
                                            1000L
                                    );

                                } catch (InterruptedException e) {

                                    Thread
                                            .currentThread()
                                            .interrupt();

                                    break;
                                }
                            }
                        },
                        "GEL-CoreMonitor"
                );

        monitorThread.start();
    }

    @Override
    protected void onDestroy() {

        monitorRunning = false;

        Thread thread =
                monitorThread;

        monitorThread = null;

        if (thread != null) {
            thread.interrupt();
        }

        super.onDestroy();
    }
}
