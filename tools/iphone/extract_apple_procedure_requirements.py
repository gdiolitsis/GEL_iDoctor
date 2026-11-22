#!/usr/bin/env python3
"""Source-preserving, conservative Apple repair procedure metadata audit.

Reads official public Apple support articles already listed in the 1,074-row
chapter index. Extracts factual names of prerequisite parts/tools and flags
for manufacturer cautions and Repair Assistant modality.

This is an AUTOMATED ARTICLE-METADATA INDEX, NOT technician-reviewed complete
instructions. Do not use its fields to direct a repair or infer a failure
from iOS panic data. Actual illustrated steps remain on Apple's site.
"""
import argparse
import csv
from datetime import date
from html.parser import HTMLParser
from pathlib import Path
import re
import time
from urllib.error import HTTPError, URLError
from urllib.parse import urlparse
from urllib.request import Request, urlopen

ROOT = Path(__file__).resolve().parents[2]
CHAPTERS = ROOT / "app/src/main/assets/iphone_apple_manual_chapters_v1.tsv"
OUTPUT = ROOT / "docs/qa/APPLE_PROCEDURE_REQUIREMENTS_AUDIT_2026-11-22.tsv"
REPORT = ROOT / "docs/qa/APPLE_PROCEDURE_REQUIREMENTS_AUDIT_2026-11-22.md"
FIELDS = ["source_url", "chapter_references", "manuals", "status", "official_title",
          "prerequisite_parts", "tool_names", "prerequisite_items",
          "tool_items", "mentions_battery_safety", "mentions_broken_glass_safety",
          "has_warning_labels", "repair_assistant_modality", "review_scope", "checked_on"]

def official(url):
    parsed = urlparse(url)
    return parsed.scheme == "https" and parsed.netloc == "support.apple.com" and bool(
        re.fullmatch(r"/(?:[a-z]{2}(?:-[a-z]{2})?/)?[0-9]{5,7}/?", parsed.path))

class RequirementParser(HTMLParser):
    """Read source labels; never store a full instruction sentence or figure."""
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.head = None
        self.heading_parts = []
        self.headings = []
        self.title = ""
        self.prerequisites = []
        self.tools = []
        self.prereq_mode = False
        self.tools_mode = False
        self.in_li = False
        self.li_parts = []
        self.body_parts = []
        self.skip = None

    def handle_starttag(self, tag, attrs):
        if tag in ("script", "style", "noscript", "svg"):
            self.skip = tag
            return
        if self.skip: return
        if tag in ("h1", "h2", "h3"):
            self.head = tag
            self.heading_parts = []
        if tag == "li" and not self.in_li:
            self.in_li = True
            self.li_parts = []

    def handle_data(self, data):
        if self.skip: return
        if len(self.body_parts) < 35000:
            self.body_parts.append(data)
        if self.head is not None:
            self.heading_parts.append(data)
        elif self.in_li:
            self.li_parts.append(data)
        if "remove the following parts before you begin" in data.casefold():
            self.prereq_mode = True
            self.tools_mode = False

    def handle_endtag(self, tag):
        if tag == self.skip:
            self.skip = None
            return
        if self.skip: return
        if self.head == tag:
            label = " ".join(" ".join(self.heading_parts).split())
            self.headings.append((tag, label))
            if tag == "h1" and not self.title: self.title = label
            if tag in ("h2", "h3"):
                if label.casefold() == "tools":
                    self.tools_mode = True
                    self.prereq_mode = False
                elif tag == "h2" or label.casefold() in (
                        "warning", "caution", "important", "note"):
                    self.tools_mode = False
                    if tag == "h2": self.prereq_mode = False
            self.head = None
            self.heading_parts = []
        if tag == "li" and self.in_li:
            label = " ".join(" ".join(self.li_parts).split())
            if label and len(label) <= 150:
                if self.prereq_mode and len(self.prerequisites) < 20:
                    self.prerequisites.append(label)
                if self.tools_mode and len(self.tools) < 55:
                    self.tools.append(label)
            self.in_li = False
            self.li_parts = []

