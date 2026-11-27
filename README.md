<h1 align="center">HAQOOQ</h1>
<h3 align="center">Multilingual Citizen Legal Guide and Safety App</h3>

<p align="center">
  Build with AI Multan 2026 — SKYRA Pakistan
</p>

<p align="center">
  <a href="https://github.com/AqibTayyab/haqooq-mobile-app">Repository</a> ·
  <a href="#project-overview">Overview</a> ·
  <a href="#features">Features</a> ·
  <a href="#demo-and-apk">Demo and APK</a> ·
  <a href="#installation">Installation</a> ·
  <a href="#usage">Usage</a> ·
  <a href="#architecture">Architecture</a> ·
  <a href="#team">Team</a>
</p>

## Project Overview

HAQOOQ is a multilingual citizen legal guide and personal-safety application designed for people in Pakistan. The project helps users understand their legal rights, access legal information in simple language, prepare for legal consultation, respond during emergencies, and connect with legal support.

The application is built for Android using Flutter. It supports English, Urdu, and Roman Urdu, including right-to-left layout support for Urdu. It also includes a Material 3 Expressive dark and light interface, offline legal-rights guidance, an online Gemini-powered AI assistant, an Emergency SOS system, a Safety Check-In feature, and a demo lawyer directory.

HAQOOQ was developed for the Build with AI Multan 2026 hackathon under the SKYRA Pakistan initiative.

## The Problem

Many people in Pakistan face difficulty understanding their legal rights during stressful situations such as police stops, arrest, harassment, workplace issues, family disputes, cybercrime, or consumer complaints.

Legal information is often written in difficult language, available only in English, or difficult to access during an emergency. People may not know what documents to collect, what questions to ask a lawyer, or which emergency contacts to contact.

HAQOOQ addresses this problem by providing:

- Simple legal-rights guidance in English, Urdu, and Roman Urdu
- Offline access to important legal-rights information
- An AI assistant that can explain legal topics in plain language
- Emergency SOS and Safety Check-In tools
- A lawyer directory and consultation workflow
- Clear disclaimers so users understand that the application provides legal information, not legal advice

## What HAQOOQ Provides

HAQOOQ is designed as a practical legal-safety companion. It does not replace a lawyer, police officer, court, or emergency service. Instead, it helps users become better prepared before speaking to a lawyer or authority.

The application provides legal-rights information, emergency safety tools, and a demonstration lawyer-access workflow. It helps users organize information, understand possible next steps, and communicate more clearly during a legal or safety situation.

## Important Legal Disclaimer

HAQOOQ provides general legal information only.

It does not provide legal advice.

It does not replace a licensed lawyer, legal-aid organization, court, police department, or emergency service.

Every AI response, lawyer profile, and dashboard in the application displays a mandatory disclaimer. Users should always verify important legal information with a qualified legal professional before taking action.

For urgent legal, safety, or criminal matters, users should contact a licensed lawyer, a legal-aid organization, or the appropriate emergency service.

## Features

| Feature | Detail |
|---|---|
| Multilingual interface | English, Urdu with RTL layout support, and Roman Urdu |
| Material 3 interface | Android Material 3 Expressive dark and light themes |
| Adaptive navigation | Floating bottom dock for compact screens and navigation rail for tablets and foldables |
| Offline legal-rights guidance | Plain-language legal-rights information available without internet |
| Online AI assistant | Gemini-powered legal-information assistant through a secure backend |
| Offline AI support | On-device AI assistance for basic legal-rights questions |
| Emergency SOS | One-tap SOS alert with location, battery status, trusted contacts, and pre-filled SMS/share options |
| Safety Check-In | Scheduled safety confirmation with local notification on expiry |
| Lawyer directory | Search, filters, lawyer profiles, reviews, and consultation booking |
| Demo lawyer chat | Scripted multilingual demo chat and consultation booking flow |
| Lawyer Briefing Pack | User-consented case briefing material for lawyer consultation |
| Dark and light mode | True-black dark mode and light mode toggle |
| Local data storage | Trusted contacts, SOS history, and safety check-in data stored locally |
| Accessibility and clarity | Simple language, clear labels, and multilingual support |

## Member Contributions

The work includes:

