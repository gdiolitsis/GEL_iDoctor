#!/usr/bin/env python3
"""Verify Apple Repair Manual chapter links by matching actual Apple TOC anchors.

The catalog has real model-manual IDs and source URLs. NEVER fill a chapter link
by guessing an Apple article number, fuzzy-matching an unrelated heading, or
copying a similar part's link from another iPhone model.

Usage:
  python3 tools/iphone/sync_apple_repair_manual_urls.py --write
  python3 tools/iphone/sync_apple_repair_manual_urls.py --limit 2
All network traffic is Apple public Support pages; no customer logs are read.
"""
import argparse
import collections
from datetime import datetime, timezone
from html.parser import HTMLParser
import pathlib
import re
import sys
import time
import unicodedata
from urllib.error import HTTPError, URLError
from urllib.parse import urljoin, urlsplit, urlunsplit
from urllib.request import Request, urlopen

ROOT = pathlib.Path(__file__).resolve().parents[2]
DOCS = ROOT / "app/src/main/assets/iphone_apple_official_guidance_v1.tsv"
CHAPTERS = ROOT / "app/src/main/assets/iphone_apple_manual_chapters_v1.tsv"
REPORT = ROOT / "docs/qa/IPHONE_APPLE_MANUAL_LINK_AUDIT_2026-11-21.md"

def canonical(text):
    """Only compare actual headings after whitespace/NBSP/case normalization."""
    text = unicodedata.normalize("NFKC", text or "")
    # Some older Apple TOC links start with invisible U+FEFF/zero-width
    # characters (e.g. iPhone 12 Camera / Speaker); they are not names.
    text = "".join(ch for ch in text if unicodedata.category(ch) != "Cf")
    return " ".join(text.split()).casefold()

def official_article(url, origin):
    if not url:
        return None
    full = urljoin(origin, url)
    p = urlsplit(full)
    if p.scheme != "https" or p.hostname != "support.apple.com":
        return None
    # Ignore navigation, resources, anchors, article search and marketing pages.
    if not re.fullmatch(r"/(?:[a-z]{2}(?:-[a-z]{2})?/)?[0-9]{5,7}/?", p.path):
        return None
    return urlunsplit((p.scheme, p.netloc, p.path.rstrip("/"), "", ""))

class AppleLinkParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.url = None
        self.nested = []
        self.texts = []
        self.anchors = collections.defaultdict(set)
        self.anchor_order = []  # actual HTML anchor order for TOC neighbor checks
    def handle_starttag(self, tag, attrs):
        if self.url is not None and tag != "a":
            self.nested.append(tag)
        if tag == "a":
            attrs = dict(attrs)
            if self.url is None:
                self.url = attrs.get("href", "")
                self.texts = []
                self.nested = []
    def handle_data(self, data):
        if self.url is not None:
            self.texts.append(data)
    def handle_endtag(self, tag):
        if tag == "a" and self.url is not None:
            name = canonical(" ".join(self.texts))
            if name:
                self.anchors[name].add(self.url)
                self.anchor_order.append((name, self.url))
            self.url = None
            self.texts = []
            self.nested = []
        elif self.url is not None and self.nested:
            self.nested.pop()

def uniquely_located_in_contents(parser, title, before, after, origin):
    """For duplicates, require correct placement between two adjacent TOC headings.

    The order of headings in an Apple manual is independently documented in
    our per-model index. A second same-name link elsewhere on the page is not
    sufficient. This conservative rule only returns a URL when the sequence
    makes one candidate unambiguous; otherwise it leaves MANUAL_CONTENTS.
    """
    key = canonical(title)
    b = canonical(before)
    a = canonical(after)
    matches = []
    for pos, (name, href) in enumerate(parser.anchor_order):
        if name != key:
            continue
        if not any(n == b for n, _ in parser.anchor_order[max(0, pos-3):pos]):
            continue
        if not any(n == a for n, _ in parser.anchor_order[pos+1:pos+13]):
            continue
        url = official_article(href, origin)
        if url:
            matches.append(url)
    urls = set(matches)
    return next(iter(urls)) if len(urls) == 1 else None

