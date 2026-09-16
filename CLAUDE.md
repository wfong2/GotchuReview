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

### Google Sign-In Setup (TODO)
- Debug SHA-1: `04:2A:6C:E5:B3:4E:09:38:AC:E7:F2:92:7F:BE:A7:3B:E5:AE:C0:24`
- An Android OAuth client ID needs to be created in Google Cloud Console (project `689036353712`) with the above SHA-1 and package name `com.gotchureviews.app`
- The emulator needs a Google account signed in (requires non-corporate network)

### Remaining Tasks
- [ ] Register Android OAuth client in Google Cloud Console
- [ ] Sign into Google account on emulator (use personal network)
- [ ] Test full Google Sign-In flow end-to-end
- [ ] Replace placeholder launcher icons with real app icon (use Android Studio Image Asset tool)