- Complete string tables for English, Urdu, and Roman Urdu
- Urdu support with automatic right-to-left layout direction
- Locale persistence through `LocaleController`
- True-black dark mode using `#000000`
- Light mode toggle in the top bar, onboarding, and settings
- Locally bundled fonts, including Plus Jakarta Sans, Inter, and Noto Naskh Arabic
- Adaptive navigation using `ExpressiveFloatingBottomDock` on compact screens
- Navigation rail support for tablets and foldables
- Node.js and Express backend
- Secure Gemini API proxy
- Flutter online AI service
- AI router for online and offline modes
- Health checker
- Automated tests
- Evaluation set
- Project documentation

The backend allows the Flutter application to communicate with the Gemini API without exposing the Gemini API key inside the mobile application.

- Offline legal-rights content
- Legal database and retrieval workflow
- On-device AI support
- Offline assistance when internet is unavailable
- Grounded legal-information responses
- Persistent trusted contacts
- Add, edit, primary-contact selection, and delete functionality
- Maximum of five trusted contacts
- Pakistani mobile-number validation
- Runtime location and notification permission checks
- App settings shortcut for denied permissions
- Emergency helplines: 15, 1122, 1099, and 1991
- Local SOS incident history
- Three-second cancelable SOS countdown
- Tactile vibration during countdown
- Real GPS location with last-known-location fallback
- Battery percentage in SOS alert
- Multilingual SOS alert messages
- Google Maps location link
- Honest pre-filled SMS intent
- Share Sheet and copy option
- Dialer intent for emergency helplines
- Persistent Safety Check-In countdown
- Local notification when the check-in expires
- Lawyer search, city, specialization, and spoken-language filters
- Demo lawyer profiles
- Scripted multilingual demo chat
- Demo consultation booking
- Explicit user consent before attaching a Lawyer Briefing Pack
- Post-booking demo reviews
- Manual QA checklist

## Legal and Safety Principles

### General legal information only

HAQOOQ provides general legal information, not legal advice. Every AI response, lawyer profile, and dashboard displays the mandatory disclaimer.

### Honest SMS and calling behavior

HAQOOQ does not send silent background SMS messages or place calls without user action.

When a user taps **Send SMS Alert** or **Call**, the application opens the device SMS or dialer application with a pre-filled recipient and message. The user must manually confirm and send the message or place the call.

This design protects user consent and follows responsible mobile-application practices.

### Background Check-In limitation

The Safety Check-In deadline is stored locally and remains available after an application restart.

The live countdown runs while the application process is active. Aggressive battery-saving features may pause timers if the operating system stops the application process.

### Demo lawyer profiles

All lawyer profiles, ratings, bookings, and replies used in the demonstration are fictional demo accounts.

They are clearly labeled:

> Demo Account — Not a Real Lawyer

### Emergency numbers

Emergency helplines, including 15, 1122, 1099, and 1991, should be verified locally before relying on them in a critical emergency.

## Demo and APK

The compiled Android APK and project demonstration video are available through Google Drive.

The APK allows reviewers, judges, and team members to install and test the HAQOOQ application on an Android device without building the project from source.

The demo video shows the main HAQOOQ features, including the multilingual interface, legal-rights library, AI assistant, Emergency SOS, Safety Check-In, and lawyer directory.

### Download Links

| Item | Link |
|---|---|
| Download HAQOOQ APK | [Add Google Drive APK Link Here](PASTE_YOUR_GOOGLE_DRIVE_APK_LINK_HERE) |
| Watch HAQOOQ Demo Video | [Add Google Drive Demo Video Link Here](PASTE_YOUR_GOOGLE_DRIVE_VIDEO_LINK_HERE) |
| Open Demo and APK Folder | [Add Google Drive Folder Link Here](PASTE_YOUR_GOOGLE_DRIVE_FOLDER_LINK_HERE) |

### APK Safety Notice

The APK is provided through Google Drive because it is a hackathon demonstration build and is not published on the Google Play Store.

Because the application is downloaded from an external source rather than an official app store, Android may display a security warning such as:

> This app is from an unverified developer.

or:

> For your safety, Play Protect has blocked the installation.

This warning appears because the APK is not distributed through the Google Play Store. It does not automatically mean that the file is unsafe.

The APK was built by the HAQOOQ project team for demonstration and evaluation purposes. Please download the APK only from the official Google Drive link provided in this README.

To install the APK:

