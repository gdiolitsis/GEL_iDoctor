package com.gel.cleaner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Customer-side visible remote command transport.
 *
 * Runs only while a customer Service Session is CONNECTED.
 * Listens to the parent service_sessions/{sessionId} document, which is
 * already readable by the authenticated customer under the current rules.
 *
 * Android 15 dataSync foreground services have a time budget; this service
 * stops cleanly on timeout. A later transport upgrade can wake commands with
 * FCM instead of holding a long-lived foreground service.
 */
public class GELRemoteCommandService extends Service {

    private static final String CUSTOMER_SESSION_PREFS =
            "GEL_CUSTOMER_SERVICE_SESSION";

    private static final String KEY_SESSION_ID =
            "session_id";

    private static final String KEY_CONNECTED =
            "connected";

    private static final String FUNCTIONS_REGION =
            "europe-west1";

    private static final String SESSIONS_COLLECTION =
            "service_sessions";

    private static final String CHANNEL_ID =
            "gel_remote_service";

    private static final int NOTIFICATION_ID =
            44117;

    private FirebaseAuth firebaseAuth;
    private FirebaseFunctions functions;
    private FirebaseFirestore firestore;

    private FirebaseAuth.AuthStateListener authStateListener;
    private ListenerRegistration sessionListener;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final Handler main =
            new Handler(
                    Looper.getMainLooper()
            );

    private String sessionId;
    private String processingCommandId;

    public static void ensureRunning(
            Context context
    ) {

        if (context == null) {
            return;
        }

        SharedPreferences p =
                context.getSharedPreferences(
                        CUSTOMER_SESSION_PREFS,
                        Context.MODE_PRIVATE
                );

        boolean connected =
                p.getBoolean(
                        KEY_CONNECTED,
                        false
                );

        String sessionId =
                p.getString(
                        KEY_SESSION_ID,
                        null
                );

        if (!connected ||
                sessionId == null ||
                sessionId.trim().isEmpty()) {

            return;
        }

        Intent serviceIntent =
                new Intent(
                        context,
                        GELRemoteCommandService.class
                );

        try {

            ContextCompat
                    .startForegroundService(
                            context,
                            serviceIntent
                    );

        } catch (Throwable ignore) {
        }
    }

    public static void stopRemoteService(
            Context context
    ) {

        if (context == null) {
            return;
        }

        try {
            context.stopService(
                    new Intent(
                            context,
                            GELRemoteCommandService.class
                    )
            );
        } catch (Throwable ignore) {}
    }

    @Override
    public void onCreate() {

        super.onCreate();

        createNotificationChannel();

        startForeground(
                NOTIFICATION_ID,
                buildNotification(
                        "Remote Service Session active"
                )
        );

        firebaseAuth =
                FirebaseAuth.getInstance();

        functions =
                FirebaseFunctions.getInstance(
                        FUNCTIONS_REGION
                );

        firestore =
                FirebaseFirestore.getInstance();

        authStateListener =
                auth -> {

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user != null) {
                        attachSessionListenerIfReady();
                    }
                };

        firebaseAuth
                .addAuthStateListener(
                        authStateListener
                );
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        if (!refreshSession()) {
            stopSelf();
            return START_NOT_STICKY;
        }

        attachSessionListenerIfReady();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(
            Intent intent
    ) {
        return null;
    }

    @Override
    public void onDestroy() {

        removeSessionListener();

        if (firebaseAuth != null &&
                authStateListener != null) {

            firebaseAuth
                    .removeAuthStateListener(
                            authStateListener
                    );
        }

        executor.shutdownNow();

        super.onDestroy();
    }

    @Override
    public void onTimeout(
            int startId,
            int fgsType
    ) {

        stopSelf();
    }

    private boolean refreshSession() {

        SharedPreferences p =
                getSharedPreferences(
                        CUSTOMER_SESSION_PREFS,
                        MODE_PRIVATE
                );

        boolean connected =
                p.getBoolean(
                        KEY_CONNECTED,
                        false
                );

        String stored =
                p.getString(
                        KEY_SESSION_ID,
                        null
                );

        if (!connected ||
                stored == null ||
                stored.trim().isEmpty()) {

            sessionId =
                    null;

            return false;
        }

        sessionId =
                stored.trim();

        return true;
    }

