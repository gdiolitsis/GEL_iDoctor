#!/usr/bin/env python3
"""Generate anonymised example service-report pages, never customer data."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

W, H = 1087, 1536
BLACK, BLUE, GREEN, GOLD, GRAY = "#151515", "#176DB1", "#087F36", "#C6A33C", "#777777"
FONT_DIRS = [Path("/usr/share/fonts/truetype/dejavu"),
             Path("/usr/share/fonts/truetype/liberation2")]

def font(name, size):
    for directory in FONT_DIRS:
        path = directory / name
        if path.is_file():
            return ImageFont.truetype(str(path), size)
    raise RuntimeError("Required font missing: " + name)

REG = font("DejaVuSansMono.ttf", 20)
BOLD = font("DejaVuSansMono-Bold.ttf", 20)
HEAD = font("DejaVuSans-Bold.ttf", 40)
MID = font("DejaVuSans-Bold.ttf", 23)
SMALL = font("DejaVuSans.ttf", 18)

def new_page():
    page = Image.new("RGB", (W, H), "white")
    return page, ImageDraw.Draw(page)

def put(draw, x, y, content, color=BLACK, face=REG):
    draw.text((x, y), content, font=face, fill=color)

def log(draw, y, content):
    if content.startswith("------"):
        put(draw, 32, y, content, GRAY, REG)
    elif content.startswith("✔ "):
        put(draw, 32, y, content, GREEN, BOLD)
    elif content.startswith("ℹ "):
        # PDF's informational i glyph, rather than unsupported font symbols.
        put(draw, 32, y, "i " + content[2:], BLUE, BOLD)
    elif content.startswith(("Result: ", "Note: ", "Status: ",
                             "Bottom RMS: ", "Bottom Peak: ",
                             "Top RMS: ", "Top Peak: ")):
        label, value = content.split(": ", 1)
        put(draw, 32, y, label + ": ", BLACK, BOLD)
        x = 32 + draw.textlength(label + ": ", font=BOLD)
        put(draw, x, y, value, GREEN, BOLD)
    else:
        put(draw, 32, y, content)

def first():
    page, d = new_page()
    put(d, 32, 40, "GEL Service Report", BLACK, HEAD)
    put(d, 32, 112, "Sample Repair Shop", BLACK, MID)
    put(d, 32, 153, "Technician: Demo Technician", BLACK, SMALL)
    put(d, 720, 112, "SAMPLE REPORT", BLACK, MID)
    put(d, 720, 153, "Device: Demo Android device", BLACK, SMALL)
    d.line((32, 249, W-32, 249), fill=GOLD, width=3)
    rows = [
        "ANDROID MANUAL TESTS – HARDWARE DIAGNOSTICS",
        "--------------------------------------------",
        "ℹ GEL Manual Tests – ready. Open a category above and run the required labs.",
        "--------------------------------------------",
        "ℹ LAB 3 – Earpiece Audio Path Check",
        "--------------------------------------------",
        "ℹ Saving audio state.",
        "ℹ Preparing earpiece routing.",
        "ℹ Playing earpiece test tones.",
        "ℹ Tone 1 / 3", "ℹ Tone 2 / 3", "ℹ Tone 3 / 3",
        "✔ Earpiece tone playback completed.",
        "Result: User confirmed audio playback",
        "✔ Lab 3 finished.",
        "--------------------------------------------",
        "ℹ LAB 4 – Microphone Hardware Check",
        "--------------------------------------------",
        "ℹ Bottom microphone auto check:",
        "--------------------------------------------",
        "Bottom RMS: 2445",
        "Bottom Peak: 15342",
        "ℹ Top microphone auto check:",
        "--------------------------------------------",
        "Top RMS: 1625",
        "Top Peak: 7297",
        "ℹ Hardware conclusions:",
        "--------------------------------------------",
        "Status: Acoustic input was observed on both capture paths",
        "✔ Lab 4 BASE finished.",
        "--------------------------------------------",
        "ℹ LAB 4 PRO – Bottom microphone call quality"
    ]
    for i, row in enumerate(rows):
        log(d, 301 + i*35, row)
    return page

def final():
    page, d = new_page()
    rows = [
        "--------------------------------------------",
        "Result: The user confirmed clear playback of the recorded sample.",
        "Note: Playback passed the user confirmation. No further action is needed.",
        "--------------------------------------------",
        "ℹ LAB 4 PRO – Earpiece Call quality",
        "--------------------------------------------",
        "Result: According to the user's declaration, the earpiece delivers clear audio.",
        "--------------------------------------------",
        "✔ Lab 4 finished.",
        "--------------------------------------------"
    ]
    for i, row in enumerate(rows):
        log(d, 36 + i*31, row)
    put(d, 32, 398, "Repair Summary / Τι επισκευάστηκε", BLACK, BOLD)
    for y in (462, 500, 538, 576):
        d.line((32,y,W-32,y), fill=GRAY, width=2)
    put(d, 32, 630, "Additional Notes / Επιπλέον παρατηρήσεις", BLACK, BOLD)
    for y in (695, 733, 771):
        d.line((32,y,W-32,y), fill=GRAY, width=2)
    for x in (32, 580):
        d.line((x,844,x+474,844), fill=BLACK, width=3)
        d.line((x,939,x+474,939), fill=BLACK, width=3)
    put(d, 32, 855, "Technician Name / Όνομα τεχνικού", GRAY, SMALL)
    put(d, 580, 855, "Customer Name / Όνομα πελάτη", GRAY, SMALL)
    put(d, 32, 950, "Signature / Υπογραφή", GRAY, SMALL)
    put(d, 580, 950, "Signature / Υπογραφή", GRAY, SMALL)
    d.line((32,1040,680,1040), fill=BLACK, width=3)
    put(d, 32, 1052, "Date / Ημερομηνία", GRAY, SMALL)
    return page

if __name__ == "__main__":
    output = Path("app/src/main/res/drawable")
    output.mkdir(parents=True, exist_ok=True)
    first().save(output / "gel_report_preview_first.png", optimize=True)
    final().save(output / "gel_report_preview_final.png", optimize=True)
    print("Generated both anonymised 1087x1536 preview images")
