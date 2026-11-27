# Member 1 - Team Lead: Flutter UI/UX, Architecture, Integration
Branch: feature/ui | Repo: https://github.com/AqibTayyab/haqooq-mobile-app
First read docs/00_CRITICAL_RULES.md. In every new AI chat paste members/00_MASTER_CONTEXT_PROMPT.md first, then the phase prompt.

## Files you own
- lib/main.dart, lib/app.dart
- lib/config/ (theme, routes, constants)
- lib/l10n/
- lib/screens/home, settings, shared chat UI
- lib/widgets/
- pubspec.yaml and lib/contracts/ (change via PR)
- Repo setup, reviews, final merge, demo

## Technical knowledge you need
- Flutter: Material 3 theme, named routes (or go_router), Provider for state, responsive layout (MediaQuery, LayoutBuilder).
- Localization: simple map-based AppStrings with 3 locales (ur, en, roman_ur) and a LocaleProvider persisted in shared_preferences; Urdu needs RTL (Directionality) and a font that supports Urdu (e.g. Noto Nastaliq Urdu or Noto Sans Arabic via assets, bundle the font, do not rely on network).
- UI states: every list/screen handles loading, empty, error, offline using one reusable StateView widget.
- Chat UI: ListView reverse, message bubble, mode badge (green Online, orange Offline, grey Library), sources chips, disclaimer footer, action buttons (Call lawyer, SOS, Save note).
- Integration: screens depend only on contracts; use a ServiceLocator (simple class or get_it) with a useMocks flag.
- Git: you merge PRs, keep main green (flutter analyze, flutter test).
- Pitfalls: overflow on small screens, RTL icon mirroring, Urdu font missing, hardcoded strings, forgetting dispose().

## Phases

### Phase 1: Bootstrap and shared base (Hours 0-2)
Goal: Everyone can clone, run and push.
Tasks:
- flutter create . --org com.haqooq --project-name haqooq in the repo
- Copy starter_repo/ files, docs/, members/
- Fix .gitignore first, then pubspec.yaml; run flutter pub get and analyze
- Push main, invite members, enable branch protection
- Create the 4 feature branches
- Run a 10-minute sync so everyone clones and runs the app
Done when:
- All 4 run the app on their device
- analyze passes
- No secrets in repo

PROMPT FOR NEW CHAT (Phase 1):
```
Phase 1. I am Member 1 (team lead). Help me bootstrap the Flutter repo from the starter files: verify the .gitignore, pubspec.yaml (Provider, sqflite, geolocator, permission_handler, url_launcher, connectivity_plus, shared_preferences, intl, uuid), analysis_options.yaml and the lib/ skeleton. Give me exact commands, a checklist to confirm all 4 teammates can clone and run on Android, and a short GitHub branch-protection setup guide. Also give a script to grep the repo for leaked keys before every push.
```

### Phase 2: Theme, navigation and core screens with mocks (Hours 2-6)
Goal: Clickable app with mock data.
Tasks:
- Material 3 theme, high-contrast emergency red
- Bottom nav: Home, AI Chat, Rights, SOS, Lawyers
- Home, chat UI, rights list/detail, settings screens using mock services
- StateView widget for loading/empty/error/offline
- Mode badge widget
Done when:
- All tabs reachable
- Mock chat shows sources, disclaimer, actions
- No overflow on 360x640

PROMPT FOR NEW CHAT (Phase 2):
```
Phase 2. I own UI. Using only lib/contracts interfaces and mock services, build: AppTheme (Material 3), bottom navigation (Home, AI Chat, Rights, SOS, Lawyers), HomeScreen with 4 quick actions and a mode badge, ChatScreen (bubbles, mode badge, source chips, urgency banner, disclaimer, buttons Call lawyer / SOS / Save note), RightsScreen with search + category chips + ArticleScreen, SettingsScreen. Provide a reusable StateView(loading/empty/error/offline). Give full compilable files with paths and no external packages beyond Provider.
```

### Phase 3: Localization, RTL and polish (Hours 6-12)
Goal: Urdu/English/Roman Urdu everywhere.
Tasks:
- AppStrings for 3 locales
- LocaleProvider persisted
- RTL for Urdu, bundled Urdu font
- Language picker on first launch and in Settings
- Accessibility: font scale, contrast, large SOS
Done when:
- Switching language updates all main screens
- Urdu text renders correctly
- No hardcoded strings in screens

PROMPT FOR NEW CHAT (Phase 3):
```
Phase 3. Implement localization for HAQOOQ with three locales: ur (RTL), en, roman_ur. Use a simple AppStrings class + LocaleProvider persisted with shared_preferences, a bundled Urdu font declared in pubspec.yaml, and Directionality handling. Provide the string table for all screens built so far (Home, Chat, Rights, SOS, Lawyers, Settings, disclaimers, mode badges), a language picker screen and a checklist to find hardcoded strings. Full files with paths.
```

### Phase 4: Integration, QA and demo (Hours 12-24)
Goal: Real services plugged in, stable demo.
Tasks:
- Switch useMocks off module by module as PRs land
- Integration PR, resolve conflicts, keep main green
- Test on a real phone incl. airplane mode
- Code freeze at hour 20
- Record demo video, finalize pitch, help M4 submit
Done when:
- Full demo journey works
- Fallbacks verified
- Video recorded

PROMPT FOR NEW CHAT (Phase 4):
```
Phase 4. I am integrating HAQOOQ. Give me: a ServiceLocator with a useMocks flag per service (ai, rag, sos, lawyers), an AiRouter wiring example (online -> offline -> library), a pre-merge checklist, an end-to-end manual test script (online question, airplane mode question, SOS, briefing, demo lawyer), a crash-proofing review list (global error handler with runZonedGuarded and FlutterError.onError), and a 4-minute demo script. Full code for the locator and error handling.
```
