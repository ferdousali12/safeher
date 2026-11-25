# SafeHer – Final Project Documentation & Verification Report

## 1. Project Overview
**SafeHer** is a comprehensive personal safety companion Android application designed to help women feel secure during their daily commutes, university journeys, public transport use, and unfamiliar environments.

## 2. Problem Statement
Personal safety concerns and vulnerability during transit are major challenges. In emergency situations, victims often struggle to quickly unlock their phones, dial emergency numbers, or articulate their precise location to family members or authorities.

## 3. Proposed Solution
SafeHer provides an intuitive, highly accessible, and rapid-response safety toolkit built natively for Android using Jetpack Compose and Firebase:
- **Instant SOS Alert**: One-tap emergency activation with countdown buffer against false triggers.
- **GPS Location Sharing**: Automatic Google Maps location link generation and transmission via SMS.
- **Trusted Contacts Management**: Store and organize emergency contacts with preferred recipient priorities.
- **Safe Journey & Check-In Timer**: Track travel destinations and ensure timely arrival check-ins.
- **Fake Incoming Call**: Simulated phone call screen to diffuse uncomfortable situations discreetly.

## 4. Main Features
1. **User Authentication & Profile**: Secure registration, email/password login, and Firestore-backed user records.
2. **Trusted Contacts**: Add, edit, delete, and mark preferred emergency recipients.
3. **SOS Emergency System**: 5-second cancelable countdown, FusedLocationProviderClient GPS retrieval, SMS alert dispatch, and Firestore emergency alert logging.
4. **Safe Journey Tracker**: Destination input, duration monitoring, and arrival confirmation.
5. **Safety Check-In Timer**: Countdown timer with safety confirmation prompt upon expiration.
6. **Fake Incoming Call**: Realistic incoming call simulation screen.
7. **Emergency History**: Chronological log of past SOS alerts and timestamps.

## 5. Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Architecture**: MVVM with Repository Pattern & StateFlow
- **Backend & Database**: Firebase Authentication & Cloud Firestore
- **Location Services**: Google Play Services FusedLocationProviderClient
- **Build System**: Gradle Kotlin DSL

## 6. System Architecture & Folder Structure
- `data/model/`: Data entities (User, Contact, Alert, Journey)
- `data/repository/`: Data repositories (AuthRepository, UserRepository, ContactRepository)
- `presentation/`: ViewModels with StateFlow state management
- `navigation/`: Centralized `SafeHerNavGraph` & `Screen` routes
- `ui/theme/`: Material 3 Color palette, Typography, and Theme
- `ui/screens/`: Feature screens (Splash, Onboarding, Login, Register, Home, Contacts, SafeJourney, Emergency, Profile, Settings, FakeCall)
- `components/`: Reusable UI elements (`SOSButton`, `SafeHerButton`, `SafeHerTextField`)
- `services/`: Location services helper

## 7. Database Structure (Cloud Firestore)
- `users/{uid}`: Full name, email, phone number, creation timestamp.
- `contacts/{contactId}`: User association, name, phone number, relationship, preferred status.
- `emergency_alerts/{alertId}`: User association, latitude, longitude, timestamp, status.
- `safe_journeys/{journeyId}`: User association, destination, start time, expected duration, status.

## 8. Android Permissions & Security
- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: Required for GPS location retrieval during emergencies.
- `SEND_SMS`: Required for sending direct emergency alerts with Google Maps location links.
- **Security**: Firestore security rules restrict read/write access strictly to authenticated document owners (`request.auth != null && request.auth.uid == resource.data.userId`).

## 9. Final Verification Report

| Feature / Module | Status | Verification Notes |
| :--- | :--- | :--- |
| Firebase Authentication & Login/Register | **PASS** | Verified registration, login, logout, and session state. |
| User Profile & Firestore Sync | **PASS** | Verified user data persistence and retrieval. |
| Trusted Contacts Management | **PASS** | Verified adding, editing, deleting, and preferred status. |
| Home Dashboard & Navigation | **PASS** | Verified Compose navigation across all 11 screens. |
| SOS Emergency System & Countdown | **PASS** | Verified 5-second countdown, cancellation, and alert logging. |
| GPS Location & Google Maps Links | **PASS** | Verified FusedLocationProviderClient integration. |
| SMS Emergency Dispatch | **PASS** | Verified SMS intent generation with location link. |
| Safe Journey Tracker | **PASS** | Verified journey initiation and completion logging. |
| Safety Check-In Timer | **PASS** | Verified countdown timer and expiration check-in prompt. |
| Fake Incoming Call | **PASS** | Verified simulated call screen and decline/accept actions. |
| Emergency History Log | **PASS** | Verified chronological display of past alerts. |
| Gradle Build & Compilation | **PASS** | Successfully compiled and built debug APK (`BUILD SUCCESSFUL`). |

## 10. Conclusion & Demonstration Readiness
The SafeHer application is fully integrated, thoroughly secured with Firestore rules, compiled successfully without warnings or errors, and is fully prepared for university project demonstration and testing on Android devices.
