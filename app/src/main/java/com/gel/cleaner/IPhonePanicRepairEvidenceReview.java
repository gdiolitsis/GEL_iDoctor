package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exact-model / exact-panic-clue historical evidence triage, NOT a component
 * diagnosis, success-probability model or recommendation to replace a part.
 *
 * Published forum attempts are often sequential interventions on ONE phone.
 * A success plus an earlier failed swap in a single incident never counts
 * as two independently corroborating successes. Every output is labeled as
 * SELF-REPORTED. Apple diagnostics are a separate, externally observed input.
 */
public final class IPhonePanicRepairEvidenceReview {
    private IPhonePanicRepairEvidenceReview() {}

    public enum Status {
        NO_MATCH,
        UNRESOLVED_OR_PARTIAL_ONLY,
        ONLY_REPORTED_FAILURES,
        MIXED_OR_RECURRING_OUTCOMES,
        ONE_SELF_REPORTED_SUCCESS,
        MULTIPLE_SELF_REPORTED_SUCCESSES
    }

    /** Each action is grouped by incident, never by panic-file occurrences. */
    public static final class ActionHistory {
        public final String actionKey;
        public final int incidents;
        public final int successOnlyIncidents;
        public final int failureOnlyIncidents;
        public final int mixedIncidents;
        public final int partialOrUnresolvedIncidents;

        private ActionHistory(String key, int all, int success, int failure,
                int mixed, int unknown) {
            actionKey = key;
            incidents = all;
            successOnlyIncidents = success;
            failureOnlyIncidents = failure;
            mixedIncidents = mixed;
            partialOrUnresolvedIncidents = unknown;
        }

        public String display(boolean greek) {
            if (greek)
                return actionKey + ": " + incidents + " περιστατικά, "
                        + successOnlyIncidents + " μόνο αναφερόμενες επιτυχίες, "
                        + failureOnlyIncidents + " μόνο αναφερόμενες αποτυχίες, "
                        + mixedIncidents + " αντικρουόμενες εκβάσεις, "
                        + partialOrUnresolvedIncidents + " ατελή αποτελέσματα.";
            return actionKey + ": " + incidents + " incidents, "
                    + successOnlyIncidents + " success reports only, "
                    + failureOnlyIncidents + " failure reports only, "
                    + mixedIncidents + " conflicting outcomes, "
                    + partialOrUnresolvedIncidents + " incomplete results.";
        }
    }

    public static final class Assessment {
        public final Status status;
        public final int distinctIncidents;
        public final int distinctAttempts;
        public final int incidentsWithAnyReportedSuccess;
        public final int incidentsWithAnyReportedFailure;
        public final boolean crossActionOrIncidentDisagreement;
        public final List<ActionHistory> actions;

        private Assessment(Status status, int incidents, int attempts,
                int success, int failure, boolean disagreement,
                List<ActionHistory> actions) {
            this.status = status;
            distinctIncidents = incidents;
            distinctAttempts = attempts;
            incidentsWithAnyReportedSuccess = success;
            incidentsWithAnyReportedFailure = failure;
            crossActionOrIncidentDisagreement = disagreement;
            this.actions = Collections.unmodifiableList(actions);
        }

