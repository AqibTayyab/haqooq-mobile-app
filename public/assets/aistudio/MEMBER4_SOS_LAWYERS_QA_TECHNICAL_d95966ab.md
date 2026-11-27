# Member 4 - SOS, Safety Check-In, Lawyer Marketplace, QA, Submission
Branch: feature/sos-lawyer | Repo: https://github.com/AqibTayyab/haqooq-mobile-app
First read docs/00_CRITICAL_RULES.md. In every new AI chat paste members/00_MASTER_CONTEXT_PROMPT.md first, then the phase prompt.

## Files you own
- lib/screens/sos/, lib/screens/lawyers/
- lib/services/sos/, lib/services/lawyers/
- assets/mock/lawyers.json
- test/, README.md, submission package

## Technical knowledge you need
- Permissions: permission_handler for location and notifications; handle denied and permanently-denied (open settings).
- Location: geolocator getCurrentPosition with timeout, fallback getLastKnownPosition; map link https://maps.google.com/?q=lat,lng.
- Battery: battery_plus (optional).
- Sending alerts honestly: normal apps cannot silently send SMS. Use url_launcher with sms:+92... ?body=... (opens the SMS app prefilled) and a share option; also call tel: for emergency numbers. Tell the user "Tap Send". Backend /sos/log is for record/demo only. Do not claim automatic delivery.
- Countdown: 3-second cancelable countdown using Timer; vibrate (HapticFeedback).
- Check-In: store deadline in settings; use a local notification package (flutter_local_notifications) or an in-app timer plus notification; when expired and not confirmed, open SOS flow. Be honest: background timers are restricted on phones; for demo keep the app open or use notification scheduling.
- Trusted contacts: stored in secure storage or SQLite (encrypt phone numbers if time); max 5.
- Lawyers: start with local JSON mock, one profile is_demo true with a visible banner; reviews and booking are local state or simple backend later. Chat: scripted demo lawyer replies or a teammate replies via backend (stretch).
- Safety/legal: demo label everywhere; no real people profiles; consent before sharing case briefing.
- QA: write widget tests for SOS countdown and contact validation; manual device test sheet; screenshots; demo video; README.
- Pitfalls: Android 13+ notification permission, location permission flows, SMS URL encoding, Urdu text in SMS, battery optimisation killing timers.

## Phases

### Phase 1: SOS foundation: contacts, permissions, emergency numbers (Hours 0-6)
Goal: Contacts and permissions working.
Tasks:
- Trusted contacts CRUD (max 5) with validation of PK numbers
- Permission flows for location/notifications
- Emergency numbers screen (verify numbers)
- Local incident storage
- Mock lawyers JSON ready
Done when:
- Contacts persist after restart
- Denied permission handled

PROMPT FOR NEW CHAT (Phase 1):
```
Phase 1. I am Member 4. Build the SOS foundation for HAQOOQ in Flutter using only contracts + Provider: TrustedContact model/provider with persistence (sqflite or secure storage), add/edit/delete screen with validation of Pakistani mobile numbers, a PermissionService (location + notifications, handles denied and permanently denied by opening settings), an Emergency Numbers screen (leave numbers in a config file marked VERIFY), and an Incident local store. Full files with paths and widget tests for validation.
```

### Phase 2: SOS button, location message, Safety Check-In (Hours 6-12)
Goal: Press SOS -> prefilled alert to contacts.
Tasks:
- SOS screen with big button, 3 s cancel countdown, haptics
- Get location + battery, build message (ur/en/roman)
- Open SMS/share prefilled; call buttons
- Save incident
- Check-In timer and expiry flow
Done when:
- Works with GPS on and off
- Message contains map link when available
- Check-In 1-min test triggers flow

PROMPT FOR NEW CHAT (Phase 2):
```
Phase 2. Implement the SOS flow: SosScreen (big red button, 3-second cancelable countdown with haptic feedback), SosService implementing the contract: get location with timeout and last-known fallback, optional battery level, build a message in the user language containing a Google Maps link, then open the SMS app prefilled for each trusted contact via url_launcher (be honest in the UI that the user must tap Send), plus tel: call buttons, and save the incident locally. Add Safety Check-In: pick a deadline, local notification at deadline, if not confirmed start the SOS flow. Explain Android/iOS limits on background work and silent SMS. Full code and tests.
```

### Phase 3: Lawyer marketplace and demo chat (Hours 10-18)
Goal: Browse, view, chat, book (demo-labelled).
Tasks:
- Lawyer list with filters (city, specialization, language, fee)
- Profile with rating, reviews list, demo banner
- Chat screen with scripted demo-lawyer replies
- Booking form + confirmation (local)
- Attach Briefing Pack to chat
Done when:
- Demo label visible on list, profile, chat
- Booking confirmation shown
- Briefing attaches

PROMPT FOR NEW CHAT (Phase 3):
```
Phase 3. Build the lawyer marketplace using LawyerService from lib/contracts with a local JSON mock (assets/mock/lawyers.json, include 4 fictional profiles, ALL flagged is_demo true with visible banner "Demo Account - Not a Real Lawyer"): list with search + filters, profile with rating and review list (add review only after a completed demo booking), chat screen with scripted replies and a typing delay, booking form with date/time slot and confirmation, and a button to attach a Lawyer Briefing Pack (AskResponse/brief model) to the chat. Full files with paths.
```

### Phase 4: QA, README and submission (Hours 16-24)
Goal: Everything tested, documented, submitted.
Tasks:
- Manual test sheet from docs/07 on real phone incl. airplane mode
- Widget/unit tests for SOS and lawyers
- README with setup, features, screenshots, architecture, team roles, limitations
- Record demo video
- Submit Google Form before 6:30 PM
Done when:
- All test rows pass or documented
- README complete
- Form submitted

PROMPT FOR NEW CHAT (Phase 4):
```
Phase 4. Help me finish HAQOOQ: generate (1) a manual QA checklist table for Android covering every feature including airplane mode and permission denial, (2) Flutter widget tests for the SOS countdown cancel/send logic and contact validation, (3) a professional README.md (problem, solution, features, architecture diagram in text, tech stack, setup, env/secrets handling, limitations stated honestly, team roles, roadmap), (4) a 4-minute demo script and 60-second pitch, and (5) the final submission checklist for the Build with AI Multan Google Form.
```
