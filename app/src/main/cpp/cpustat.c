// GDiolitsis Engine Lab (GEL) — Author & Developer
// cpustat.c — v16.1 Shared JNI Bridge Edition
// Priority chain:
//   1) /proc/stat   RAW (only if REAL, not fake)
//   2) /sys/cpu/... FREQ (universal, true hardware Hz)
//   3) /sys/thermal hybrid
//
// Return format:
//   RAW:     0–100
//   FREQ:    1000–1100  (encoded percent = raw+1000)
//   THERMAL: 2000–2100  (encoded percent = raw+2000)
//   FAIL:    -1

#include <jni.h>
#include <stdio.h>
#include <string.h>

// ------------------------------------------------------
// Helpers
// ------------------------------------------------------
static int clamp_int(int v, int lo, int hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static long clamp_long(long v, long lo, long hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static int read_line(const char *path, char *buf, size_t len) {
    FILE *fp = fopen(path, "r");
    if (!fp) return -1;

    if (!fgets(buf, (int)len, fp)) {
        fclose(fp);
        return -1;
    }

    fclose(fp);
    return 0;
}

static long read_long(const char *path) {
    char buf[64];

    if (read_line(path, buf, sizeof(buf)) != 0)
        return -1;

    size_t length = strlen(buf);

    if (length > 0 &&
        (buf[length - 1] == '\n' || buf[length - 1] == '\r')) {
        buf[length - 1] = 0;
    }

    long value = -1;

    if (sscanf(buf, "%ld", &value) != 1)
        return -1;

    return value;
}

// ======================================================
// 1) RAW /proc/stat ENGINE (with anti-fake logic)
// ======================================================
static long lastIdle = -1;
static long lastTotal = -1;

static int read_cpu_raw(int *outPercent) {

    char line[256];

    if (read_line("/proc/stat", line, sizeof(line)) != 0)
        return -1;

    if (strncmp(line, "cpu", 3) != 0)
        return -1;

    char cpuLabel[5];
    long user;
    long nice;
    long system;
    long idle;
    long iowait;
    long irq;
    long softirq;

    int scanned = sscanf(
            line,
            "%4s %ld %ld %ld %ld %ld %ld %ld",
            cpuLabel,
            &user,
            &nice,
            &system,
            &idle,
            &iowait,
            &irq,
            &softirq
    );

    if (scanned < 5)
        return -1;

    long idleAll = idle + iowait;

    long total =
            user
            + nice
            + system
            + idle
            + iowait
            + irq
            + softirq;

    // First call establishes baseline.
    if (lastIdle < 0 || lastTotal < 0) {
        lastIdle = idleAll;
        lastTotal = total;
        *outPercent = 0;
        return 0;
    }

    long diffIdle = idleAll - lastIdle;
    long diffTotal = total - lastTotal;

    lastIdle = idleAll;
    lastTotal = total;

    // RAW must have significant movement.
    if (diffTotal < 50)
        return -1;

    if (diffIdle == 0 && diffTotal == 0)
        return -1;

    long used = diffTotal - diffIdle;

    if (used < 0)
        used = 0;

    long percent = (used * 100) / diffTotal;
    percent = clamp_long(percent, 0, 100);

    *outPercent = (int)percent;
    return 0;
}

// ======================================================
// 2) UNIVERSAL FREQ ENGINE
// ======================================================
static int detect_cores() {

    int cores = 0;

    for (int index = 0; index < 32; index++) {

        char path[128];

        snprintf(
                path,
                sizeof(path),
                "/sys/devices/system/cpu/cpu%d/cpufreq",
                index
        );

        FILE *fp = fopen(path, "r");

        if (fp) {
            fclose(fp);
            cores++;
        } else if (cores == index) {
            break;
        }
    }

    if (cores <= 0)
        cores = 1;

    return cores;
}

static int read_cpu_freq(int *outPercent) {

    int cores = detect_cores();

    if (cores <= 0)
        return -1;

    long accumulator = 0;
    int valid = 0;

    for (int index = 0; index < cores; index++) {

        char currentPath[160];
        char maximumPath[160];

        snprintf(
                currentPath,
                sizeof(currentPath),
                "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq",
                index
        );

        snprintf(
                maximumPath,
                sizeof(maximumPath),
                "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq",
                index
        );

        long current = read_long(currentPath);
        long maximum = read_long(maximumPath);

        if (current <= 0 || maximum <= 0)
            continue;

        long percent = (current * 100) / maximum;
        percent = clamp_long(percent, 0, 100);

        accumulator += percent;
        valid++;
    }

    if (valid <= 0)
        return -1;

    long average = accumulator / valid;
    average = clamp_long(average, 0, 100);

    *outPercent = (int)average;
    return 0;
}

// ======================================================
// 3) THERMAL ENGINE
// ======================================================
static int read_cpu_thermal(int *outPercent) {

    char type[128];
    char tempPath[128];

    long tempMilli = -1;

    for (int index = 0; index < 32; index++) {

        char typePath[160];

        snprintf(
                typePath,
                sizeof(typePath),
                "/sys/class/thermal/thermal_zone%d/type",
                index
        );

        if (read_line(typePath, type, sizeof(type)) != 0)
            continue;

        size_t length = strlen(type);

        if (length > 0 &&
            (type[length - 1] == '\n' || type[length - 1] == '\r')) {
            type[length - 1] = 0;
        }

        if (!(strstr(type, "cpu")
              || strstr(type, "CPU")
              || strstr(type, "soc")
              || strstr(type, "SOC")
              || strstr(type, "ap")
              || strstr(type, "AP"))) {
            continue;
        }

        snprintf(
                tempPath,
                sizeof(tempPath),
                "/sys/class/thermal/thermal_zone%d/temp",
                index
        );

        tempMilli = read_long(tempPath);

        if (tempMilli > 0)
            break;
    }

    if (tempMilli <= 0)
        return -1;

    double celsius =
            tempMilli > 1000
                    ? tempMilli / 1000.0
                    : tempMilli;

    const double minimumCelsius = 30.0;
    const double maximumCelsius = 90.0;

    double percent;

    if (celsius <= minimumCelsius) {
        percent = 0;
    } else if (celsius >= maximumCelsius) {
        percent = 100;
    } else {
        percent =
                (celsius - minimumCelsius)
                * 100.0
                / (maximumCelsius - minimumCelsius);
    }

    int result = (int)(percent + 0.5);
    result = clamp_int(result, 0, 100);

    *outPercent = result;
    return 0;
}

// ======================================================
// SHARED ENGINE
// ======================================================
static jint get_cpu_usage_encoded() {

    int percent = -1;

    if (read_cpu_raw(&percent) == 0) {
        return clamp_int(percent, 0, 100);
    }

    if (read_cpu_freq(&percent) == 0) {
        return 1000 + clamp_int(percent, 0, 100);
    }

    if (read_cpu_thermal(&percent) == 0) {
        return 2000 + clamp_int(percent, 0, 100);
    }

    return -1;
}

// ======================================================
// JNI ENTRY — CpuRamLiveActivity (backward compatibility)
// ======================================================
JNIEXPORT jint JNICALL
Java_com_gel_cleaner_CpuRamLiveActivity_getCpuUsageNative(
        JNIEnv *env,
        jobject obj
) {
    return get_cpu_usage_encoded();
}

// ======================================================
// JNI ENTRY — CpuStatBridge (widget + shared access)
// ======================================================
JNIEXPORT jint JNICALL
Java_com_gel_cleaner_CpuStatBridge_getCpuUsageNative(
        JNIEnv *env,
        jclass clazz
) {
    return get_cpu_usage_encoded();
}