        public String display(boolean greek) {
            switch (status) {
                case NO_MATCH:
                    return greek
                            ? "Καμία τεκμηριωμένη δημοσιευμένη αντιστοίχιση για αυτόν ακριβώς τον συνδυασμό μοντέλου και ένδειξης."
                            : "No source-linked published report for this exact model and clue.";
                case UNRESOLVED_OR_PARTIAL_ONLY:
                    return greek
                            ? "Μόνο μερικές ή ανεπίλυτες δημοσιευμένες εκβάσεις. Δεν υπάρχει τεκμηριωμένη επιτυχής αποκατάσταση."
                            : "Only partial or unresolved published outcomes; no successful resolution is documented.";
                case ONLY_REPORTED_FAILURES:
                    return greek
                            ? "Μόνο αναφερόμενες αποτυχημένες προσπάθειες. Δεν έχει τεκμηριωθεί λύση."
                            : "Only reported failed attempts; no documented fix.";
                case MIXED_OR_RECURRING_OUTCOMES:
                    return greek
                            ? "Αντικρουόμενες, επαναλαμβανόμενες ή διαφορετικές εκβάσεις. Δεν τεκμηριώνεται μία αξιόπιστη λύση."
                            : "Conflicting, recurring or differing outcomes; no single reliable fix is established.";
                case ONE_SELF_REPORTED_SUCCESS:
                    return greek
                            ? "Μία διαφορετική συσκευή με αναφερόμενη επιτυχία. Η λύση δεν έχει επιβεβαιωθεί ανεξάρτητα."
                            : "One distinct device with a reported success; the fix has not been independently verified.";
                case MULTIPLE_SELF_REPORTED_SUCCESSES:
                default:
                    return greek
                            ? "Περισσότερα από ένα διαφορετικά περιστατικά αναφέρουν επιτυχία. Δεν αποτελεί πιστοποίηση ή οδηγία αντικατάστασης."
                            : "More than one distinct incident reports success; this is not certification or a replacement instruction.";
            }
        }

        public String independentEvidence(boolean greek) {
            if (greek)
                return distinctIncidents + " διαφορετικά δημοσιευμένα περιστατικά, "
                        + distinctAttempts + " προσπάθειες: "
                        + incidentsWithAnyReportedSuccess + " με αναφορά επιτυχίας, "
                        + incidentsWithAnyReportedFailure + " με αναφορά αποτυχίας. "
                        + "Η έκβαση παραμένει αυτοαναφερόμενη και όχι αποτέλεσμα διαγνωστικού Apple.";
            return distinctIncidents + " distinct published incidents, "
                    + distinctAttempts + " attempts: "
                    + incidentsWithAnyReportedSuccess + " with success reports, "
                    + incidentsWithAnyReportedFailure + " with failure reports. "
                    + "All outcomes are self-reported, not results of Apple diagnostics.";
        }
    }

    /** Aggregate one incident across different attempted parts and outcomes. */
    private static final class Incident {
        final Map<String, EnumSet<IPhonePanicRepairKnowledgeBase.Outcome>> byAction =
                new LinkedHashMap<>();
        boolean success;
        boolean failure;
        boolean partial;
        boolean unresolved;
        void add(IPhonePanicRepairKnowledgeBase.RepairCase item) {
            EnumSet<IPhonePanicRepairKnowledgeBase.Outcome> states = byAction.get(item.actionKey);
            if (states == null) {
                states = EnumSet.noneOf(IPhonePanicRepairKnowledgeBase.Outcome.class);
                byAction.put(item.actionKey, states);
            }
            states.add(item.outcome);
            switch (item.outcome) {
                case SELF_REPORTED_SUCCESS: success = true; break;
                case SELF_REPORTED_FAILURE: failure = true; break;
                case PARTIAL_OR_RECURRING: partial = true; break;
                default: unresolved = true; break;
            }
        }
    }

