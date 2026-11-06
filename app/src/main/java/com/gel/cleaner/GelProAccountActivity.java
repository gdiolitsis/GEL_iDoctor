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
    private boolean gr;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        gr = AppLang.isGreek(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int padding = Math.round(20 * getResources().getDisplayMetrics().density);
        box.setPadding(padding, padding, padding, padding);
        box.setBackgroundColor(Color.rgb(16,16,16));
        status = new TextView(this); status.setTextSize(18); status.setTextColor(Color.WHITE);
        box.addView(status);
        billing = new GelBillingManager(this, new GelProBillingListener() {
            @Override public void onGelProActivated(boolean offer) { render(""); }
            @Override public void onPurchasePending(String product) {
                render(gr ? "Η πληρωμή εκκρεμεί." : "Payment pending.");
            }
            @Override public void onBillingError(String message) { render(message); }
        });
        billing.start();
        action(box, gr ? "Συνδρομή PRO μέσω Google Play" : "Subscribe to PRO through Google Play",
                () -> billing.launchGelProPurchase(this));
        action(box, gr ? "Επαναφορά αγορών Play" : "Restore Play purchases", () -> billing.refreshPurchases());
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
        action(box, gr ? "Επιστροφή" : "Back", this::finish);
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
        button.setOnClickListener(v -> action.run());
        box.addView(button, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
    private void render(String message) {
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
                + (GelReportStore.unlimited(this) ? (gr ? "\nΑπεριόριστη εξατομίκευση αγορασμένη." : "\nUnlimited personalisation purchased.") : "")
                + "\n\n" + message);
    }
    private void signIn() {
        if (busy) return;
        int id = getResources().getIdentifier("default_web_client_id", "string", getPackageName());
        if (id == 0) { render(gr ? "Η σύνδεση Google δεν έχει ρυθμιστεί ακόμη." : "Google sign-in is not configured yet."); return; }
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
        GelAccountPro.refresh(this, (active, message) -> { busy = false; if (!isFinishing()) render(message); });
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
