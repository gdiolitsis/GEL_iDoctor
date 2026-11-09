import re
import unittest
import xml.etree.ElementTree as ET
from ui_audit_selectors import (MANUAL_ENTRY, matching, is_visible, find_visible,
                                scroll_gesture, manual_section_pattern, incomplete_lab_names,
                                find_accessible_action, SAFE_ACCOUNT_ACTIONS, is_safe_account_action,
                                has_welcome_dialog)

class SelectorsTest(unittest.TestCase):
    def test_android_uppercase_greek_removes_tonos(self):
        self.assertIsNotNone(matching(r"^(Αργότερα|Όχι τώρα)$", "ΑΡΓΟΤΕΡΑ", re.I))
        self.assertIsNotNone(matching(r"^(Αργότερα|Όχι τώρα)$", "ΟΧΙ ΤΩΡΑ", re.I))
    def test_manual_heading_is_not_the_entry_action(self):
        self.assertIsNone(matching(MANUAL_ENTRY, "ANDROID MANUAL TESTS", re.I))
        self.assertIsNotNone(matching(MANUAL_ENTRY, "GEL Android Manual Tests", re.I))
        self.assertIsNotNone(matching(MANUAL_ENTRY, "GEL Χειροκίνητα Tests Android", re.I))
    def test_leaf_controls_are_returned_even_when_elementtree_bool_is_false(self):
        root = ET.fromstring('<hierarchy><node text="Allow" bounds="[10,10][80,60]" /></hierarchy>')
        found = find_visible(root, lambda n: n.get("text") == "Allow")
        self.assertIsNotNone(found)
        self.assertEqual(found.get("text"), "Allow")
        self.assertIsNone(find_visible(root, lambda n: n.get("text") == "Missing"))
    def test_zero_area_is_not_tappable(self):
        self.assertFalse(is_visible(ET.fromstring('<node bounds="[0,0][0,50]"/>')))
        self.assertTrue(is_visible(ET.fromstring('<node bounds="[0,0][50,50]"/>')))


    def test_scroll_targets_labs_not_fixed_logs_on_android_11(self):
        # Real Android 11 bounds: labs [0,24][320,382], LOGS [0,448][320,548].
        root = ET.fromstring(
            '<hierarchy><node scrollable="true" bounds="[0,24][320,382]"/>'
            '<node scrollable="true" bounds="[0,448][320,548]"/></hierarchy>'
        )
        self.assertEqual(scroll_gesture(root), (160, 311, 160, 95))
        self.assertEqual(scroll_gesture(root, up=False), (160, 95, 160, 311))

    def test_scroll_no_scrollable_view_uses_fallback(self):
        root = ET.fromstring('<hierarchy><node bounds="[0,0][320,640]"/></hierarchy>')
        self.assertIsNone(scroll_gesture(root))

    def test_manual_section_legacy_and_corrected_range(self):
        pattern = manual_section_pattern(25, 31)
        self.assertIsNotNone(matching(pattern, "Advanced / Logs — Manual Labs 25–30", re.I))
        self.assertIsNotNone(matching(pattern, "Advanced / Logs — Manual Labs 25–31", re.I))
        self.assertIsNone(matching(manual_section_pattern(21, 24), "Manual Labs 25–30", re.I))

    def test_missing_lab_sections_fail_qa_coverage(self):
        rows = [
            {"name": "lab-10", "status": "SECTION_NOT_FOUND"},
            {"name": "lab-11", "status": "UI_OPENED_REQUIRES_INTERACTION_OR_REVIEW"},
            {"name": "lab-12", "status": "HARNESS_OR_UI_ERROR"},
        ]
        self.assertEqual(incomplete_lab_names(rows), ["lab-10", "lab-12"])


    def test_accessible_settings_toolbar_action_in_both_locales(self):
        root = ET.fromstring(
            '<hierarchy><node bounds="[0,0][320,64]">'
            '<node clickable="true" content-desc="Ρυθμίσεις" bounds="[270,10][315,57]"/>'
            '</node></hierarchy>')
        self.assertIsNotNone(find_accessible_action(root, "Ρυθμίσεις"))
        self.assertIsNone(find_accessible_action(root, "Settings"))
        eng = ET.fromstring(
            '<hierarchy><node clickable="true" content-desc="Settings" '
            'bounds="[270,10][315,57]"/></hierarchy>')
        self.assertIsNotNone(find_accessible_action(eng, "Settings"))

    def test_empty_toolbar_icon_does_not_pass_accessibility(self):
        root = ET.fromstring(
            '<hierarchy><node clickable="true" text="" content-desc="" '
            'bounds="[270,10][315,57]"/></hierarchy>')
        self.assertIsNone(find_accessible_action(root, "Settings"))



    def test_unattended_account_actions_are_read_only_allowlisted(self):
        self.assertEqual(len(SAFE_ACCOUNT_ACTIONS), 2)
        self.assertTrue(is_safe_account_action("Refresh gifted PRO"))
        self.assertTrue(is_safe_account_action("Restore Play purchases"))
        for label in ("Sign out", "Delete account", "Αποσύνδεση",
                      "Διαγραφή λογαριασμού", "Buy GEL PRO", "Subscribe"):
            self.assertFalse(is_safe_account_action(label), label)



    def test_welcome_dialog_requires_real_dismissal(self):
        blocked = ET.fromstring(
            '<hierarchy><node text="WELCOME" bounds="[20,80][300,120]"/>'
            '<node text="Mute voice instructions" bounds="[30,500][200,520]"/>'
            '</hierarchy>'
        )
        self.assertTrue(has_welcome_dialog(blocked))
        dismissed = ET.fromstring(
            '<hierarchy><node text="Settings" bounds="[20,80][300,120]"/></hierarchy>'
        )
        self.assertFalse(has_welcome_dialog(dismissed))
        greek = ET.fromstring(
            '<hierarchy><node text="ΚΑΛΩΣ ΗΡΘΑΤΕ" bounds="[20,80][300,120]"/>'
            '</hierarchy>'
        )
        self.assertTrue(has_welcome_dialog(greek))

if __name__ == "__main__":
    unittest.main()