def fetch(url):
    # Public official pages can rate-limit repeated requests. Pause/back off.
    last = None
    for attempt in range(4):
        if attempt:
            time.sleep(min(4 * (2 ** (attempt - 1)), 16))
        req = Request(url, headers={
            "User-Agent": "Mozilla/5.0 (compatible; GEL-iDoctor-Official-Manual-TOC-Audit/1.0)",
            "Accept": "text/html,application/xhtml+xml",
            "Accept-Language": "en-US,en;q=0.9",
        })
        try:
            with urlopen(req, timeout=28) as resp:
                if resp.status != 200:
                    raise ValueError("HTTP " + str(resp.status))
                data = resp.read(4_000_000)
                actual = resp.geturl()
            if len(data) < 2000:
                raise ValueError("Unexpectedly small Apple response")
            html = data.decode("utf-8", "replace")
            if "Repair Manual" not in html and "Εγχειρίδιο επισκευ" not in html:
                raise ValueError("Not an Apple Repair Manual HTML document")
            return html, actual
        except (HTTPError, URLError, TimeoutError, ValueError) as exc:
            last = str(exc)
    raise RuntimeError("Could not fetch Apple manual: " + str(last))

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--write", action="store_true", help="Update TSV and audit report")
    p.add_argument("--limit", type=int, default=0, help="Only first N manuals for trial")
    args = p.parse_args()

    original = CHAPTERS.read_text(encoding="utf-8")
    original_lines = original.splitlines(keepends=True)
    official = DOCS.read_text(encoding="utf-8").splitlines()
    sources = {}
    for line in official:
        if not line or line.startswith("#") or line.startswith("id\t"):
            continue
        fields = line.split("\t")
        if len(fields) == 10 and fields[2] in ("MODEL_MANUAL", "MODEL_MANUAL_DIRECTORY"):
            sources[fields[0]] = {"title": fields[4], "url": fields[7]}
    if len(sources) != 30:
        raise ValueError("Expected exactly 30 indexed Apple iPhone Repair Manuals")

    chapter_lines = {}
    for index, line in enumerate(original_lines):
        fields = line.rstrip("\r\n").split("\t")
        if len(fields) == 8 and fields[0] != "entry_id":
            chapter_lines[fields[0]] = (index, fields)
    if len(chapter_lines) != 983:
        raise ValueError("Existing chapter index unexpectedly changed; audit the baseline")

    pages = list(sources.items())
    if args.limit > 0:
        pages = pages[:args.limit]
    updates = {}
    reviewed = 0
    failures = []
    no_exact = []
    conflict = []
    page_stats = []
    order_resolutions = 0
    for manual_id, source in pages:
        title = source["title"]
        src_url = source["url"]
        # Prefer English wording for exact TOC-title comparisons; old model
        # master links using a regional locale remain authoritative.
        attempt_url = re.sub(r"^https://support\.apple\.com/(?:en-gb|en-ie|en-la|el-gr)/",
                             "https://support.apple.com/en-us/", src_url)
        try:
            html, actual = fetch(attempt_url)
            parser = AppleLinkParser()
            parser.feed(html)
            if len(parser.anchors) < 15:
                raise ValueError("No usable chapter TOC anchors on returned page")
            headings_matched = 0
            updated = 0
            total = 0
            for chapter_id, (index, fields) in chapter_lines.items():
                if fields[1] != manual_id:
                    continue
                total += 1
                title_en = fields[5]
                targets = {
                    official_article(u, actual)
                    for u in parser.anchors.get(canonical(title_en), ())
                }
                targets.discard(None)
                targets.discard(official_article(src_url, src_url))
                if len(targets) > 1:
                    # Resolve only if a unique direct Apple URL is positioned
                    # exactly where the catalog's neighboring TOC headings are.
                    sibling_rows = [row for _, row in chapter_lines.values()
                                    if row[1] == manual_id]
                    at = next((i for i, row in enumerate(sibling_rows)
                               if row[0] == chapter_id), -1)
                    if 0 < at < len(sibling_rows) - 1:
                        ordered = uniquely_located_in_contents(
                            parser, title_en, sibling_rows[at - 1][5],
                            sibling_rows[at + 1][5], actual)
                        if ordered is not None and ordered in targets:
                            targets = {ordered}
                            order_resolutions += 1
                if len(targets) == 1:
                    official_url = next(iter(targets))
                    headings_matched += 1
                    if fields[7] == "DIRECT_CHAPTER":
                        # Existing manually reviewed link is NOT overwritten;
                        # a disagreement needs an independent human check.
                        old_id = urlsplit(fields[6]).path.rstrip("/").split("/")[-1]
                        new_id = urlsplit(official_url).path.rstrip("/").split("/")[-1]
                        if old_id != new_id:
                            conflict.append((manual_id, chapter_id, title_en,
                                             fields[6], official_url))
                    else:
                        updates[chapter_id] = official_url
                        updated += 1
                else:
                    if fields[7] != "DIRECT_CHAPTER":
                        no_exact.append((manual_id, title_en,
                                         "not linked" if len(targets) == 0
                                         else "ambiguous: " + ", ".join(sorted(targets))))
            reviewed += 1
            page_stats.append((title, src_url, total, headings_matched, updated))
            print(f"{manual_id}: {headings_matched}/{total} actual named TOC anchors, "
                  f"{updated} newly linked", flush=True)
        except Exception as exc:
            failures.append((title, src_url, str(exc)))
            print(f"ERROR {manual_id}: {exc}", file=sys.stderr, flush=True)
        time.sleep(1.1)

    revised = list(original_lines)
    for chapter_id, new_url in updates.items():
        index, fields = chapter_lines[chapter_id]
        if fields[7] != "MANUAL_CONTENTS":
            raise ValueError("Refusing to overwrite direct chapter: " + chapter_id)
        fields[6] = new_url
        fields[7] = "DIRECT_CHAPTER"
        revised[index] = "\t".join(fields) + "\n"

    all_rows = [line.strip("\r\n").split("\t") for line in revised]
    all_rows = [row for row in all_rows if len(row) == 8 and row[0] != "entry_id"]
    direct = sum(row[7] == "DIRECT_CHAPTER" for row in all_rows)
    pending = sum(row[7] == "MANUAL_CONTENTS" for row in all_rows)
    if direct + pending != 983:
        raise AssertionError("Missing or extra chapter rows after source audit")

    summary = [
        "# Apple iPhone Repair Manual direct-link audit — 2026-11-21",
        "",
        "These links were extracted from actual named anchors in each official",
        "Apple model's Repair Manual contents page, NOT invented from article numbers.",
        "The script compares exact normalized English section headings and refuses",
        "conflicting URL IDs. Existing manually checked links remain untouched.",
        "",
        f"- Manuals fetched and parsed: **{reviewed} / {len(pages)} attempted**",
        f"- Newly discovered model/chapter matches: **{len(updates)}**",
        f"- All direct chapter links: **{direct} / 983**",
        f"- Remaining links to manual contents: **{pending}**",
        f"- Manual fetch failures: **{len(failures)}**",
        f"- Conflicts requiring manual verification: **{len(conflict)}**",
        f"- Entries with no unique named direct link: **{len(no_exact)}**",
        f"- Duplicate names disambiguated by neighboring Apple TOC order: **{order_resolutions}**",
        "",
        "| Official Apple model manual | Expected catalog entries | Named anchors matched | Newly linked |",
        "|---|---:|---:|---:|",
    ]
    for title, url, total, matched, updated in page_stats:
        summary.append(f"| [{title}]({url}) | {total} | {matched} | {updated} |")
    if failures:
        summary += ["", "## Unavailable Apple manual pages"]
        summary += [f"- [{name}]({url}): {reason}" for name, url, reason in failures]
    if conflict:
        summary += ["", "## Existing link conflicts — NOT auto-replaced"]
        summary += [f"- {manual} / {title}: prior {prior}; source TOC {found}"
                    for manual, _, title, prior, found in conflict]
    if no_exact:
        summary += ["", "## Named headings without a unique direct article URL"]
        summary += [f"- {manual}: {title} — {why}" for manual, title, why in no_exact]
    summary += ["", "## Interpretation",
                "A manual TOC **section heading** (such as Views, Parts, and Tools)",
                "may have subordinate Internal View / Orderable Parts / Screws /",
                "Tools links but no direct page for the heading itself.",
                "Do not invent or substitute a link in those cases.",
                "This link audit does NOT mean a physical repair step was reviewed,",
                "nor does any heading prove Apple recommends a component for a panic code.",
                "No customer logs or user data are downloaded or submitted.",
                ""]

    print(f"SUMMARY: fetched={reviewed}/{len(pages)} new={len(updates)} "
          f"direct={direct} pending={pending} conflicts={len(conflict)} "
          f"fetch_failures={len(failures)}", flush=True)
    if args.write:
        CHAPTERS.write_text("".join(revised), encoding="utf-8")
        REPORT.write_text("\n".join(summary), encoding="utf-8")
    if reviewed == 0:
        raise RuntimeError("No Apple manual page could be verified: refuse to mark catalog complete")
    if conflict:
        # Preserve previously manually reviewed URLs, record both candidates
        # and make this a visible follow-up instead of discarding all the
        # other newly verified, non-conflicting links from this audit.
        for manual, chapter_id, name, prior, found in conflict:
            print(f"REVIEW_REQUIRED: {manual} / {chapter_id} / {name}: "
                  f"prior={prior} AppleTOC={found}", file=sys.stderr, flush=True)
    return 0

if __name__ == "__main__":
    sys.exit(main())
