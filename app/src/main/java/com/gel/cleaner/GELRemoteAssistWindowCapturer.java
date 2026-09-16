package com.gel.cleaner;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;

import org.webrtc.CapturerObserver;
import org.webrtc.NV21Buffer;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;

/**
 * Captures only the currently resumed GELAutoActivityHook Window.
 *
 * This is deliberately not a screen capturer: if GEL iDoctor is not the
 * foreground Activity, no frame is emitted. PixelCopy therefore forms the
 * privacy boundary for the Remote Assist POC.
 */
public final class GELRemoteAssistWindowCapturer implements VideoCapturer {

    private static final int DEFAULT_WIDTH = 540;
    private static final int DEFAULT_HEIGHT = 960;
    private static final int DEFAULT_FPS = 8;

    private final Object lock = new Object();

    private CapturerObserver observer;
    private HandlerThread workerThread;
    private Handler worker;

    private boolean running;
    private boolean disposed;
    private int targetWidth = DEFAULT_WIDTH;
    private int targetHeight = DEFAULT_HEIGHT;
    private int fps = DEFAULT_FPS;

    private final Runnable captureTick =
            this::captureOneFrame;

    @Override
    public void initialize(
            SurfaceTextureHelper surfaceTextureHelper,
            Context applicationContext,
            CapturerObserver capturerObserver
    ) {
        synchronized (lock) {
            observer = capturerObserver;

            if (workerThread == null) {
                workerThread =
                        new HandlerThread(
                                "gel-remote-window-capture"
                        );
                workerThread.start();
                worker =
                        new Handler(
                                workerThread.getLooper()
                        );
            }
        }
    }

    @Override
    public void startCapture(
            int width,
            int height,
            int framerate
    ) {
        synchronized (lock) {
            if (disposed ||
                    observer == null ||
                    worker == null) {
                return;
            }

            targetWidth =
                    sanitizeEven(
                            width > 0
                                    ? width
                                    : DEFAULT_WIDTH,
                            160,
                            960
                    );

            targetHeight =
                    sanitizeEven(
                            height > 0
                                    ? height
                                    : DEFAULT_HEIGHT,
                            240,
                            1600
                    );

            fps = Math.max(
                    1,
                    Math.min(
                            framerate > 0
                                    ? framerate
                                    : DEFAULT_FPS,
                            12
                    )
            );

            if (running) {
                return;
            }

            running = true;
            worker.removeCallbacks(
                    captureTick
            );
            worker.post(
                    captureTick
            );
        }

        observer.onCapturerStarted(
                true
        );
    }

    @Override
    public void stopCapture() {
        CapturerObserver currentObserver;

        synchronized (lock) {
            if (!running) {
                return;
            }

            running = false;

            if (worker != null) {
                worker.removeCallbacks(
                        captureTick
                );
            }

            currentObserver = observer;
        }

        if (currentObserver != null) {
            currentObserver.onCapturerStopped();
        }
    }

    @Override
    public void changeCaptureFormat(
            int width,
            int height,
            int framerate
    ) {
        synchronized (lock) {
            targetWidth =
                    sanitizeEven(
                            width > 0
                                    ? width
                                    : targetWidth,
                            160,
                            960
                    );

            targetHeight =
                    sanitizeEven(
                            height > 0
                                    ? height
                                    : targetHeight,
                            240,
                            1600
                    );

            fps = Math.max(
                    1,
                    Math.min(
                            framerate > 0
                                    ? framerate
                                    : fps,
                            12
                    )
            );
        }
    }

    @Override
    public void dispose() {
        HandlerThread thread;

        synchronized (lock) {
            if (disposed) {
                return;
            }

            disposed = true;
            running = false;

            if (worker != null) {
                worker.removeCallbacksAndMessages(
                        null
                );
            }

            thread = workerThread;
            worker = null;
            workerThread = null;
            observer = null;
        }

        if (thread != null) {
            thread.quitSafely();
        }
    }

    @Override
    public boolean isScreencast() {
        return true;
    }

    private void captureOneFrame() {
        final GELAutoActivityHook activity =
                GELRemoteAssistRuntime
                        .getActiveActivity();

        final CapturerObserver currentObserver;
        final int maxWidth;
        final int maxHeight;

        synchronized (lock) {
            if (!running ||
                    disposed ||
                    worker == null) {
                return;
            }

            currentObserver = observer;
            maxWidth = targetWidth;
            maxHeight = targetHeight;
        }

        if (activity == null ||
                currentObserver == null) {
            scheduleNext();
            return;
        }

        final Window window =
                activity.getWindow();

        final View decor =
                window != null
                        ? window.peekDecorView()
                        : null;

        if (window == null ||
                decor == null ||
                !decor.isAttachedToWindow()) {
            scheduleNext();
            return;
        }

        final int sourceWidth =
                decor.getWidth();
        final int sourceHeight =
                decor.getHeight();

        if (sourceWidth < 2 ||
                sourceHeight < 2) {
            scheduleNext();
            return;
        }

        final int[] size =
                fitEven(
                        sourceWidth,
                        sourceHeight,
                        maxWidth,
                        maxHeight
                );

        final Bitmap bitmap;

        try {
            bitmap =
                    Bitmap.createBitmap(
                            size[0],
                            size[1],
                            Bitmap.Config.ARGB_8888
                    );
        } catch (Throwable t) {
            scheduleNext();
            return;
        }

        try {
            PixelCopy.request(
                    window,
                    bitmap,
                    copyResult -> {
                        try {
                            if (copyResult ==
                                    PixelCopy.SUCCESS) {

                                byte[] nv21 =
                                        bitmapToNv21(
                                                bitmap
                                        );

                                NV21Buffer buffer =
                                        new NV21Buffer(
                                                nv21,
                                                bitmap.getWidth(),
                                                bitmap.getHeight(),
                                                null
                                        );

                                VideoFrame frame =
                                        new VideoFrame(
                                                buffer,
                                                0,
                                                System.nanoTime()
                                        );

                                currentObserver
                                        .onFrameCaptured(
                                                frame
                                        );

                                frame.release();
                            }
                        } catch (Throwable ignore) {
                            // A single bad frame must never end the session.
                        } finally {
                            try {
                                bitmap.recycle();
                            } catch (Throwable ignore) {}

                            scheduleNext();
                        }
                    },
                    worker
            );
        } catch (Throwable t) {
            try {
                bitmap.recycle();
            } catch (Throwable ignore) {}

            scheduleNext();
        }
    }