def fetch(url):
    request = Request(url, headers={
        "User-Agent": "Mozilla/5.0 (compatible; GEL-iDoctor-Source-Requirements/1.0)",
        "Accept": "text/html",
        "Accept-Language": "en-US,en;q=0.9",
    })
    try:
        with urlopen(request, timeout=22) as response:
            actual = response.geturl()
            if not official(actual):
                raise ValueError("Redirected outside official Apple Support")
            if urlparse(actual).path.rstrip("/").split("/")[-1] != urlparse(url).path.rstrip("/").split("/")[-1]:
                raise ValueError("Apple redirected to different article ID")
            data = response.read(3_000_000)
            if response.status != 200 or len(data) < 1200:
                raise ValueError("Unexpected response from Apple article")
            return data.decode("utf-8", "replace")
    except HTTPError as err:
        raise RuntimeError("RATE_LIMITED" if err.code == 429 else "HTTP_" + str(err.code)) from err
    except (ValueError, URLError, TimeoutError) as err:
        raise RuntimeError(type(err).__name__.upper()) from err

def catalog():
    urls = {}
    with CHAPTERS.open(encoding="utf-8", newline="") as file:
        for row in csv.reader(file, delimiter="\t"):
            if not row or row[0].startswith("#") or row[0] == "entry_id":
                continue
            if len(row) != 8 or row[7] != "DIRECT_CHAPTER":
                raise ValueError("Unexpected chapter source provenance")
            if row[2] != "PROCEDURE": continue
            if not official(row[6]): raise ValueError("Non-Apple procedure link")
            info = urls.setdefault(row[6], {"chapters": 0, "manuals": set()})
            info["chapters"] += 1
            info["manuals"].add(row[1])
    if sum(a["chapters"] for a in urls.values()) != 407 or len(urls) != 315:
        raise ValueError("Procedure source scope changed unexpectedly")
    return urls

def extract(url, source, html):
    p = RequirementParser()
    p.feed(html)
    if not p.title:
        raise RuntimeError("MISSING_APPLE_TITLE")
    text = " ".join(" ".join(p.body_parts).split()).casefold()
    def short_names(values):
        # Part and tool names are factual. Avoid long paragraph copying and
        # any mistaken mixing of a list with procedural instruction text.
        clean = []
        for item in values:
            normalized = " ".join(item.replace("|", "/").split())
            if len(normalized) > 100 or len(normalized.split()) > 15:
                continue
            if normalized not in clean: clean.append(normalized)
        return clean
    prereqs = short_names(p.prerequisites)
    tools = short_names(p.tools)
    # These flags are only mentions; they do not mean those risks were
    # individually inspected or that a particular part must be replaced.
    if re.search(r"\brequired\b.{0,85}\brepair assistant\b", text) or re.search(
            r"\brepair assistant\b.{0,85}\brequired\b", text):
        assistant = "REQUIRED_TEXT"
    elif re.search(r"\brecommended\b.{0,85}\brepair assistant\b", text) or re.search(
            r"\brepair assistant\b.{0,85}\brecommended\b", text):
        assistant = "RECOMMENDED_TEXT"
    elif "repair assistant" in text:
        assistant = "MENTIONED_ONLY"
    else:
        assistant = "NOT_MENTIONED"
    heading_text = " ".join(t for _, t in p.headings).casefold()
    return dict(source_url=url, chapter_references=source["chapters"],
                manuals=len(source["manuals"]), status="SOURCE_METADATA_EXTRACTED",
                official_title=p.title[:120], prerequisite_parts=" | ".join(prereqs),
                tool_names=" | ".join(tools),
                prerequisite_items=len(prereqs), tool_items=len(tools),
                mentions_battery_safety=str("battery safety" in text).lower(),
                mentions_broken_glass_safety=str("broken glass safety" in text).lower(),
                has_warning_labels=str("warning" in heading_text).lower(),
                repair_assistant_modality=assistant,
                review_scope="AUTOMATED_LABELS_ONLY_NOT_FULL_SERVICE_REVIEW",
                checked_on=date.today().isoformat())

def load():
    if not OUTPUT.is_file(): return {}
    with OUTPUT.open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader((l for l in f if not l.startswith("#")), delimiter="\t")
        if reader.fieldnames != FIELDS:
            raise ValueError("Unexpected requirements audit schema")
        return {r["source_url"]: r for r in reader}

