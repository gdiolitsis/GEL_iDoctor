"""Pure selectors for Android UI XML; no device or account access."""
import re
import unicodedata

MANUAL_ENTRY = r"^GEL (?:Android Manual Tests|Χειροκίνητα Tests Android)$"

def normalize(text):
    return "".join(c for c in unicodedata.normalize("NFD", text) if not unicodedata.combining(c))

def matching(pattern, text, flags=0):
    return re.search(normalize(pattern), normalize(text), flags)

def is_visible(node):
    b = list(map(int, re.findall(r"\d+", node.get("bounds",""))))
    return len(b) == 4 and b[2] > b[0] and b[3] > b[1]

def find_visible(root, predicate):
    for node in root.iter("node"):
        if is_visible(node) and predicate(node):
            return node
    return None

def scroll_gesture(root, up=True):
    """Swipe in the largest scrollable viewport instead of fixed LOGS panels.

    Android 11: labs scroll pane is y=24..382, while LOGS is y=448..548.
    Whole-screen swipes beginning at 72% of screen height hit the LOGS pane.
    """
    candidates = []
    for node in root.iter("node"):
        if node.get("scrollable") != "true":
            continue
        b = list(map(int, re.findall(r"\d+", node.get("bounds", ""))))
        if len(b) != 4:
            continue
        x1, y1, x2, y2 = b
        width, height = x2 - x1, y2 - y1
        if width <= 0 or height < 80:
            continue
        pad = max(12, int(height * .2))
        top, bottom = y1 + pad, y2 - pad
        if bottom - top < 40:
            continue
        candidates.append((width * height, -y1, (x1 + x2) // 2, bottom, top))
    if not candidates:
        return None
    _, _, x, bottom, top = max(candidates)
    return (x, bottom, x, top) if up else (x, top, x, bottom)


def manual_section_pattern(lo, hi):
    """Allow the shipped 25-30 label and the corrected 25-31 label."""
    end = r"(?:30|31)" if (lo, hi) == (25, 31) else str(hi)
    return r"Manual Labs\s*" + str(lo) + r"\s*[–—-]\s*" + end


def incomplete_lab_names(results):
    """Missing sections/controls are incomplete coverage, never a green QA run."""
    incomplete = {"SECTION_NOT_FOUND", "LAB_NOT_FOUND", "HARNESS_OR_UI_ERROR"}
    return [item["name"] for item in results
            if re.fullmatch(r"lab-\d{2}", item.get("name", ""))
            and item.get("status") in incomplete]

def find_accessible_action(root, label):
    """Find a visible clickable action by exact text or content description."""
    def named(node):
        if node.get("clickable") != "true":
            return False
        return any(matching(r"^" + re.escape(label) + r"$",
                            node.get(field, ""), re.I)
                   for field in ("text", "content-desc"))
    return find_visible(root, named)
