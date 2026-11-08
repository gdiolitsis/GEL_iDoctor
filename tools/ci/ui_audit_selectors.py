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