    /**
     * Expects the exact result of IPhonePanicRepairKnowledgeBase.lookup(), or
     * delegates to assessExact() to guarantee a model/code-specific filter.
     * Deduplicates identical record IDs; never inflates independent devices
     * from repeated panic files or repeated attempted parts in one case.
     */
    public static Assessment assess(List<IPhonePanicRepairKnowledgeBase.RepairCase> matched) {
        if (matched == null || matched.isEmpty())
            return new Assessment(Status.NO_MATCH, 0, 0, 0, 0, false,
                    Collections.emptyList());
        Set<String> ids = new HashSet<>();
        Map<String, Incident> incidents = new LinkedHashMap<>();
        for (IPhonePanicRepairKnowledgeBase.RepairCase item : matched) {
            if (item == null || !ids.add(item.id)) continue;
            Incident instance = incidents.get(item.incidentId);
            if (instance == null) {
                instance = new Incident();
                incidents.put(item.incidentId, instance);
            }
            instance.add(item);
        }
        if (ids.isEmpty()) return assess(Collections.emptyList());

        int successIncidents = 0, failureIncidents = 0;
        boolean mixed = false, recurring = false;
        for (Incident i : incidents.values()) {
            if (i.success) successIncidents++;
            if (i.failure) failureIncidents++;
            mixed |= i.success && i.failure;
            recurring |= i.partial;
        }

        // Per-action statistics are counted by independent incident; do NOT
        // present attempt-success ratios or a "best part" ranking.
        Map<String, Map<String, EnumSet<IPhonePanicRepairKnowledgeBase.Outcome>>> byAction =
                new HashMap<>();
        for (Map.Entry<String, Incident> entry : incidents.entrySet()) {
            for (Map.Entry<String, EnumSet<IPhonePanicRepairKnowledgeBase.Outcome>>
                    action : entry.getValue().byAction.entrySet()) {
                Map<String, EnumSet<IPhonePanicRepairKnowledgeBase.Outcome>> owners =
                        byAction.get(action.getKey());
                if (owners == null) {
                    owners = new LinkedHashMap<>();
                    byAction.put(action.getKey(), owners);
                }
                owners.put(entry.getKey(), action.getValue());
            }
        }

        List<ActionHistory> actions = new ArrayList<>();
        for (Map.Entry<String, Map<String, EnumSet<IPhonePanicRepairKnowledgeBase.Outcome>>>
                action : byAction.entrySet()) {
            int good = 0, bad = 0, conflicting = 0, incomplete = 0;
            for (EnumSet<IPhonePanicRepairKnowledgeBase.Outcome> states :
                    action.getValue().values()) {
                boolean hasGood = states.contains(
                        IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_SUCCESS);
                boolean hasBad = states.contains(
                        IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_FAILURE);
                boolean uncertain = states.contains(
                        IPhonePanicRepairKnowledgeBase.Outcome.PARTIAL_OR_RECURRING)
                        || states.contains(IPhonePanicRepairKnowledgeBase.Outcome.UNRESOLVED);
                if ((hasGood && hasBad) || (hasGood && uncertain)
                        || (hasBad && uncertain)) {
                    conflicting++;
                    mixed = true;
                } else if (hasGood) good++;
                else if (hasBad) bad++;
                else incomplete++;
            }
            if (good > 0 && (bad > 0 || conflicting > 0)) mixed = true;
            actions.add(new ActionHistory(action.getKey(), action.getValue().size(),
                    good, bad, conflicting, incomplete));
        }
        actions.sort(Comparator.comparing(a -> a.actionKey)); // lexical, NOT rank

        Status status;
        if (successIncidents == 0 && failureIncidents == 0)
            status = Status.UNRESOLVED_OR_PARTIAL_ONLY;
        else if (successIncidents == 0 && !recurring)
            status = Status.ONLY_REPORTED_FAILURES;
        else if (mixed || recurring || (successIncidents > 0 && failureIncidents > 0))
            status = Status.MIXED_OR_RECURRING_OUTCOMES;
        else if (successIncidents == 1)
            status = Status.ONE_SELF_REPORTED_SUCCESS;
        else
            status = Status.MULTIPLE_SELF_REPORTED_SUCCESSES;

        return new Assessment(status, incidents.size(), ids.size(),
                successIncidents, failureIncidents, mixed || recurring, actions);
    }

    public static Assessment assessExact(
            List<IPhonePanicRepairKnowledgeBase.RepairCase> catalog,
            String exactModel, String exactClue) {
        return assess(IPhonePanicRepairKnowledgeBase.lookup(catalog, exactModel, exactClue));
    }
}
