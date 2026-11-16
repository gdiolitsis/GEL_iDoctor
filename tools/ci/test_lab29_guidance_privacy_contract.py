#!/usr/bin/env python3
"""Source-level regression guard: Lab 29 must not collect buyer IMEI outcomes."""
from pathlib import Path

src = Path("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java").read_text(encoding="utf-8")

def section(start, end):
    assert src.count(start) == 1, f"Expected exactly one {start!r}"
    a = src.index(start)
    b = src.index(end, a + len(start))
    assert b > a, f"Missing end {end!r}"
    return src[a:b]

guidance = section(
    "private void showLab29ExternalGuidanceDialog(boolean gr)",
    "// Only an external provider displays the result; GEL records no IMEI answers."
)
lab30 = section("private void lab30CombineFindings()", "private void lab31FinalSummary()")
lab31 = section("private void lab31FinalSummary()", "private void enableSingleExportButton()")

assert "Uri.parse(\"https://www.checkmend.com/eu/consumer/report\")" in guidance
assert "Intent.ACTION_VIEW" in guidance
assert "*#06#" in guidance
assert "setCancelable(true)" in guidance
assert "EXTERNAL IMEI CHECK" in guidance
assert "EXIT" in guidance

for forbidden in (
    "CheckBox", "EditText", "SharedPreferences", "GELServiceLog", "logLabel",
    ".putInt(", ".putLong(", ".putBoolean(", "precheck_status", "imeilookup",
    "onActivityResult(", "getImei(", "getDeviceId(", "setOnActivityResult"
):
    assert forbidden not in guidance, f"Private IMEI result may leak via {forbidden}"

for method in (lab30, lab31):
    assert 'getInt("lab29_precheck_' not in method
    assert 'getBoolean("lab29_precheck_' not in method
    assert 'getString("lab29_precheck_' not in method
    assert "UsedDevicePrecheckPolicy" not in method
    assert "TechnicianPrecheckAssessment" not in method
    assert "FLAGGED at the recorded official source" not in method

for key in (
    "lab29_precheck_status",
    "lab29_precheck_imei_status",
    "lab29_precheck_official_source",
    "lab29_precheck_documents",
    "lab29_precheck_transfer",
    "lab29_precheck_user_reported",
):
    assert f'.remove("{key}")' in src, f"Missing legacy privacy cleanup for {key}"
    assert f'.putInt("{key}"' not in src, f"Status still saved: {key}"
    assert f'.putBoolean("{key}"' not in src, f"Buyer check still saved: {key}"

# Legacy Lab27 has been intentionally removed. Privacy/IMEI checks remain
# mandatory for the renumbered buyer and technical-summary methods.
assert src.count("private void lab28HardwareStability()") == 0
assert "this::lab28HardwareStability" not in src
assert src.count("private void lab29DeviceAuthenticity()") == 1
assert src.count("private void lab30CombineFindings()") == 1
assert src.count("private void lab31FinalSummary()") == 1
assert "27. Καταγραφές λειτουργίας και μετρήσεις" not in src
assert "lab29_obs_manufacturer" in lab30
assert "lab29_obs_manufacturer" in lab31
assert "Uri.parse(\"https://www.checkmend.com/eu/consumer/report\")" in guidance
print("Buyer guidance privacy and 29-lab count contract: PASS")