1. Download the APK file from the provided Google Drive link.
2. Open the downloaded APK on an Android device.
3. If Android shows a security warning, review the warning and choose **Install Anyway** only if you trust the source.
4. Allow installation from the browser or file manager if prompted.
5. Complete the installation.
6. Open HAQOOQ.
7. For online AI features, ensure the Gemini backend is running and the application is connected to the backend URL.

### Demo Video

The demonstration video shows how HAQOOQ works in real use.

It includes:

- Opening the application
- Changing language between English, Urdu, and Roman Urdu
- Switching between dark and light mode
- Opening the legal-rights library
- Asking a legal-rights question
- Using the AI assistant
- Testing the Emergency SOS flow
- Testing the Safety Check-In feature
- Searching the lawyer directory
- Opening a demo lawyer profile
- Starting a demo lawyer chat
- Booking a demo consultation

## Installation

### Requirements

To run the project from source, install the following:

- Flutter SDK
- Android Studio or VS Code with Flutter extensions
- Android SDK
- Android device or Android emulator
- Node.js, required only for the Gemini backend
- A Gemini API key, required only for online AI features

### Clone the repository

```bash
git clone [https://github.com/AqibTayyab/haqooq-mobile-app.git](https://github.com/AqibTayyab/haqooq-mobile-app.git)
cd haqooq-mobile-app
```

### Run the mobile application

The runnable Flutter application is located in the `app/` directory.

```bash
cd app
flutter pub get
flutter analyze
flutter test
flutter run
```

If multiple devices are connected, select the Android device or emulator when prompted.

### Run on an Android phone

1. Enable Developer Options on your Android phone.
2. Enable USB Debugging.
3. Connect the phone to your computer using USB.
4. Accept the USB debugging prompt on the phone.
5. Run:

```bash
flutter devices
```

6. Confirm that your Android device appears in the device list.
7. Run:

```bash
flutter run
```

## Run the Gemini Backend

The Gemini backend is located in:

```bash
member2/backend/
```

Open a second terminal and run:

```bash
cd member2/backend
npm install
cp .env.example .env
```

Open `.env` and add your Gemini API key:

```env
GEMINI_API_KEY=your-gemini-api-key
GEMINI_MODEL=gemini-2.5-flash
APP_PUBLIC_KEY=haqooq-demo-app
```

Start the backend:

```bash
set -a
source .env
set +a
npm start
```

The backend will start on:

```text
http://localhost:8080
```

## Connect the App to the Backend

Find your computer’s local IP address:

```bash
hostname -I
```

Example output:

```text
192.168.1.5
```

Run the Flutter application with the backend URL:

```bash
cd app
flutter run \
  --dart-define=BACKEND_BASE_URL=http://192.168.1.5:8080 \
  --dart-define=APP_PUBLIC_KEY=haqooq-demo-app
```

Replace `192.168.1.5` with your actual local IP address.

Your Android phone and computer must be connected to the same Wi-Fi network.

## Usage

### Ask a legal question

1. Open the AI Chat screen.
2. Select your preferred language.
3. Ask a question, for example:

```text
Police ne mujhe roka hai, kya karun?
```

4. The application will provide legal information, relevant sources, next steps, and a disclaimer.

### Use Emergency SOS

1. Open the SOS screen.
2. Add trusted contacts.
3. Press the SOS button.
4. Confirm or cancel within the countdown.
5. The application prepares an emergency message with your location and battery status.
6. Choose a contact and send the alert through your device SMS application.

### Use Safety Check-In

1. Open Safety Check-In.
2. Select a check-in time.
3. Confirm that you are safe before the timer ends.
4. If you do not confirm, the application alerts your selected trusted contacts with your last known location.

### Find legal support

1. Open the Lawyer Directory.
2. Filter lawyers by city, specialization, language, or fee.
3. View a lawyer profile.
4. Start a demo chat or book a consultation.

## Architecture

### Application structure

```text
haqooq-mobile-app/
├── app/                     # Runnable Flutter application
├── docs/                    # Project documentation
├── members/                 # Team member documentation
├── member2/                 # Gemini backend and Flutter AI client
├── member3/                 # Offline legal database, RAG, and on-device AI
├── starter_repo/            # Shared base application and contracts
├── tools/                   # App assembly and utility scripts
└── public/                  # Static assets
```

### Technology stack