    private void scheduleNext() {
        synchronized (lock) {
            if (!running ||
                    disposed ||
                    worker == null) {
                return;
            }

            long delayMs =
                    Math.max(
                            50L,
                            1000L /
                                    Math.max(
                                            1,
                                            fps
                                    )
                    );

            worker.postDelayed(
                    captureTick,
                    delayMs
            );
        }
    }

    private static int sanitizeEven(
            int value,
            int min,
            int max
    ) {
        int bounded =
                Math.max(
                        min,
                        Math.min(
                                max,
                                value
                        )
                );

        if ((bounded & 1) != 0) {
            bounded -= 1;
        }

        return Math.max(
                2,
                bounded
        );
    }

    private static int[] fitEven(
            int sourceWidth,
            int sourceHeight,
            int maxWidth,
            int maxHeight
    ) {
        double scale =
                Math.min(
                        1.0d,
                        Math.min(
                                maxWidth /
                                        (double) sourceWidth,
                                maxHeight /
                                        (double) sourceHeight
                        )
                );

        int width =
                Math.max(
                        2,
                        (int) Math.round(
                                sourceWidth * scale
                        )
                );

        int height =
                Math.max(
                        2,
                        (int) Math.round(
                                sourceHeight * scale
                        )
                );

        if ((width & 1) != 0) {
            width -= 1;
        }

        if ((height & 1) != 0) {
            height -= 1;
        }

        return new int[]{
                Math.max(2, width),
                Math.max(2, height)
        };
    }

    /**
     * ARGB_8888 -> NV21 (Y + interleaved VU).
     *
     * Chroma is averaged per 2x2 block. The conversion uses integer BT.601
     * coefficients, which is sufficient for diagnostic UI streaming.
     */
    private static byte[] bitmapToNv21(
            Bitmap bitmap
    ) {
        final int width =
                bitmap.getWidth();
        final int height =
                bitmap.getHeight();

        int[] pixels =
                new int[
                        width * height
                ];

        bitmap.getPixels(
                pixels,
                0,
                width,
                0,
                0,
                width,
                height
        );

        byte[] out =
                new byte[
                        width * height * 3 / 2
                ];

        int ySize =
                width * height;

        for (int y = 0; y < height; y++) {
            int row = y * width;

            for (int x = 0; x < width; x++) {
                int color =
                        pixels[row + x];

                int r =
                        (color >> 16) & 0xFF;
                int g =
                        (color >> 8) & 0xFF;
                int b =
                        color & 0xFF;

                int yy =
                        ((66 * r +
                                129 * g +
                                25 * b +
                                128) >> 8) + 16;

                out[row + x] =
                        (byte) clamp255(
                                yy
                        );
            }
        }

        int uvIndex =
                ySize;

        for (int y = 0; y < height; y += 2) {
            for (int x = 0; x < width; x += 2) {

                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                int count = 0;

                for (int dy = 0; dy < 2; dy++) {
                    int py = y + dy;
                    if (py >= height) continue;

                    for (int dx = 0; dx < 2; dx++) {
                        int px = x + dx;
                        if (px >= width) continue;

                        int color =
                                pixels[
                                        py * width + px
                                ];

                        sumR +=
                                (color >> 16) & 0xFF;
                        sumG +=
                                (color >> 8) & 0xFF;
                        sumB +=
                                color & 0xFF;
                        count++;
                    }
                }

                int r = sumR /
                        Math.max(1, count);
                int g = sumG /
                        Math.max(1, count);
                int b = sumB /
                        Math.max(1, count);

                int u =
                        ((-38 * r -
                                74 * g +
                                112 * b +
                                128) >> 8) + 128;

                int v =
                        ((112 * r -
                                94 * g -
                                18 * b +
                                128) >> 8) + 128;

                if (uvIndex + 1 < out.length) {
                    out[uvIndex++] =
                            (byte) clamp255(
                                    v
                            );
                    out[uvIndex++] =
                            (byte) clamp255(
                                    u
                            );
                }
            }
        }

        return out;
    }

    private static int clamp255(
            int value
    ) {
        return Math.max(
                0,
                Math.min(
                        255,
                        value
                )
        );
    }
}
