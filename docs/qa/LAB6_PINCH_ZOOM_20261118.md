# GEL iDoctor — Lab 6 verified two-finger pinch-to-zoom (QA, 2026-11-18)

## Objective
Add functional multi-touch zoom verification to existing Lab 6, without a new numbered Lab, removing the 96-zone touch test or changing its color/uniformity PRO stage.

## User flow
1. Lab 6 tests the existing 96 independent touch targets. Canceled/incomplete grid tests terminate exactly as before.
2. After **successful** grid verification, the user sees a new dark, portrait-only two-finger zoom canvas.
3. With **two distinct simultaneous MotionEvent pointer IDs**, spreading the fingers must increase their separation >=22% and >=42 dp; pinching the fingers must decrease it >=18% and >=42 dp. Two directions can be completed in one or two gestures.
4. The circular GEL drawing visibly scales as gestures occur. A green direction label appears only after the corresponding independent threshold is actually met.
5. **CONTINUE** is enabled only after both directions. **SKIP** records no overall PASS and proceeds to existing display-color PRO. **EXIT** and Android Back terminate without starting the next Lab phase.
6. The result is delivered only by activity-result extras from this run. Exported logs/PDF receive verified positive or neutral incomplete/skip findings. No touch coordinates, fingerprints or screenshots are stored.

## What is proven
An Android application received genuine two-pointer touch events and an observed increase **and** decrease in inter-pointer distance sufficient to drive the local zoom visualization. This is not a calibrated digitizer inspection, proof of accuracy across the entire panel, or a promise of zoom in all external apps.

## Validation
- Pure JVM tests: stationary fingers, translation, jitter, invalid spans, incomplete direction, one or two successful gestures, and post-up events.
- Source regression tests: 96-grid -> pinch -> color PRO, status colors, fresh result extras, skip/cancel, portrait, non-exported Activity.
- Android device QA: both directions on Android 11/13/16, simultaneous two-pointer identification, 3-finger interruption, lift/re-touch, back/exit/skip, small screens and Greek/English.
- Confirm HTML/PDF export preserves the existing `logLabelOkValue` greens and neutral `logLabelValue` fields.
- No main, protected backup, signed QA APK, Play release or previous Lab modifications until physical QA passes.