    private void attachSessionListenerIfReady() {

        if (!refreshSession()) {
            stopSelf();
            return;
        }

        FirebaseUser user =
                firebaseAuth != null
                        ? firebaseAuth.getCurrentUser()
                        : null;

        if (user == null ||
                firestore == null) {
            return;
        }

        if (sessionListener != null) {
            return;
        }

        sessionListener =
                firestore
                        .collection(
                                SESSIONS_COLLECTION
                        )
                        .document(
                                sessionId
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        updateNotification(
                                                "Remote session waiting for network"
                                        );

                                        return;
                                    }

                                    if (snapshot == null ||
                                            !snapshot.exists()) {

                                        stopSelf();
                                        return;
                                    }

                                    String status =
                                            snapshot.getString(
                                                    "status"
                                            );

                                    if (!"CONNECTED".equals(status)) {

                                        getSharedPreferences(
                                                CUSTOMER_SESSION_PREFS,
                                                MODE_PRIVATE
                                        )
                                                .edit()
                                                .putBoolean(
                                                        KEY_CONNECTED,
                                                        false
                                                )
                                                .apply();

                                        stopSelf();

                                        return;
                                    }

                                    Object raw =
                                            snapshot.get(
                                                    "remoteCommand"
                                            );

                                    if (!(raw instanceof Map)) {

                                        updateNotification(
                                                "Remote Service Session connected"
                                        );

                                        return;
                                    }

                                    Map<?, ?> command =
                                            (Map<?, ?>) raw;

                                    Object idRaw =
                                            command.get(
                                                    "id"
                                            );

                                    Object statusRaw =
                                            command.get(
                                                    "status"
                                            );

                                    if (!(idRaw instanceof String) ||
                                            statusRaw == null) {
                                        return;
                                    }

                                    String commandId =
                                            (String) idRaw;

                                    String commandStatus =
                                            String.valueOf(
                                                    statusRaw
                                            );

                                    if (!"PENDING".equals(
                                            commandStatus
                                    )) {
                                        return;
                                    }

                                    if (commandId.equals(
                                            processingCommandId
                                    )) {
                                        return;
                                    }

                                    claimAndExecute(
                                            commandId
                                    );
                                }
                        );
    }

    private void removeSessionListener() {

        if (sessionListener != null) {

            sessionListener.remove();

            sessionListener =
                    null;
        }
    }

    private void claimAndExecute(
            String commandId
    ) {

        processingCommandId =
                commandId;

        updateNotification(
                "Receiving technician command..."
        );

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );

        data.put(
                "commandId",
                commandId
        );

        functions
                .getHttpsCallable(
                        "claimRemoteCommand"
                )
                .call(
                        data
                )
                .addOnCompleteListener(
                        task -> {

                            if (!task.isSuccessful() ||
                                    task.getResult() == null) {

                                processingCommandId =
                                        null;

                                updateNotification(
                                        "Remote Service Session connected"
                                );

                                return;
                            }

                            Object raw =
                                    task
                                            .getResult()
                                            .getData();

                            if (!(raw instanceof Map)) {

                                processingCommandId =
                                        null;

                                return;
                            }

                            Map<?, ?> server =
                                    (Map<?, ?>) raw;

                            Object actionRaw =
                                    server.get(
                                            "action"
                                    );

                            if (!(actionRaw instanceof String)) {

                                complete(
                                        commandId,
                                        false,
                                        "Command action missing.",
                                        Collections.emptyMap(),
                                        0
                                );

                                return;
                            }

                            String action =
                                    (String) actionRaw;

                            Map<String, Object> payload =
                                    new HashMap<>();

                            Object payloadRaw =
                                    server.get(
                                            "payload"
                                    );

                            if (payloadRaw instanceof Map) {

                                for (Map.Entry<?, ?> e :
                                        ((Map<?, ?>) payloadRaw)
                                                .entrySet()) {

                                    if (e.getKey() instanceof String) {

                                        payload.put(
                                                (String) e.getKey(),
                                                e.getValue()
                                        );
                                    }
                                }
                            }

                            updateNotification(
                                    "Technician command: " +
                                            action
                            );

                            executor.execute(
                                    () -> {

                                        GELRemoteCommandExecutor.Result result;

                                        try {

                                            result =
                                                    GELRemoteCommandExecutor
                                                            .execute(
                                                                    getApplicationContext(),
                                                                    action,
                                                                    payload
                                                            );

                                        } catch (Throwable t) {

                                            result =
                                                    GELRemoteCommandExecutor
                                                            .Result
                                                            .fail(
                                                                    t.getMessage() != null
                                                                            ? t.getMessage()
                                                                            : "Customer command failed."
                                                            );
                                        }

                                        GELRemoteCommandExecutor.Result finalResult =
                                                result;

                                        main.post(
                                                () -> complete(
                                                        commandId,
                                                        finalResult.success,
                                                        finalResult.message,
                                                        finalResult.data,
                                                        0
                                                )
                                        );
                                    }
                            );
                        }
                );
    }

    private void complete(
            String commandId,
            boolean success,
            String message,
            Map<String, Object> result,
            int attempt
    ) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );

        data.put(
                "commandId",
                commandId
        );

        data.put(
                "status",
                success
                        ? "SUCCESS"
                        : "FAILED"
        );

        data.put(
                "message",
                message != null
                        ? message
                        : ""
        );

        data.put(
                "result",
                result != null
                        ? result
                        : Collections.emptyMap()
        );

        functions
                .getHttpsCallable(
                        "completeRemoteCommand"
                )
                .call(
                        data
                )
                .addOnCompleteListener(
                        task -> {

                            if (!task.isSuccessful() &&
                                    attempt < 3) {

                                main.postDelayed(
                                        () -> complete(
                                                commandId,
                                                success,
                                                message,
                                                result,
                                                attempt + 1
                                        ),
                                        1500L * (attempt + 1)
                                );

                                return;
                            }

                            processingCommandId =
                                    null;

                            updateNotification(
                                    task.isSuccessful()
                                            ? "Remote Service Session connected"
                                            : "Remote result sync failed"
                            );
                        }
                );
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager nm =
                (NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        if (nm == null) {
            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "GEL Remote Service",
                        NotificationManager.IMPORTANCE_LOW
                );

        channel.setDescription(
                "Visible connection used while a customer device is linked to a technician Service Session."
        );

        nm.createNotificationChannel(
                channel
        );
    }

    private Notification buildNotification(
            String text
    ) {

        Notification.Builder builder =
                Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.O
                        ? new Notification.Builder(
                                this,
                                CHANNEL_ID
                        )
                        : new Notification.Builder(
                                this
                        );

        builder
                .setSmallIcon(
                        R.mipmap.ic_launcher
                )
                .setContentTitle(
                        "GEL iDoctor Remote Service"
                )
                .setContentText(
                        text
                )
                .setOngoing(
                        true
                )
                .setOnlyAlertOnce(
                        true
                );

        return builder.build();
    }

    private void updateNotification(
            String text
    ) {

        NotificationManager nm =
                (NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        if (nm != null) {

            nm.notify(
                    NOTIFICATION_ID,
                    buildNotification(
                            text
                    )
            );
        }
    }
}
