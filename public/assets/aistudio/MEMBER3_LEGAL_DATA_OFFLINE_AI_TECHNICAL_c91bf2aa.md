# Member 3 - Legal Database, RAG, Offline AI (Gemma)
Branch: feature/offline-ai | Repo: https://github.com/AqibTayyab/haqooq-mobile-app
First read docs/00_CRITICAL_RULES.md. In every new AI chat paste members/00_MASTER_CONTEXT_PROMPT.md first, then the phase prompt.

## Files you own
- assets/legal_data/*.json
- lib/services/rag/ (database, search, intent classifier)
- lib/services/ai/offline/
- Content verification log (docs/legal_sources.md)

## Technical knowledge you need
- Content: see docs/08. Official sources first (Pakistan Code). Plain-language summaries with source_url, reviewed date, reviewer. Do not copy long statutes.
- SQLite via sqflite: load JSON cards from assets on first run into legal_cards table; index title/keywords/body; use LIKE/FTS5 if available else in-memory scoring (fast enough for < 500 cards).
- Retrieval: tokenize, remove stopwords (en + roman urdu), synonym map (giraftar=arrest, thana=police station), score = weighted keyword overlap (title x3, keywords x3, body x1) + category boost; threshold to detect "not covered".
- Vector search is optional; skip unless time remains. Keyword RAG is enough for demo and more predictable.
- Offline model: Gemma 3 1B (about 0.5 GB, 4 GB RAM recommended) through flutter_gemma (LiteRT/MediaPipe based). Verify package README for current install steps, Android minSdk and iOS requirements; model is downloaded at runtime, not committed. Fallback Gemma 3 270M or library-only.
- Prompting small models: short prompt, max 3 cards, low temperature, ask for bullets, post-check output (non-empty, appended disclaimer, length cap). If model errors or is too slow (> 25 s), return top card text directly.
- Pitfalls: model files in git, loading model on app start (freeze), too many cards in prompt, Urdu script tokenization weak in tiny models (prefer Roman Urdu/English for offline answers; library cards in Urdu are shown as-is), unverified legal text.

## Phases

### Phase 1: Legal content and card schema (Hours 0-6)
Goal: 12+ verified cards (en + roman_ur), more if time.
Tasks:
- Create assets/legal_data/cards_en.json and cards_ru.json using docs/08 schema
- Record every source in docs/legal_sources.md
- Mark drafts; get a reviewer
- Add emergency numbers card (verify)
- Urdu cards for top 6 topics
Done when:
- Each card has source_url, last_reviewed, steps, keywords
- JSON validates
- No invented sections

PROMPT FOR NEW CHAT (Phase 1):
```
Phase 1. I am Member 3. Help me write verified-style legal-rights cards for Pakistani citizens in plain language using the JSON schema in docs/08_LEGAL_CONTENT_GUIDE.md. Topics: police stop, arrest/detention, FIR registration, FIR refused, bail basics, workplace harassment, domestic violence help, online fraud, cyber harassment, consumer complaint, unpaid salary, evidence checklist, finding legal aid. For each, give body, steps, keywords (English + Roman Urdu), urgency_hint and the OFFICIAL source I must open to verify (Pakistan Code etc.). Do NOT invent section numbers or punishments; where unsure write VERIFY on the card so I check it. Give a script to validate the JSON.
```

### Phase 2: SQLite store and search (Hours 4-10)
Goal: RagService.retrieve works and is fast.
Tasks:
- Create database service, load cards from assets
- Implement tokenizer, stopwords, synonym map
- Implement scoring + threshold + category boost
- Rule-based IntentClassifier
- Unit tests with 15 queries
Done when:
- retrieve("police ne roka") returns police cards
- Unknown query returns empty
- < 300 ms

PROMPT FOR NEW CHAT (Phase 2):
```
Phase 2. Implement RagService from lib/contracts in Flutter: DatabaseService (sqflite, load assets/legal_data/*.json into legal_cards on first run), Tokenizer with English + Roman Urdu stopwords and a synonym map, scoring (title x3, keywords x3, body x1, category boost), threshold for "not covered", a rule-based IntentClassifier (police_arrest, fir, women_safety, domestic, cybercrime, employment, property, consumer, emergency) and emergency-word detection. Provide unit tests with 15 queries (English + Roman Urdu) and expected top cards. Full code with paths.
```

### Phase 3: Offline Gemma with RAG (Hours 8-16)
Goal: Offline AI answers from cards.
Tasks:
- Add flutter_gemma per its README; model download manager with progress
- RAM check and fallback logic
- OfflineAiService implementing AiService
- Prompt from docs/05 section 3, output post-processing
- 25 s timeout -> return top card text
Done when:
- Airplane mode answer works on a real phone
- Disclaimer always appended
- No model file in git

PROMPT FOR NEW CHAT (Phase 3):
```
Phase 3. Integrate an on-device Gemma 3 1B model with flutter_gemma into HAQOOQ: ModelManager (download with progress, resume, delete, store in app dir, never in git), device RAM check with fallback to library-only, OfflineAiService implementing AiService from lib/contracts: retrieve top 3 cards via RagService, build the short prompt from docs/05 section 3, generate with temperature 0.2 and token cap, post-process (non-empty, bullet format, append disclaimer), 25 s timeout then fall back to top card text. Tell me exactly which parts depend on the flutter_gemma version so I can verify against its README. Full code.
```

### Phase 4: Shared retrieval, performance, handoff (Hours 16-22)
Goal: Same cards power online + offline; tested on phone.
Tasks:
- Expose retrieve() to M2 online path
- Measure memory/time on a 4 GB phone
- Lite mode toggle
- Content freeze and reviewer sign-off
- Write docs on how to add cards
Done when:
- Numbers recorded (load time, answer time)
- Content review logged

PROMPT FOR NEW CHAT (Phase 4):
```
Phase 4. Help me finish HAQOOQ offline AI: write a benchmark routine (model load time, first-token time, total answer time, peak memory estimate) to run on a real 4 GB Android phone, a Lite Mode setting (disable model, library only), a lazy-load/unload strategy when the chat screen opens/closes, and a short doc on how teammates add new legal cards safely. Also give a checklist for content freeze (sources present, drafts flagged, disclaimer on every answer).
```
