# GEL iDoctor — iPhone Apple source retention in HTML PDF and TXT PDF

**Audit date:** 2026-11-22  
**Review branch:** `research/iphone-panic-repair-knowledge-20261121` (draft PR #181)

## Why this repair-knowledge QA is necessary

An official Apple troubleshooting URL and an independently published repair-history URL are evidence sources, not a verified component diagnosis. Both must remain readable when Lab 4 logs are exported. An existing path rendered clickable source URLs in the iPhone Lab 4 Android screen, but the HTML/PDF report writer escaped those URLs as ordinary text. The original Canvas TXT/PDF printer drew each entire log line without width-aware wrapping; long source URLs could extend beyond the printable page width.

## Implemented corrections

### HTML PDF source links

- Added `GELReportSourceLinks.escapeAndLinkify()` (pure Java).
- The HTML print renderer now uses it for recorded full lines and value spans from the exact `GELServiceLog.PdfRow` metadata.
- The allowlist is limited to exact HTTPS hosts `support.apple.com`, `www.ifixit.com` and `forum.nopowerfix.com`. Spoofed hosts remain plain, HTML-escaped text. HTML special characters in logs and URLs are escaped before building any anchor.
- The `source-link` print stylesheet inherits the existing source/value color, underlines the link and allows long URL wrapping.
- No third-party URL is automatically fetched or uploaded. A WebView print job still requires the person to choose **Save as PDF**; issuing the Android print dialog is not file-saving confirmation.

### Canvas TXT PDF retains long URLs

- Added `GELPdfMeasuredLineWrap.split()` with measured text width (the actual PDF `Paint.measureText`), rather than a character-count guess.
- The TXT/PDF renderer prints all fragments over as many lines and pages as necessary and never intentionally truncates a long Apple source URL.
- Fragments retain their original source offsets, so the actual recorded value colors are re-applied across a wrapped line. Neither threshold nor panic keywords determine colors.
- Canvas TXT/PDF preserves source URL **text**; it is **not** claimed that Android Canvas embeds tappable hyperlinks. For clickable links, use HTML PDF.

## Automated regression checks

| Test class | Contract |
|---|---|
| `GELReportSourceLinksTest` | Apple/iFixit/NoPowerFix URLs become safe anchors; hostile tags and spoofed sources remain escaped; punctuation is preserved outside links |
| `GELPdfMeasuredLineWrapTest` | Exact lossless reconstruction of long URLs, emoji and multibyte characters, measured line limits, original color offsets |
| `IPhoneAppleSourceReportExportContractTest` | Lab 4 manufacturer/community sources reach `GELServiceLog`; HTML/TXT renderers use the new functions; print UI is not misreported as a saved file |
| `GELServiceLogPdfColorTest` | Existing screen/PDF value-color semantics still hold after adding measured wrapping and safe source hyperlink rendering |

**GitHub Actions:** [iPhone repair knowledge base integrity QA — run 44972023013](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/44972023013): **completed SUCCESS**, including Android Java compilation and full JVM test suite on the source changes.

An initial run [44971898784](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/44971898784) identified two stale source-contract tests that explicitly expected the old one-line TXT PDF rendering and plain-text HTML output. They were updated to assert equivalent color integrity under the new, safer rendering approach. The final run passed.

## Boundaries / outstanding acceptance checks

This audit validates the program logic and test suite, **not a physically printed document**. Before merging a production APK:

1. Run a real sourced iPhone panic example in Greek and English; verify Lab 4 shows original Apple and community source links with the right type labels.
2. Export via **HTML PDF**, use Android system print to save the file and open it to confirm tappable links and wrapped long addresses across pages.
3. Export via **TXT PDF**, confirm the full text of very long official support URLs is present on the PDF pages and that source information is not clipped.
4. Confirm value colors remain neutral blue or white for information, green for actually verified OK, yellow for a real caution and red only for confirmed failure. Self-reported repairs must never appear as green device verdicts.
5. Verify exports neither leak the imported raw panic contents beyond intended summary nor claim an Apple-certified repair for an unsupported model/code.

The draft PR remains unmerged; main and the protected backup are unchanged. The 1,074 linked Apple manual chapters and 447 fetched article structures are **not** proof that every physical procedure has been individually reviewed or that all panic signatures have confirmed fixes.
