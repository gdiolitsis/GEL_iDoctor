import re
import unittest
import xml.etree.ElementTree as ET
from ui_audit_selectors import MANUAL_ENTRY, matching, is_visible, find_visible

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

if __name__ == "__main__":
    unittest.main()
