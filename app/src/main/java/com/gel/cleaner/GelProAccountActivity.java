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
            FirebaseAuth.getInstance().signOut(); GelAccountPro.clear(this); GelProEntitlement.clearTesterCache(this); render("");
        });
        action(box, gr ? "Διαγραφή λογαριασμού" : "Delete account", () -> {
            if (busy) return;
            new AlertDialog.Builder(this).setMessage(gr
                    ? "Θα διαγραφεί ο λογαριασμός σύνδεσης και η δωρεάν άδεια PRO, καθώς και τα credits και οι αποθηκευμένες άδειες αναφορών. Η συνδρομή Google Play δεν ακυρώνεται από εδώ."
                    : "Delete your sign-in account, gifted PRO access, report credits and saved report grants? This does not cancel a Google Play subscription.")
                    .setNegativeButton(gr ? "Άκυρο" : "Cancel", null)
                    .setPositiveButton(gr ? "Διαγραφή" : "Delete", (d,w) -> deleteAccount()).show();
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
        button.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = Math.round(12 * getResources().getDisplayMetrics().density);
        box.addView(button, params);
    }
    private void render(String message) {
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
                                        if (task.isSuccessful()) { GelProEntitlement.clearTesterCache(GelProAccountActivity.this); billing.refreshPurchases(); refresh(); }
                                        else render(gr ? "Η σύνδεση απέτυχε. Δοκιμάστε ξανά." : "Sign-in failed. Please retry.");
                                    });
                        } catch (Exception e) { busy = false; render("Google credential could not be read."); }
                    }
                    @Override public void onError(GetCredentialException error) {
                        busy = false; render(gr ? "Η σύνδεση ακυρώθηκε ή δεν είναι διαθέσιμη." : "Sign-in cancelled or unavailable.");
                    }
                });
    }
    private void refresh() {
        if (busy) return; busy = true;
        GelAccountPro.refresh(this, (active, message) -> {
            busy = false;
            if (isFinishing()) return;
            if (accessChoices && giftActivation && active) {
                GelReportStore.refresh(this, (ok, code, reportMessage) -> {
                    if (isFinishing()) return;
                    if (!ok) { render(reportMessage); return; }
                    android.widget.Toast.makeText(this, gr ? "Το δωρεάν FULL ACCESS ενεργοποιήθηκε."
                            : "Free FULL ACCESS activated.", android.widget.Toast.LENGTH_LONG).show();
                    finish();
                });
            } else render(accessChoices && giftActivation && message.isEmpty()
                    ? (gr ? "Δεν έχει απονεμηθεί gift σε αυτόν τον λογαριασμό. Επιλέξτε διαφορετικό λογαριασμό ή GEL PRO μέσω Google Play."
                          : "This account has no gift. Choose another Google account or GEL PRO through Google Play.") : message);
        });
    }
    private void deleteAccount() {
        if (busy) return; busy = true;
        FirebaseFunctions.getInstance("europe-west1").getHttpsCallable("deleteProGoogleAccount")
                .call().addOnCompleteListener(task -> {
                    busy = false;
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut(); GelAccountPro.clear(this); GelProEntitlement.clearTesterCache(this); render("");
                    } else render(gr ? "Συνδεθείτε ξανά με Google και επαναλάβετε τη διαγραφή."
                            : "Sign in with Google again, then retry deletion.");
                });
    }
}
