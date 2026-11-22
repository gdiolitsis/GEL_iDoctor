#!/usr/bin/env python3
"""Extract Apple public article *structure*, never unaudited repair instructions.

A DIRECT_CHAPTER link is not a source-reviewed physical repair procedure.
This script records only exact article identity, headings and yes/no presence
of source sections, not copyrighted manual bodies or repair prescriptions.
It stops politely on repeated rate limits and logs incompleteness.
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
CATALOG = ROOT / "app/src/main/assets/iphone_apple_manual_chapters_v1.tsv"
OUTPUT = ROOT / "docs/qa/APPLE_SOURCE_ARTICLE_STRUCTURE_AUDIT_2026-11-22.tsv"
REPORT = ROOT / "docs/qa/APPLE_SOURCE_ARTICLE_STRUCTURE_AUDIT_2026-11-22.md"
FIELDS = ["url", "chapter_links", "manual_count", "status", "title",
          "section_headings", "has_before_you_begin", "has_tools",
          "has_removal", "has_reassembly", "has_diagnostics",
          "has_warning", "mentions_repair_assistant", "date"]

def official(url):
    parsed = urlparse(url)
    return parsed.scheme == "https" and parsed.netloc == "support.apple.com" and bool(
        re.fullmatch(r"/(?:[a-z]{2}(?:-[a-z]{2})?/)?[0-9]{5,7}/?", parsed.path))

class Headings(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.headings = []
        self.tag = None
        self.parts = []
        self.text = []
        self.suppress = None

    def handle_starttag(self, tag, attrs):
        if tag in ("script", "style", "svg", "noscript"):
            self.suppress = tag
            return
        if self.suppress:
            return
        if tag in ("h1", "h2", "h3"):
            self.tag = tag
            self.parts = []

    def handle_data(self, data):
        if self.suppress:
            return
        if self.tag is not None:
            self.parts.append(data)
        elif len(self.text) < 30000:
            self.text.append(data)

    def handle_endtag(self, tag):
        if tag == self.suppress:
            self.suppress = None
            return
        if self.suppress:
            return
        if tag == self.tag:
            name = " ".join(" ".join(self.parts).split())
            if 0 < len(name) < 180:
                self.headings.append((tag, name))
            self.tag = None
            self.parts = []

def index():
    docs = {}
    with CATALOG.open(encoding="utf-8") as handle:
        for cells in csv.reader(handle, delimiter="\t"):
            if not cells or cells[0].startswith("#") or cells[0] == "entry_id":
                continue
            if len(cells) != 8 or cells[7] != "DIRECT_CHAPTER" or not official(cells[6]):
                raise ValueError("Unverified chapter URL or field count")
            item = docs.setdefault(cells[6], {"count": 0, "manuals": set()})
            item["count"] += 1
            item["manuals"].add(cells[1])
    if sum(d["count"] for d in docs.values()) != 1074:
        raise ValueError("Catalog count changed without review")
    return docs

def fetch(url):
    request = Request(url, headers={
        "User-Agent": "Mozilla/5.0 (compatible; GEL-iDoctor-Apple-Source-Structure-Audit/1.0)",
        "Accept": "text/html",
        "Accept-Language": "en-US,en;q=0.9",
    })
    try:
        with urlopen(request, timeout=20) as response:
            actual = response.geturl()
            if not official(actual):
                raise ValueError("Not an official Apple article after redirect")
            if urlparse(actual).path.rstrip("/").split("/")[-1] != urlparse(url).path.rstrip("/").split("/")[-1]:
                raise ValueError("Article ID changed after redirect")
            if response.status != 200:
                raise ValueError("Non-success Apple response")
            body = response.read(3_000_000)
            if len(body) < 1500:
                raise ValueError("Unusually short document")
            return body.decode("utf-8", "replace")
    except HTTPError as exc:
        raise RuntimeError("RATE_LIMITED" if exc.code == 429 else "HTTP_" + str(exc.code)) from exc
    except (URLError, TimeoutError, ValueError) as exc:
        raise RuntimeError(type(exc).__name__.upper()) from exc

def parse(url, info, html):
    reader = Headings()
    reader.feed(html)
    title = next((name for tag, name in reader.headings if tag == "h1"), "")
    if not title:
        raise RuntimeError("NO_APPLE_ARTICLE_HEADING")
    names = [name for tag, name in reader.headings if tag in ("h2", "h3")]
    low = " ".join(names).casefold()
    body = " ".join(reader.text).casefold()
    main = [name.replace(";", "/").replace("\t", " ")
            for tag, name in reader.headings if tag == "h2"][:14]
    return dict(url=url, chapter_links=info["count"], manual_count=len(info["manuals"]),
                status="STRUCTURE_PARSED_ONLY", title=title[:130],
                section_headings="; ".join(main)[:650],
                has_before_you_begin=str("before you begin" in low).lower(),
                has_tools=str("tools" in low).lower(),
                has_removal=str("removal" in low).lower(),
                has_reassembly=str("reassembly" in low).lower(),
                has_diagnostics=str("diagnostic" in low).lower(),
                has_warning=str("warning" in low).lower(),
                mentions_repair_assistant=str("repair assistant" in body).lower(),
                date=date.today().isoformat())

def load():
    if not OUTPUT.is_file(): return {}
    with OUTPUT.open(encoding="utf-8", newline="") as handle:
        reader = csv.DictReader((line for line in handle if not line.startswith("#")), delimiter="\t")
        if reader.fieldnames != FIELDS:
            raise ValueError("Source-structure audit fields changed")
        return {row["url"]: row for row in reader}

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--write", action="store_true")
    p.add_argument("--max-pages", type=int, default=447)
    p.add_argument("--delay", type=float, default=1.35)
    args = p.parse_args()
    sources = index()
    records = {url: row for url, row in load().items() if url in sources}
    ordered = sorted(sources, key=lambda url: (-sources[url]["count"], url))
    examined, errors, rate_limited = 0, 0, 0
    for url in ordered:
        if records.get(url, {}).get("status") == "STRUCTURE_PARSED_ONLY":
            continue
        if examined >= args.max_pages or rate_limited >= 4:
            break
        examined += 1
        try:
            records[url] = parse(url, sources[url], fetch(url))
            rate_limited = 0
        except RuntimeError as ex:
            errors += 1
            reason = str(ex)
            if reason == "RATE_LIMITED":
                rate_limited += 1
            records[url] = dict(url=url, chapter_links=sources[url]["count"],
                                manual_count=len(sources[url]["manuals"]),
                                status=reason, date=date.today().isoformat())
        if examined % 20 == 0:
            print("Source-check", examined, "errors", errors, flush=True)
        time.sleep(max(args.delay, 0.5) + (6 if rate_limited else 0))
    for url, item in sources.items():
        records.setdefault(url, dict(url=url, chapter_links=item["count"],
                                     manual_count=len(item["manuals"]),
                                     status="NOT_YET_FETCHED", date=""))
    verified = [url for url, row in records.items() if row["status"] == "STRUCTURE_PARSED_ONLY"]
    unverified = [url for url, row in records.items() if row["status"] != "STRUCTURE_PARSED_ONLY"]
    linked = sum(sources[url]["count"] for url in verified)
    if args.write:
        OUTPUT.parent.mkdir(parents=True, exist_ok=True)
        with OUTPUT.open("w", encoding="utf-8", newline="") as handle:
            handle.write("# Machine-extracted HTML structure only, NOT technical review or completed repair instructions\n")
            writer = csv.DictWriter(handle, fieldnames=FIELDS, delimiter="\t",
                                    lineterminator="\n", extrasaction="ignore")
            writer.writeheader()
            for url in sorted(sources):
                writer.writerow(records[url])
        lines = [
            "# Official Apple iPhone article structural source audit",
            "",
            "This is machine-verified article metadata, **NOT** individual",
            "expert review of complete procedures or permission to perform repair.",
            "No full Apple article text is stored in the repository.",
            "",
            f"- Official chapter links: **1074/1074**",
            f"- Distinct first-party Apple articles: **{len(sources)}**",
            f"- Article structures fetched and parsed: **{len(verified)}**",
            f"- Linked chapters with structural verification: **{linked}**",
            f"- Not yet verified / unavailable: **{len(unverified)}**",
            f"- HTTP/parse failures in this attempt: **{errors}**",
            "",
            "The TSV stores only official article title, short section heading",
            "labels, and binary structural flags. A tools, removal, warning,",
            "diagnostics or Repair Assistant flag is **not** a repair direction",
            "and does not indicate eligibility or that any test was performed.",
            "See the complete official source URL for illustrated procedure",
            "details and model-specific safety precautions.",
            "",
            "Existing original Greek/English technical notes and audited",
            "procedure-stage summaries remain separate and must be reviewed",
            "individually. No panic-code-to-replacement inference is made.",
            ""
        ]
        if unverified:
            lines += ["## Unverified pages (first 30)"]
            lines += ["- " + url + ": " + records[url]["status"] for url in unverified[:30]]
        REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"AUDIT VERIFIED {len(verified)}/{len(sources)} ARTICLE STRUCTURES; "
          f"{linked}/1074 CHAPTER REFERENCES; errors={errors}", flush=True)
    if not verified:
        raise RuntimeError("No source articles were accessible; no success claim")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
