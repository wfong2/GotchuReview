# ReviewApp - GotchuReviews

## Project Overview
GotchuReviews is a contractor review app. It has an iOS app (App Store ready) and an Android app (in development).

## Architecture

### iOS
- Swift/SwiftUI app located in the root project directory
- Already submitted/preparing for App Store

### Android
- Located in `android/GotchuReviews/`
- Kotlin + Jetpack Compose
- Hilt for dependency injection
- Credential Manager API for Google Sign-In
- Firebase Auth REST API (no google-services.json — uses REST directly)
- Retrofit + OkHttp for networking
- DataStore for local token persistence
- Package name: `com.gotchureviews.app`

### Backend
- API hosted on AWS App Runner: `https://xduzpwxmsp.us-east-1.awsapprunner.com/api/v1/`
- Firebase Auth for token exchange
- Google OAuth Web Client ID: `689036353712-1do1c716605eh0ivss1dautter73v6mjc.apps.googleusercontent.com`

## Android Setup Notes

### Corporate Network (Mercedes-Benz proxy)
- The corporate proxy (Corp-Proxy01-G2) intercepts HTTPS with its own CA (Corp-Prj-Root-CA / Daimler AG)
- The corporate root CA was imported into the JDK trust store at:
  `~/Library/Java/JavaVirtualMachines/jbr-21.0.11/Contents/Home/lib/security/cacerts`
  alias: `mercedes-corp-root-ca`
- Gradle builds work through the proxy after this import
- The Android emulator cannot sign into Google through the proxy — switch to a personal network to add a Google account

### Google Sign-In Setup (Done)
- Debug SHA-1: `04:2A:6C:E5:B3:4E:09:38:AC:E7:F2:92:7F:BE:A7:3B:E5:AE:C0:24`
- Android OAuth client registered in Google Cloud Console (project `689036353712`)
- Web Client ID used for Credential Manager: `689036353712-0ii028k1qi6f6t4q5f6iklv60ulkf497.apps.googleusercontent.com`
- Google Sign-In tested and working on physical device

### Completed Android Tasks
- [x] Register Android OAuth client in Google Cloud Console
- [x] Google Sign-In working end-to-end
- [x] App icon replaced with iOS icon
- [x] Guest access (no login required to browse)
- [x] Account deletion in settings
- [x] Camera permission handling
- [x] Review form submit button fix

## Google Play Store Publishing Plan

### 1. Developer Account
- Sign up at https://play.google.com/console
- One-time $25 registration fee

### 2. Generate Signed Release AAB
- In Android Studio: Build > Generate Signed Bundle / APK
- Choose **Android App Bundle (AAB)**
- Create a new keystore (back up the keystore file and passwords — cannot be recovered)
- Select **release** build type

### 3. Pre-Release Checklist
- [ ] Verify `BASE_URL` in `AppModule.kt` points to production API
- [ ] Remove or gate `HttpLoggingInterceptor` behind `BuildConfig.DEBUG` (currently logs all request/response bodies)
- [ ] Set `versionCode` and `versionName` in `app/build.gradle.kts`
- [ ] Test the release build on a real device
- [ ] Generate a release signing key (keystore) and back it up securely

### 4. Store Listing Assets Needed
- App icon: 512x512 PNG (already have from iOS)
- Feature graphic: 1024x500 PNG
- Screenshots: at least 2 phone screenshots (min 320px, max 3840px)
- Short description: up to 80 characters
- Full description: up to 4000 characters
- Privacy policy URL (reuse iOS privacy policy page)

### 5. Play Console Setup
- Create app in Play Console
- Fill out Store listing (title, descriptions, screenshots, icon)
- Complete Content rating questionnaire
- Set Pricing & distribution (free)
- Complete Data safety form (data collected, usage)
- Set target audience and content settings

### 6. Upload & Release
- Go to Production > Create new release
- Upload the `.aab` file
- Add release notes
- Review and roll out
- Google review typically takes a few hours to a few days

## App Store Review - Rejection (October 5, 2026)

Submission ID: 41ccd220-46fc-4b04-8ec2-fc689d6264a9
Version reviewed: 1.0 (2)
Devices: iPhone 17 Pro Max, iPad Air 11-inch (M3)

### Issue 1: Guideline 5.1.1(v) - Guest Access Required
**Status:** [x] Done
The app requires login before browsing reviews and services. Apple requires that non-account-based features be freely accessible without registration.
**Fix applied:** Added "Continue as Guest" option on onboarding. Guests can browse the Explore tab freely. Scan and History tabs show a sign-in prompt. Settings accessible from Explore tab gear icon.

### Issue 2: Guideline 5.1.1(v) - Account Deletion Required
**Status:** [x] Done
The app supports account creation but has no account deletion option.
**Fix applied:** Added "Delete Account" button in Settings with confirmation alert. Calls DELETE /users/me endpoint. After deletion, user is signed out and stays in guest mode. Backend endpoint needs to exist (DELETE /users/me).

### Issue 3: Guideline 2.1(b) - Business Model Clarification
**Status:** [x] Done (reply drafted)
Apple wants to understand the business model.
**Fix:** Reply drafted explaining the app is free, credits are earned (not purchased), no paid content/subscriptions/IAP.
