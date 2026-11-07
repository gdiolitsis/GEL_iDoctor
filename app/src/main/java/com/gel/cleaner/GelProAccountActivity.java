package com.gel.cleaner;

import android.os.Bundle;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;

/** Optional Google sign-in for administrator-gifted PRO, not required for normal use. */
public final class GelProAccountActivity extends GELAutoActivityHook {
    private TextView status;
    private boolean busy;
    private GelBillingManager billing;
    public static final String EXTRA_ACCESS_CHOICES = "gel_access_choices";
    private boolean accessChoices;
    private boolean giftActivation;
    private boolean gr;
    private String lastMessage = "";
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        gr = AppLang.isGreek(this);
        accessChoices = getIntent().getBooleanExtra(EXTRA_ACCESS_CHOICES, false);
        ScrollView scroll = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int padding = Math.round(20 * getResources().getDisplayMetrics().density);
        box.setPadding(padding, padding, padding, padding);
        box.setBackgroundColor(Color.rgb(16,16,16));
        status = new TextView(this); status.setTextSize(18); status.setTextColor(Color.WHITE);
        box.addView(status);
        billing = new GelBillingManager(this, new GelProBillingListener() {
            @Override public void onGelProActivated(boolean offer) {
                if (accessChoices && !giftActivation && billing.isGelProActive()) finish();
                else render("");
            }
            @Override public void onPurchasePending(String product) {
                render(gr ? "Η πληρωμή εκκρεμεί." : "Payment pending.");
            }
            @Override public void onBillingError(String message) { render(message); }
        });
        billing.start();
        if (accessChoices) {
            action(box, gr ? "Ενεργοποίηση GEL PRO — 4,99€" : "Activate GEL PRO — €4.99",
                    () -> billing.launchGelProPurchase(this));
            action(box, gr ? "Ενεργοποίηση δωρεάν FULL ACCESS — Gift" : "Activate free FULL ACCESS — Gift", () -> {
                giftActivation = true;
                box.removeAllViews(); box.addView(status);
                action(box, gr ? "Επιλογή λογαριασμού Google" : "Choose Google account", this::signIn);
                action(box, gr ? "Επιστροφή" : "Back", this::finish);
                render("");
                signIn();
            });
        } else {
        action(box, gr ? "Συνδρομή PRO μέσω Google Play" : "Subscribe to PRO through Google Play",
                () -> billing.launchGelProPurchase(this));
        action(box, gr ? "Επαναφορά αγορών Play" : "Restore Play purchases", this::restorePurchases);
        action(box, gr ? "Σύνδεση με Google" : "Sign in with Google", this::signIn);
        action(box, gr ? "Έλεγχος δωρεάν PRO" : "Refresh gifted PRO", this::refresh);
        action(box, gr ? "Αποσύνδεση" : "Sign out", () -> {
            if (busy) return;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || user.isAnonymous()) {
                render(gr ? "Δεν υπάρχει συνδεδεμένος λογαριασμός Google." : "No Google account is signed in.");
                return;
            }
            FirebaseAuth.getInstance().signOut(); GelAccountPro.clear(this); GelProEntitlement.clearTesterCache(this);
            render(gr ? "Αποσυνδεθήκατε από Google. Η συνδρομή Play παραμένει ανεξάρτητη."
                    : "Signed out of Google. Your Play subscription is separate.");
        });
        action(box, gr ? "Διαγραφή λογαριασμού" : "Delete account", () -> {
            if (busy) return;
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(gr ? "Διαγραφή λογαριασμού" : "Delete account").setMessage(gr
                    ? "Θα διαγραφούν ο λογαριασμός και τα συνδεδεμένα δεδομένα της εφαρμογής, τα credits και οι αποθηκευμένες άδειες αναφορών. Αν σας έχει απονεμηθεί gift, θα ενεργοποιηθεί ξανά με σύνδεση στο ίδιο Google account. Η συνδρομή Google Play δεν ακυρώνεται από εδώ."
                    : "Delete your app account and associated data, report credits and saved report grants? A developer-awarded gift will reactivate when you sign in again with the same Google account. This does not cancel a Google Play subscription.")
                    .setNegativeButton(gr ? "Άκυρο" : "Cancel", null)
                    .setPositiveButton(gr ? "Διαγραφή" : "Delete", (d,w) -> deleteAccount()).create();
            dialog.setOnShowListener(d -> {
                if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(
                        new android.graphics.drawable.ColorDrawable(0xFF101010));
                TextView message = dialog.findViewById(android.R.id.message);
                if (message != null) message.setTextColor(Color.WHITE);
                int titleId = getResources().getIdentifier("alertTitle", "id", getPackageName());
                TextView title = dialog.findViewById(titleId);
                if (title != null) title.setTextColor(Color.WHITE);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFD700);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFD700);
            });
            dialog.show();
        });
        }
        action(box, accessChoices ? (gr ? "Όχι τώρα" : "Not now") : (gr ? "Επιστροφή" : "Back"), this::finish);
        scroll.addView(box); setContentView(scroll); render("");
    }
    @Override protected void onResume() {
        super.onResume();
        if (billing != null) billing.refreshPurchases();
    }
    @Override protected void onDestroy() {
        if (billing != null) billing.close();
        super.onDestroy();
    }
    private void action(LinearLayout box, String title, Runnable action) {
        GelButton button = new GelButton(this); button.setText(title); button.setAllCaps(false);
        button.setTextColor(Color.WHITE); button.setTextSize(16);
        button.setSupportBackgroundTintList(null); button.setBackgroundTintList(null);
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setColor(0xFF101010);
        background.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        background.setStroke(Math.round(2 * getResources().getDisplayMetrics().density), 0xFFFFD700);
        button.setBackground(background);
        button.setOnClickListener(v -> {
            if (busy) {
                android.widget.Toast.makeText(this, gr ? "Μια ενέργεια βρίσκεται σε εξέλιξη…"
                        : "An account action is in progress…", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            action.run();
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = Math.round(12 * getResources().getDisplayMetrics().density);
        box.addView(button, params);
    }
    private void render(String message) {
        // Quiet billing callbacks must not erase an explicit account action's result.
        if (message != null && !message.isEmpty()) lastMessage = message;
        message = lastMessage;
        if (accessChoices) {
            status.setText((giftActivation
                    ? (gr ? "Δωρεάν FULL ACCESS\n\nΣυνδεθείτε με τον Google λογαριασμό στον οποίο έχει απονεμηθεί το gift. Η απλή δήλωση email δεν παρέχει πρόσβαση."
                          : "Free FULL ACCESS\n\nSign in with the Google account awarded the gift. Entering an email alone does not grant access.")
                    : (gr ? "GEL PRO\n\nΕπιλέξτε αγορά πρόσβασης ενός μήνα ή ενεργοποίηση gift. Η δωρεάν FULL ACCESS πρόσβαση ισχύει μόνο για λογαριασμούς που έχει επιλέξει ο δημιουργός."
                          : "GEL PRO\n\nChoose one-month access or activate a gift. Free FULL ACCESS is only available to accounts selected by the developer."))
                    + "\n\n" + message);
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String identity = user != null && !user.isAnonymous() ? user.getEmail()
                : (gr ? "Δεν έχετε συνδεθεί με Google." : "Not signed in with Google.");
        status.setText(identity + "\n\n" + (GelProEntitlement.isPaidProActive(this)
                ? (gr ? "Συνδρομή PRO ενεργή μέσω Google Play." : "Paid PRO active through Google Play.")
                : "") + "\n" + (GelAccountPro.active(this)
                ? (gr ? "Δωρεάν PRO ενεργό — χωρίς συνδρομή." : "Gifted PRO active — no subscription.")
                : (gr ? "Δεν υπάρχει επιβεβαιωμένη δωρεάν άδεια PRO." : "No verified gifted PRO access."))
                + "\n\n" + (gr ? "Η δωρεάν πρόσβαση αποδίδεται από τον δημιουργό και επανελέγχεται online. Οι αγορές Play λειτουργούν ξεχωριστά."
                : "Gift access is assigned by the developer and periodically checked online. Play purchases work separately.")
                + "\n\n" + (gr ? "Διαθέσιμα credits αναφορών: " : "Available report credits: ")
                + GelReportStore.credits(this)
                + (GelReportStore.unlimited(this) ? (gr ? "\nΑπεριόριστη εξατομίκευση διαθέσιμη." : "\nUnlimited personalisation available.") : "")
                + "\n\n" + message);
    }
    private void restorePurchases() {
        if (busy) return;
        busy = true;
        render(gr ? "Έλεγχος αγορών Google Play…" : "Checking Google Play purchases…");
        final android.os.Handler timeout = new android.os.Handler(android.os.Looper.getMainLooper());
        final boolean[] completed = {false};
        timeout.postDelayed(() -> {
            if (completed[0] || isFinishing()) return;
            completed[0] = true; busy = false;
            render(gr ? "Ο έλεγχος καθυστέρησε. Ελέγξτε τη σύνδεση και δοκιμάστε ξανά."
                    : "Restore timed out. Check your connection and retry.");
        }, 45000);
        billing.restorePurchases((ok, message) -> runOnUiThread(() -> {
            if (completed[0] || isFinishing()) return;
            completed[0] = true; busy = false;
            render(message);
        }));
    }
    private void signIn() {
        if (busy) return;
        // Direct reference keeps the generated OAuth resource in shrunk release APKs.
        int id = R.string.default_web_client_id;
        busy = true;
        render(gr ? "Επιλέξτε λογαριασμό Google…" : "Choose a Google account…");
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(
                new GetSignInWithGoogleOption.Builder(getString(id)).build()).build();
        CredentialManager.create(this).getCredentialAsync(this, request, null,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override public void onResult(GetCredentialResponse response) {
                        try {
                            if (!GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(response.getCredential().getType())) {
                                busy = false; render("Unsupported credential."); return;
                            }
                            String token = GoogleIdTokenCredential.createFrom(response.getCredential().getData()).getIdToken();
                            FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(token, null))
                                    .addOnCompleteListener(task -> {
                                        busy = false; GelAccountPro.clear(GelProAccountActivity.this);
                                        if (task.isSuccessful()) {
                                            render(gr ? "Η σύνδεση Google ολοκληρώθηκε." : "Google sign-in complete.");
                                            GelProEntitlement.clearTesterCache(GelProAccountActivity.this);
                                            billing.refreshPurchases(); refresh();
                                        } else {
                                            String code = task.getException() instanceof com.google.firebase.auth.FirebaseAuthException
                                                    ? ((com.google.firebase.auth.FirebaseAuthException) task.getException()).getErrorCode() : "CONNECTION";
                                            render((gr ? "Η σύνδεση απέτυχε: " : "Google sign-in failed: ") + code);
                                        }
                                    });
                        } catch (Exception e) { busy = false; render("Google credential could not be read."); }
                    }
                    @Override public void onError(GetCredentialException error) {
                        busy = false; render(gr ? "Η σύνδεση ακυρώθηκε ή δεν είναι διαθέσιμη." : "Sign-in cancelled or unavailable.");
                    }
                });
    }
    private void refresh() {
        if (busy) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            render(gr ? "Συνδεθείτε πρώτα με Google για έλεγχο της δωρεάν πρόσβασης."
                    : "Sign in with Google first to check gifted access.");
            return;
        }
        busy = true;
        render(gr ? "Έλεγχος δωρεάν πρόσβασης…" : "Checking gifted access…");
        final boolean[] completed = {false};
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (completed[0] || isFinishing() || isDestroyed()) return;
            completed[0] = true; busy = false;
            render(gr ? "Ο έλεγχος καθυστέρησε. Δοκιμάστε ξανά."
                    : "Gift access check timed out. Please retry.");
        }, 45000);
        GelAccountPro.refresh(this, (active, message) -> {
            if (completed[0] || isFinishing() || isDestroyed()) return;
            completed[0] = true; busy = false;
            if (accessChoices && giftActivation && active) {
                GelReportStore.refresh(this, (ok, code, reportMessage) -> {
                    if (isFinishing()) return;
                    if (!ok) { render(reportMessage); return; }
                    android.widget.Toast.makeText(this, gr ? "Το δωρεάν FULL ACCESS ενεργοποιήθηκε."
                            : "Free FULL ACCESS activated.", android.widget.Toast.LENGTH_LONG).show();
                    finish();
                });
            } else render(active && message.isEmpty()
                    ? (gr ? "Η δωρεάν FULL ACCESS πρόσβαση επιβεβαιώθηκε." : "Gifted FULL ACCESS verified.")
                    : message.isEmpty()
                    ? (gr ? "Δεν έχει απονεμηθεί gift σε αυτόν τον λογαριασμό. Επιλέξτε διαφορετικό λογαριασμό ή GEL PRO μέσω Google Play."
                          : "This account has no gift. Choose another Google account or GEL PRO through Google Play.") : message);
        });
    }
    private void deleteAccount() {
        if (busy) return;
        final FirebaseUser owner = FirebaseAuth.getInstance().getCurrentUser();
        if (owner == null || owner.isAnonymous()) {
            render(gr ? "Δεν υπάρχει συνδεδεμένος λογαριασμός Google για διαγραφή."
                    : "No Google account is signed in for deletion.");
            return;
        }
        busy = true;
        render(gr ? "Επιβεβαιώστε τον ίδιο λογαριασμό Google για τη διαγραφή…"
                : "Confirm the same Google account to delete your app account…");
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(
                new GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id)).build()).build();
        try {
            CredentialManager.create(this).getCredentialAsync(this, request, null,
                    ContextCompat.getMainExecutor(this),
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override public void onResult(GetCredentialResponse response) {
                            if (!deletionOwnerCurrent(owner)) return;
                            try {
                                if (!GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(
                                        response.getCredential().getType())) {
                                    deletionFailed(gr ? "Μη υποστηριζόμενη επαλήθευση. Δεν έγινε διαγραφή."
                                            : "Unsupported verification. Account was not deleted.");
                                    return;
                                }
                                String token = GoogleIdTokenCredential.createFrom(
                                        response.getCredential().getData()).getIdToken();
                                // Reauthenticate the captured user; never sign in as a different account for deletion.
                                owner.reauthenticate(GoogleAuthProvider.getCredential(token, null))
                                        .addOnCompleteListener(authTask -> {
                                            if (!deletionOwnerCurrent(owner)) return;
                                            if (!authTask.isSuccessful()) {
                                                deletionFailed(gr ? "Η επαλήθευση απέτυχε. Επιλέξτε τον ίδιο Google λογαριασμό. Δεν έγινε διαγραφή."
                                                        : "Verification failed. Choose the same Google account. Account was not deleted.");
                                                return;
                                            }
                                            // The callable must receive a fresh auth_time, not the previously cached ID token.
                                            owner.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                                if (!deletionOwnerCurrent(owner)) return;
                                                if (!tokenTask.isSuccessful()) {
                                                    deletionFailed(gr ? "Η ανανέωση σύνδεσης απέτυχε. Δεν έγινε διαγραφή. Δοκιμάστε ξανά."
                                                            : "Session refresh failed. Account was not deleted. Please retry.");
                                                    return;
                                                }
                                                performAccountDeletion(owner);
                                            });
                                        });
                            } catch (Exception error) {
                                deletionFailed(gr ? "Η επαλήθευση Google απέτυχε. Δεν έγινε διαγραφή."
                                        : "Google verification failed. Account was not deleted.");
                            }
                        }
                        @Override public void onError(GetCredentialException error) {
                            if (!deletionOwnerCurrent(owner)) return;
                            deletionFailed(gr ? "Η επαλήθευση ακυρώθηκε ή δεν είναι διαθέσιμη. Δεν έγινε διαγραφή."
                                    : "Verification cancelled or unavailable. Account was not deleted.");
                        }
                    });
        } catch (Exception error) {
            deletionFailed(gr ? "Η επαλήθευση Google δεν είναι διαθέσιμη. Δεν έγινε διαγραφή."
                    : "Google verification unavailable. Account was not deleted.");
        }
    }
    private boolean deletionOwnerCurrent(FirebaseUser owner) {
        if (isFinishing() || isDestroyed()) return false;
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if (current != null && owner.getUid().equals(current.getUid())) return true;
        deletionFailed(gr ? "Ο συνδεδεμένος λογαριασμός άλλαξε. Η διαγραφή διακόπηκε."
                : "The signed-in account changed. Deletion stopped.");
        return false;
    }
    private void deletionFailed(String message) {
        busy = false;
        if (!isFinishing() && !isDestroyed()) render(message);
    }
    private void performAccountDeletion(FirebaseUser owner) {
        if (!deletionOwnerCurrent(owner)) return;
        render(gr ? "Διαγραφή λογαριασμού…" : "Deleting app account…");
        FirebaseFunctions.getInstance("europe-west1").getHttpsCallable("deleteProGoogleAccount")
                .call().addOnCompleteListener(task -> {
                    if (isFinishing() || isDestroyed()) return;
                    busy = false;
                    if (task.isSuccessful()) {
                        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                        if (current == null || owner.getUid().equals(current.getUid())) {
                            FirebaseAuth.getInstance().signOut();
                            GelAccountPro.clear(this);
                            GelProEntitlement.clearTesterCache(this);
                        }
                        render(gr ? "Ο λογαριασμός διαγράφηκε. Το gift επιστρέφει με νέα σύνδεση στο ίδιο Google account. Η συνδρομή Play δεν ακυρώθηκε."
                                : "Account deleted. Your gift returns when you sign in again with the same Google account. Your Play subscription was not cancelled.");
                    } else {
                        String code = task.getException() instanceof com.google.firebase.functions.FirebaseFunctionsException
                                ? ((com.google.firebase.functions.FirebaseFunctionsException) task.getException()).getCode().name() : "CONNECTION";
                        render((gr ? "Η διαγραφή δεν ολοκληρώθηκε. Δοκιμάστε ξανά. Κωδικός: "
                                : "Deletion did not complete. Please retry. Code: ") + code);
                    }
                });
    }
}