def run():
    arg = argparse.ArgumentParser()
    arg.add_argument("--write", action="store_true")
    arg.add_argument("--max-pages", type=int, default=315)
    arg.add_argument("--delay", type=float, default=1.45)
    opts = arg.parse_args()
    urls = catalog()
    prior = {url: row for url, row in load().items() if url in urls}
    order = sorted(urls, key=lambda u: (-urls[u]["chapters"], u))
    examined, errors, limits = 0, 0, 0
    for url in order:
        if prior.get(url, {}).get("status") == "SOURCE_METADATA_EXTRACTED": continue
        if examined >= opts.max_pages or limits >= 4: break
        examined += 1
        try:
            prior[url] = extract(url, urls[url], fetch(url))
            limits = 0
        except RuntimeError as ex:
            errors += 1
            if str(ex) == "RATE_LIMITED": limits += 1
            prior[url] = dict(source_url=url, chapter_references=urls[url]["chapters"],
                              manuals=len(urls[url]["manuals"]), status=str(ex),
                              review_scope="NOT_REVIEWED", checked_on=date.today().isoformat())
        if examined % 20 == 0:
            print(f"PROCEDURE SOURCES {examined}, errors {errors}", flush=True)
        time.sleep(max(0.5, opts.delay) + (6 if limits else 0))
    for url, item in urls.items():
        prior.setdefault(url, dict(source_url=url, chapter_references=item["chapters"],
                                   manuals=len(item["manuals"]), status="NOT_YET_FETCHED",
                                   review_scope="NOT_REVIEWED"))
    available = [u for u in urls if prior[u]["status"] == "SOURCE_METADATA_EXTRACTED"]
    unavailable = [u for u in urls if u not in available]
    mapped = sum(urls[url]["chapters"] for url in available)
    if opts.write:
        OUTPUT.parent.mkdir(parents=True, exist_ok=True)
        with OUTPUT.open("w", encoding="utf-8", newline="") as f:
            f.write("# Automated names/flags from official Apple HTML. NOT technician-reviewed physical repair instructions.\n")
            writer = csv.DictWriter(f, fieldnames=FIELDS, delimiter="\t",
                                    lineterminator="\n", extrasaction="ignore")
            writer.writeheader()
            for url in sorted(urls):
                writer.writerow(prior[url])
        summary = [
            "# Apple iPhone individual repair-procedure prerequisite and tool metadata audit",
            "",
            "**Automated factual metadata, not a complete repair manual or safety certification.**",
            "All quoted part and tool labels remain linked to the original first-party",
            "Apple procedure. The full illustrated procedure and its warnings must",
            "be read before attempting any physical intervention.",
            "",
            f"- Source-linked procedure chapter records: **407**",
            f"- Distinct procedure/preparation articles: **{len(urls)}**",
            f"- Apple procedure sources inspected successfully: **{len(available)}**",
            f"- Indexed chapter records with source metadata: **{mapped}**",
            f"- Procedure sources not inspected: **{len(unavailable)}**",
            f"- Errors in this run: **{errors}**",
            "",
            "The TSV provides article identity, actual named prerequisite parts",
            "(only where explicitly detectable), actual named tools (only where",
            "explicitly detected), binary warning mentions and Repair Assistant",
            "wording class. Missing metadata is **unknown**, not evidence that the",
            "tool, part or safety step is unnecessary.",
            "",
            "REQUIRED_TEXT, RECOMMENDED_TEXT and MENTIONED_ONLY are distinctions",
            "extracted from actual article wording, not a blanket rule for a model.",
            "No Apple diagnostic test or outcome is simulated; no panic code",
            "can turn this metadata into an automatic replacement recommendation.",
            ""
        ]
        if unavailable:
            summary += ["## Sources not verified (first 20)"]
            summary += ["- " + u + " — " + prior[u]["status"] for u in unavailable[:20]]
        REPORT.write_text("\n".join(summary) + "\n", encoding="utf-8")
    print(f"PROCEDURE INDEX RESULT {len(available)}/{len(urls)}, "
          f"chapter refs {mapped}/407 errors={errors}", flush=True)
    if not available:
        raise RuntimeError("No official procedure sources could be inspected")

if __name__ == "__main__":
    run()
