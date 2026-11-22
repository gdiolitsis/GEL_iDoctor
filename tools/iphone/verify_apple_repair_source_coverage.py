#!/usr/bin/env python3
"""Offline truth-in-coverage regression test for the iPhone Apple corpus.

Metadata-only source verification, separately curated bilingual technical
findings and detailed model-specific stage notes are never conflated.
No network access or customer panic files are read by this script.
"""
import csv
from pathlib import Path
from collections import Counter, defaultdict
from urllib.parse import urlsplit

ROOT = Path(__file__).resolve().parents[2]
ASSET = ROOT / "app/src/main/assets"
QA = ROOT / "docs/qa"

def rows(path, expected_header):
    with path.open(encoding="utf-8", newline="") as handle:
        clean = (l for l in handle if l.strip() and not l.startswith("#"))
        reader = csv.reader(clean, delimiter="\t")
        header = next(reader)
        if header[0] != expected_header:
            raise AssertionError(f"Bad {path.name} header: {header[0]}")
        data = [r for r in reader if r]
    return data

def support_link(value):
    uri = urlsplit(value)
    return (uri.scheme == "https" and uri.netloc == "support.apple.com"
            and uri.path.strip("/").split("/")[-1].isdigit()
            and not uri.query and not uri.fragment)

def main():
    chapters = rows(ASSET / "iphone_apple_manual_chapters_v1.tsv", "entry_id")
    facts = rows(ASSET / "iphone_apple_technical_findings_v1.tsv", "fact_id")
    stages = rows(ASSET / "iphone_apple_service_steps_v1.tsv", "step_id")
    audit = rows(QA / "APPLE_SOURCE_ARTICLE_STRUCTURE_AUDIT_2026-11-22.tsv", "url")

    assert len(chapters) == 1074 and all(len(r) == 8 for r in chapters)
    assert all(r[7] == "DIRECT_CHAPTER" and support_link(r[6]) for r in chapters)
    assert len({r[0] for r in chapters}) == len(chapters)

    urls = Counter(r[6] for r in chapters)
    manuals = defaultdict(set)
    for row in chapters: manuals[row[6]].add(row[1])
    assert len(urls) == 447
    assert len(audit) == 447 and all(len(r) == 14 for r in audit)
    assert len({r[0] for r in audit}) == len(audit)
    assert {r[0] for r in audit} == set(urls)
    for row in audit:
        assert support_link(row[0])
        assert int(row[1]) == urls[row[0]]
        assert int(row[2]) == len(manuals[row[0]])
        assert row[3] == "STRUCTURE_PARSED_ONLY", (row[0], row[3])
        assert row[4].strip(), "Apple article page title cannot be blank"
        assert all(value in ("true", "false") for value in row[6:13])
        assert row[13], "Source fetch date is required"

    assert len(facts) == 120 and len({r[0] for r in facts}) == 120
    assert len(stages) == 62 and len({r[0] for r in stages}) == 62
    valid_scopes = {(r[6], r[2], r[3]) for r in chapters}
    for r in facts:
        assert len(r) == 9 and support_link(r[1])
        assert (r[1], r[2], r[3]) in valid_scopes
        assert r[6].strip() and r[7].strip()
        assert r[8], "Each reviewed technical fact must carry a review date"
    fact_urls = {r[1] for r in facts}
    assert len(fact_urls) == 12
    covered = sum((r[6], r[2], r[3]) in
                  {(f[1], f[2], f[3]) for f in facts} for r in chapters)
    assert covered == 520
    stage_by_id = {r[0]: r[6] for r in chapters}
    for r in stages:
        assert len(r) == 8 and r[1] in stage_by_id
        assert r[6] == stage_by_id[r[1]]
    assert len({r[1] for r in stages}) == 10
    print("PASS: 30 manuals, 1074 model chapter entries, 447 Apple article")
    print("structures, 120 unique curated notes from 12 sources, 520")
    print("topic-bounded chapter matches and 62 stage records / 10 chapters")
    print("No auto-derived hardware faults or copied repair text.")

if __name__ == "__main__":
    main()
