GEL iDoctor — SMART SERVICE QR
==============================

PURPOSE
-------
One QR created by the technician carries the current Service Session.

Installed iDoctor:
  QR -> HTTPS Smart Service link -> iDoctor -> automatic claimServiceSession

Not installed (after Play/Internal Testing exists):
  QR -> HTTPS Smart Service link -> Google Play -> Install -> Open
     -> Install Referrer restores the same Service Session -> automatic pairing

FILES ADDED / CHANGED
---------------------
Android:
  app/src/main/AndroidManifest.xml
  app/build.gradle
  app/src/main/java/com/gel/cleaner/RepairDeviceActivity.java
  app/src/main/java/com/gel/cleaner/ConnectToTechnicianActivity.java
  app/src/main/java/com/gel/cleaner/MainActivity.java
  app/src/main/java/com/gel/cleaner/GELSmartServiceLink.java             NEW
  app/src/main/java/com/gel/cleaner/GELInstallReferrerManager.java      NEW

Firebase Hosting:
  firebase.smartqr.json
  hosting/connect/index.html

Future direct App Link verification:
  templates/assetlinks_TEMPLATE.json

IMPORTANT
---------
No Cloud Functions changes are required for Smart Service QR.
The already deployed createServiceSession / claimServiceSession backend remains unchanged.
The current 6-digit Service Code remains visible and usable as fallback.

The manifest already declares the HTTPS App Link, but verified direct-open requires
/.well-known/assetlinks.json with the real signing SHA-256. Until then the hosted
page uses an Android intent fallback to open the installed app. After Play App Signing
is established, deploy assetlinks.json and the browser hop can disappear on verified devices.

Firebase Hosting is kept in firebase.smartqr.json so it does NOT overwrite the existing
firebase.json used by Cloud Functions.
