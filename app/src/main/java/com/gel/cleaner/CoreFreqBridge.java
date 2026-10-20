// GDiolitsis Engine Lab (GEL) — Author & Developer
// CoreFreqBridge.java
//
// Pure-Java replacement for the former corefreq JNI library.

package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public final class CoreFreqBridge {

    private CoreFreqBridge() {}

    public static String getCoreInfo() {

        StringBuilder out =
                new StringBuilder(
                        "GEL Core Monitor<br><br>"
                );

        boolean found =
                false;

        for (int index = 0; index < 64; index++) {

            String base =
                    "/sys/devices/system/cpu/cpu"
                            + index
                            + "/cpufreq/";

            File dir = new File(base);

            if (!dir.exists()) {
                continue;
            }

            long current =
                    readLong(
                            base + "scaling_cur_freq"
                    );

            long maximum =
                    readLong(
                            base + "cpuinfo_max_freq"
                    );

            if (maximum <= 0L) {
                continue;
            }

            found = true;

            out.append("C")
                    .append(index)
                    .append(": ");

            if (current <= 0L) {

                out.append(
                        "0 MHz   [OFFLINE]<br>"
                );

                continue;
            }

            int mhz =
                    (int) (current / 1000L);

            int percentage =
                    (int) (
                            (current * 100L)
                                    / maximum
                    );

            String state = "OK";

            if (percentage < 5) {
                state = "SUSPECT";
            }

            if (percentage > 80) {
                state = "BOOST";
            }

            if (current == maximum) {
                state = "MAX";
            }

            out.append(mhz)
                    .append(" MHz   [")
                    .append(state)
                    .append("]<br>");
        }

        if (!found) {

            out.append(
                    "CPU frequency data unavailable.<br>"
            );
        }

        return out.toString();
    }

    private static long readLong(
            String path
    ) {

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(path)
                        )
        ) {

            String line =
                    reader.readLine();

            if (line == null) {
                return -1L;
            }

            return Long.parseLong(
                    line.trim()
            );

        } catch (Throwable ignore) {
            return -1L;
        }
    }
}
