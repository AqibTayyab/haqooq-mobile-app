# Member 2 - Gemini AI Lawyer, Backend, AI Safety
Branch: feature/gemini | Repo: https://github.com/AqibTayyab/haqooq-mobile-app
First read docs/00_CRITICAL_RULES.md. In every new AI chat paste members/00_MASTER_CONTEXT_PROMPT.md first, then the phase prompt.

## Files you own
- backend/ (functions, README, .env.example)
- lib/services/ai/ai_router.dart, online_ai_service.dart
- Prompt files and AI evaluation set
- docs/04 and 05 updates for API changes

## Technical knowledge you need
- Gemini API via Google AI Studio key (organizers shared free credits: g.dev/credits/gdg-live-pk-build-with-ai-multan). Use a Flash-class model by default; set the model name in an env var (GEMINI_MODEL) so you can change it without code edits. Verify the exact current model name in AI Studio.
- Backend choice (decide hour 0): Firebase Cloud Functions (Node.js, secrets via functions config / Secret Manager) or Supabase Edge Functions (Deno, secrets via supabase secrets set). Cloud credits from the hackathon may help. Either: key in env only, never in repo.
- Structured output: ask Gemini for JSON (response MIME type JSON + schema) and still validate server-side; retry once on invalid JSON.
- Safety: system prompt in docs/05; server-side input length limit (e.g. 2000 chars), rate limit per IP/device, prompt-injection resistance (cards are data, not instructions), refuse evidence-hiding requests.
- Grounding: client sends top cards; model must cite only those.
- Timeouts: 12 s client timeout then fallback.
- Flutter side: http package, AiService implementation, error mapping to AskResponse fallback, ConnectivityService for mode.
- Pitfalls: exposing key, CORS/auth config, quota errors (429) -> show offline fallback, non-JSON replies, huge prompts, logging private user text.

## Phases

### Phase 1: Backend scaffold and key security (Hours 0-3)
Goal: Deployed /health and secure secret handling.
Tasks:
- Choose Firebase or Supabase
- Scaffold backend/ with README and .env.example
- Store key as secret, deploy /health
- Add rate limit + input validation skeleton
- Document base URL in backend/README.md (no secrets)
Done when:
- /health returns ok from phone browser
- No key in repo (grep)

PROMPT FOR NEW CHAT (Phase 1):
```
Phase 1. I am Member 2 (Gemini + backend). Set up a minimal backend for HAQOOQ using [Firebase Cloud Functions Node 20 | Supabase Edge Functions - pick one and tell me why for a 24h hackathon]. Provide: folder layout under backend/, a GET /health endpoint, secret management steps so GEMINI_API_KEY never enters the repo, .env.example, CORS and simple per-IP rate limiting, deploy commands, and a curl test. Warn me about every place a key could leak.
```

### Phase 2: POST /ai/ask with Gemini (Hours 3-8)
Goal: Grounded JSON answers.
Tasks:
- Implement system prompt from docs/05
- Accept cards[], language, context, history
- Call Gemini with JSON schema
- Validate output, retry once, map errors
- Add 8 curl tests
Done when:
- Valid JSON for 8 test questions
- Unknown topic -> not sure + lawyer
- Injection test refused

PROMPT FOR NEW CHAT (Phase 2):
```
Phase 2. Implement POST /ai/ask on my backend. Input: question, language (ur|en|roman_ur), city, context flags, cards[] (id,title,body,source_name,source_url), history[]. Call the Gemini API with this system prompt (paste docs/05_AI_SYSTEM.md section 1) using JSON-schema structured output; validate the schema, retry once on invalid JSON, return the AskResponse shape from docs/04_DATA_AND_API.md. Include input length limits, unsafe_request refusals, error codes, and 8 curl tests including a prompt-injection and an evidence-hiding request. Give full code.
```

### Phase 3: Briefing endpoint, safety hardening, evaluation (Hours 8-14)
Goal: Lawyer Briefing Pack + tested safety.
Tasks:
- POST /ai/brief with briefing prompt
- POST /sos/log (store only needed fields)
- Build 20-question evaluation file and run it
- Tune prompt from failures
- Optional: document upload summary (online only, small files)
Done when:
- Briefing JSON valid for 3 sample chats
- Eval: >= 90% grounded, 100% unsafe refused

PROMPT FOR NEW CHAT (Phase 3):
```
Phase 3. Add POST /ai/brief (conversation -> summary, timeline, people_involved, evidence_checklist, questions_for_lawyer, urgency; never invent facts) and POST /sos/log. Then write a 20-case evaluation set (arrest, FIR refused, harassment, fraud, unknown topic, hide-evidence request, prompt injection, Urdu input, Roman Urdu input) as a JSON file plus a Node script that runs it against the endpoint and prints pass/fail for: valid JSON, grounded in provided cards, disclaimer present, correct urgency, refusal when unsafe.
```

### Phase 4: Flutter AI client and router (Hours 12-20)
Goal: App uses real Gemini with automatic fallback.
Tasks:
- Implement OnlineAiService (http, timeout 12 s)
- Implement AiRouter: online -> offline -> library
- Connectivity detection
- Map errors to friendly fallbacks
- Unit tests with mocked http
Done when:
- Airplane mode switches mode in < 3 s
- Backend error never shows raw error
- Tests pass

PROMPT FOR NEW CHAT (Phase 4):
```
Phase 4. Write the Flutter side for HAQOOQ: OnlineAiService implementing AiService from lib/contracts (http package, 12 s timeout, JSON parsing to AskResponse, no API key anywhere), AiRouter that tries online, then OfflineAiService, then returns a LIBRARY_ONLY response, using connectivity_plus plus a /health ping. Include a mode enum exposed through a ChangeNotifier for the UI badge, graceful error mapping, and unit tests using a mock http client. Full files with paths.
```