| Layer | Technology | Purpose |
|---|---|---|
| Mobile application | Flutter | Cross-platform Android and iOS user interface |
| UI framework | Material 3 | Modern adaptive interface |
| Localization | Custom localization system | English, Urdu, and Roman Urdu |
| Online AI | Gemini API | Legal-information assistant |
| Backend | Node.js and Express | Secure Gemini API proxy |
| Offline AI | Gemma on-device model | Basic offline legal-rights assistance |
| Lawyer directory | JSON-based demo service | Lawyer search, profiles, chat, and booking |
| Emergency features | Device location, SMS, dialer, and notifications | SOS and Safety Check-In |

### AI architecture

```text
User question
      |
      v
AI Router
      |
      +--> Online mode: Gemini backend
      |
      +--> Offline mode: On-device Gemma model
      |
      +--> Library-only mode: Offline legal-rights database
```

The AI answers are grounded in verified legal-rights content. If the available information is insufficient, the application recommends contacting a licensed lawyer instead of generating unsupported legal claims.

## Security and Privacy

| Concern | Approach |
|---|---|
| API key exposure | The Gemini API key is stored only in backend environment variables, never in the mobile application |
| Sensitive case data | Encrypted local storage and user-controlled deletion |
| Location sharing | Location is shared only with contacts selected by the user |
| Police notifications | Police alerts require explicit user consent |
| Demo lawyer accounts | Clearly labeled as fictional and non-licensed |
| Legal misinformation | AI responses are grounded in reviewed legal-rights content and include disclaimers |

## Testing

Run Flutter analysis:

```bash
cd app
flutter analyze
```

Run Flutter tests:

```bash
flutter test
```

Run backend tests:

```bash
cd member2/backend
npm install
npm test
```

## Manual QA Checklist

| ID | Feature / Scenario | Expected Result | Status |
|---|---|---|---|
| QA-01 | Trusted Contacts CRUD and validation | Invalid numbers rejected; maximum of five contacts enforced; edit, primary star, and delete persist across restart | Pass |
| QA-02 | Permissions and App Settings flow | Runtime location and notification prompts work; denied state offers App Settings shortcut | Pass |
| QA-03 | Three-second SOS countdown and cancel | Countdown vibrates from 3 to 2 to 1; tapping Cancel aborts without logging an incident | Pass |
| QA-04 | SOS GPS, battery, and honest SMS/share | Attaches Google Maps location when available or honest GPS-off text; opens pre-filled SMS, Share, or Copy cleanly | Pass |
| QA-05 | Safety Check-In timer | 15, 30, and 60 minute presets count down, persist deadline, cancel through “I’m Safe,” and trigger alert and notification at 00:00 | Pass |
| QA-06 | Emergency helplines dialer | 15, 1122, 1099, and 1991 show verify-locally notice and open dialer without auto-calling | Pass |
| QA-07 | Lawyer search, filters, and demo banner | Search, city, topic, and language chips filter lawyers; demo banner visible everywhere | Pass |
| QA-08 | Demo booking, consent, and reviews | Explicit consent toggle for Lawyer Briefing Pack; demo review dialog unlocked only after booking | Pass |
| QA-09 | Multilingual, RTL, dark, and light mode | All Member 4 labels, dialogs, and SOS templates render cleanly in English, Urdu RTL, and Roman Urdu in dark and light themes | Pass |

## Roadmap

| Version | Feature | Status |
|---|---|---|
| v1.0 | Multilingual legal-rights library, AI assistant, SOS, Safety Check-In, lawyer directory | Completed |
| v1.1 | Verified lawyer onboarding and real consultation payments | Planned |
| v1.1 | Official police and legal-aid integrations | Planned |
| v1.2 | Improved offline Urdu AI model | Planned |
| v1.2 | Document analysis for FIRs, notices, and contracts | Planned |
| Future | iOS release and app-store publication | Planned |

## Team

### Rayan Siddiqui

[GitHub](https://github.com/rayan208)  
[LinkedIn](https://www.linkedin.com/in/rayan-siddiqui-46825a377/)

### Muhammad Aqib Tayyab

[GitHub](https://github.com/AqibTayyab)  
[LinkedIn](https://www.linkedin.com/in/muhammad-aqib-tayyab-ethical-hacker/)

## License

This project is created for the Build with AI Multan 2026 hackathon.
