# Architecture — HAQOOQ

## 1. High level
```
[Flutter App]
  UI (screens, widgets, l10n)
  Providers/state
  Services (via lib/contracts interfaces)
    AiService  ── online ─> [Backend Proxy] ──> Gemini API
               └─ offline -> OfflineAiService (Gemma 3 1B / LiteRT) + RagService
    RagService  ── SQLite legal cards (keyword + optional vector)
    SosService  ── Geolocator, contacts, SMS/share intent, backend SOS log
    LawyerService ── backend (Firestore/Supabase) or local mock JSON
    ConnectivityService ── decides mode
  Local storage: SQLite (cards, notes), secure storage (contacts, notes encryption key)
```

## 2. Mode selection (AiRouter)
1. If online and backend healthy -> ONLINE (Gemini, cards retrieved locally and sent as context).
2. Else if device RAM ok and model downloaded -> OFFLINE (Gemma + RAG).
3. Else -> LIBRARY_ONLY (show top matching cards + emergency contacts).
Always show the active mode badge. Timeouts: online 12 s then fall back automatically.

## 3. Key decisions
| Decision | Choice | Reason |
|---|---|---|
| Framework | Flutter | One codebase, fast |
| Online AI | Gemini (Flash default; Pro for long/complex) via backend | Hackathon credits, quality, key safety |
| Offline AI | Gemma 3 1B (about 0.5 GB, 4 GB RAM recommended), fallback Gemma 3 270M, via flutter_gemma / LiteRT-LM | Runs on-device |
| Grounding | RAG over verified legal cards | Reduces hallucination |
| Local DB | SQLite (sqflite) | Simple, offline |
| Backend | Firebase Cloud Functions OR Supabase Edge Functions (pick one on hour 0) | Hide API key |
| SOS delivery | Share/SMS intent + backend log | Works without custom SMS gateway |
| State mgmt | Provider | Simple for 4 people |

## 4. Why a backend proxy
Anything inside an APK/IPA can be extracted. The Gemini key must never ship in the app. The proxy also lets us rate limit, validate input and enforce the safety prompt server-side.

## 5. Backend endpoints
POST /ai/ask, POST /ai/brief, POST /sos/log, GET /lawyers, GET /health. Details in 04_DATA_AND_API.md.

## 6. Offline model notes
- Download model after install (not bundled in repo; never commit model files).
- Check RAM before enabling; warn under 4 GB, offer 270M or library-only.
- Keep prompts short, max 3 retrieved cards, answer under 120 words.
- Load model only when AI chat opens; unload on leave.

## 7. SOS notes
- Android/iOS cannot silently send SMS from a normal app. Use: (a) share sheet / sms: URL launch prefilled with message + map link, (b) backend log + push to contacts if they also use app (future). Be honest in UI: "Tap Send to alert your contact".
- Location: geolocator with permission flow; use last known position if fresh GPS slow.
- Check-In: schedule local notification; on expiry prompt then fire SOS flow.

## 8. Security
Encrypt notes (AES via secure-storage key), minimal logging, no key in client, HTTPS only, delete-all-data button.

## 9. Failure matrix
| Failure | Behaviour |
|---|---|
| No internet | Offline AI or library |
| Backend error/timeout | Offline AI or library |
| Model missing | Library + prompt to download |
| GPS denied | Send message without location, explain |
| Contacts empty | Prompt to add contact; show emergency numbers |
