# HAQOOQ — Multilingual Citizen Legal Guide & Safety App

**Build with AI Multan 2026 (SKYRA Pakistan)**  
Repository: https://github.com/AqibTayyab/haqooq-mobile-app

> **Know your rights. Stay safe. Reach the right lawyer.**

HAQOOQ provides Pakistani citizens with plain-language offline legal rights guidance, a grounded AI legal information assistant, an Emergency SOS & Safety Check-In hub, and a demo Lawyer Directory — built with **full English, Urdu (RTL), and Roman Urdu** localization and **Android 17 Material 3 Expressive** Dark/Light theming.

## Implemented Features & Architecture

- **Member 1 — Multilingual UI & Material 3 Expressive Architecture (`com.example.l10n`, `com.example.ui`)**
  - Complete string tables (`AppStrings`) for **English (`en`)**, **Urdu (`ur` with automatic `LayoutDirection.Rtl`)**, and **Roman Urdu (`roman_ur`)** persisted via `LocaleController`.
  - Default true-black (`#000000`) Dark Mode + Light Mode toggle in Top Bar, Onboarding, and Settings, with locally bundled fonts (`Plus Jakarta Sans`, `Inter`, `Noto Naskh Arabic`).
  - Adaptive navigation (`ExpressiveFloatingBottomDock` on compact widths, `NavigationRail` on tablets/foldables).
- **Member 4 — SOS, Safety Check-In, Lawyer Marketplace & QA (`com.example.services.sos`, `com.example.services.lawyers`)**
  - **SOS Foundation (`PersistentSosService`, `SosDeviceHelper`)**: Persistent Trusted Contacts CRUD (add, edit, primary toggle, delete, max 5) with Pakistani mobile validation (`03XXXXXXXXX` / `+923XXXXXXXXX`), runtime Location & Notification permission checks + App Settings shortcut, emergency helplines (`15`, `1122`, `1099`, `1991`), and local SOS incident history.
  - **SOS Alert & Check-In Flow**: 3-second cancelable countdown with tactile vibration, real GPS (`LocationManagerCompat` with `getLastKnownLocation` fallback) + battery percentage, multilingual SOS alerts with Google Maps coordinates link, honest pre-filled `ACTION_SENDTO` (`smsto:`), Share Sheet, Copy, and `ACTION_DIAL` (`tel:`) intents, plus persistent Check-In countdown with local notification on expiry.
  - **Lawyer Marketplace (`JsonLawyerService` + `assets/mock/lawyers.json`)**: 4 fictional demo profiles (`is_demo = true`) with search, city, specialization, and spoken language filters, scripted multilingual demo chat, demo consultation booking, explicit user consent before attaching a `LawyerBriefingPack`, and post-booking demo reviews.

## Legal & Safety Principles & Known Limitations

- **General Legal Information Only**: HAQOOQ provides general legal information, not legal advice. Every AI response, profile footer, and dashboard displays the mandatory disclaimer.
- **Honest SMS & Calling Behavior**: To comply with Google Play policy and protect user consent, HAQOOQ never sends silent background SMS or places calls without user action. Tapping **Send SMS Alert** or **Call** opens the device SMS/Dialer app with a pre-filled recipient and message so the user taps Send/Call.
- **Background Check-In Caveat**: The Safety Check-In deadline is persisted locally across app restarts, while live countdown ticking runs while the app process is active; aggressive battery savers may pause timers if the OS kills the process.
- **Demo Lawyer Profiles**: All advocate profiles, ratings, bookings, and replies are fictional demo accounts explicitly labeled `Demo Account - Not a Real Lawyer`.
- **Emergency Numbers**: Helplines (`15`, `1122`, `1099`, `1991`) should be verified locally before relying on them in a critical emergency.

## Manual QA Checklist (Member 4)

| ID | Feature / Scenario | Expected Result | Status |
| :--- | :--- | :--- | :--- |
| QA-01 | Trusted Contacts CRUD & Validation | Invalid numbers rejected; max 5 enforced; edit, primary star, delete persist across restart | Pass |
| QA-02 | Permissions & App Settings Flow | Runtime Location & Notification prompt works; denied state offers App Settings shortcut | Pass |
| QA-03 | 3-Second SOS Countdown & Cancel | Countdown vibrates 3→2→1; tapping Cancel aborts without logging an incident | Pass |
| QA-04 | SOS GPS + Battery + Honest SMS/Share | Attaches `maps.google.com/?q=lat,lng` when available or honest GPS-off text; opens pre-filled `smsto:` / Share / Copy cleanly | Pass |
| QA-05 | Safety Check-In Timer | 15/30/60 min presets count down, persist deadline, cancel via "I'm Safe", and trigger alert + notification at `00:00` | Pass |
| QA-06 | Emergency Helplines Dialer | `15`, `1122`, `1099`, `1991` show verify-locally notice and open `ACTION_DIAL` without auto-calling | Pass |
| QA-07 | Lawyer Search, Filters & Demo Banner | Search + city/topic/language chips filter `lawyers.json`; `"Demo Account - Not a Real Lawyer"` visible everywhere | Pass |
| QA-08 | Demo Booking, Consent & Reviews | Explicit consent toggle for `LawyerBriefingPack`; Demo review dialog unlocked only after booking | Pass |
| QA-09 | Multilingual & RTL + Dark/Light Mode | All Member 4 labels, dialogs, and SOS templates render cleanly in `en`, `ur` (RTL), and `roman_ur` in Dark & Light themes | Pass |

## Build & Test

```bash
gradle :app:assembleDebug
gradle :app:testDebugUnitTest --tests "com.example.HaqooqArchitectureTest" --tests "com.example.HaqooqMember4Test"
```
