# Blueprint — HAQOOQ

## 1. Navigation
Bottom nav: Home | AI Chat | Rights | SOS | Lawyers. Settings via Home app bar.

## 2. Screens
| Screen | Owner | Notes |
|---|---|---|
| Splash + language | M1 | persist choice |
| Onboarding + permissions | M1/M4 | location, notifications; skip allowed |
| Home | M1 | quick actions, mode badge, recent notes |
| AI Chat | M1 UI / M2+M3 logic | bubbles, badge, sources, disclaimer, action buttons |
| Rights Library + Article | M1 UI / M3 data | search, categories |
| SOS + Contacts + Check-In | M4 | big button, countdown |
| Emergency numbers | M4 | |
| Lawyer list/profile/chat/booking | M4 | demo labelled |
| Briefing Pack view | M2 data / M4 share | |
| Settings | M1 | language, delete data, model download |

## 3. Core flows
A) Question: Chat -> AiRouter -> RagService.retrieve -> Online or Offline answer -> sources + actions.
B) Emergency: SOS -> 3 s countdown (cancel) -> location+battery -> prefilled message to contacts -> option call lawyer -> local incident saved.
C) Lawyer handover: Chat -> Create Briefing -> Lawyer list -> chat/book -> briefing attached.
D) Offline: airplane mode -> badge turns orange -> library + Gemma still work.

## 4. Folder blueprint (starter_repo has this skeleton)
```
lib/
  main.dart
  app.dart
  contracts/        # shared interfaces + models (change only with team approval)
  config/           # theme, routes, constants, env
  l10n/             # strings (ur, en, roman)
  screens/          # home, chat, rights, sos, lawyers, settings
  widgets/
  services/         # ai/, rag/, sos/, lawyers/, connectivity/
  providers/
assets/legal_data/  # cards_en.json, cards_ur.json, cards_ru.json
assets/images/
backend/            # functions, README, .env.example
docs/  members/
test/
```

## 5. Build order and sync checkpoints (24h)
| Hour | M1 | M2 | M3 | M4 |
|---|---|---|---|---|
| 0-2 | bootstrap, push | choose backend, /health | card schema, 10 cards | contracts check, mock lawyers |
| 2-6 | nav + screens (mock data) | /ai/ask live | SQLite + search | SOS screen + contacts |
| 6-12 | l10n, polish | brief endpoint, safety | Gemma + RAG | check-in, lawyer screens |
| 12-18 | integrate real services | Flutter AiService client | RAG feeds online too | demo chat, booking |
| 18-22 | bug fixes | safety tests | perf test on phone | QA, README |
| 22-24 | demo video | | | submission |
Checkpoints: hour 2 (repo runs for all), hour 8 (each module merged once), hour 16 (integration merge), hour 20 (code freeze), hour 22 (video).

## 6. Mock-first rule
Every service has a Mock implementation (in contracts/mocks) so UI works before backend/AI is ready. Swap via one switch in config.
