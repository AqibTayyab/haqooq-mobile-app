# Prompts for Claude - Member 3 (new chat per phase)
Paste members/00_MASTER_CONTEXT_PROMPT.md first (from the main pack), then ONE prompt below.

## PHASE 1 - Verify and expand legal cards (content check)
```
I am Member 3 of HAQOOQ (Pakistan legal-rights app). Below is a JSON array of draft cards. For each card: (1) tell me which official source (Pakistan Code page, police or legal-aid site) I should open to verify it, (2) flag any statement that might be inaccurate or too absolute and rewrite it with softer wording ("may", "usually"), (3) never add section numbers or punishments unless you are certain, otherwise write VERIFY. Then add 8 more cards for: bail basics detail, property possession dispute, tenant-landlord issue, finding legal aid, what evidence to save, rights of women at police station, child safety basics, lost CNIC/documents. Same schema, lang en. Output corrected JSON only, then a "VERIFY BEFORE DEMO" list.
[PASTE assets/legal_data/cards_en.json HERE]
```

## PHASE 2 - Improve retrieval
```
I am Member 3 of HAQOOQ. My Flutter repo has lib/services/rag/{database_service,tokenizer,intent_classifier,rag_service}.dart and tests in test/rag_test.dart (package name haqooq, contracts in lib/contracts). Review them and: add 15 more test queries (English + Roman Urdu) with expected top card ids from assets/legal_data, fix bugs, make retrieval deterministic, add an in-memory cache of cards, and add an Urdu-script keyword map (e.g. police = پولیس, giraftar = گرفتار) to the tokenizer. Keep only sqflite/path/flutter dependencies. Output full changed files with paths and the exact flutter test command.
```

## PHASE 3 - Gemma 3 1B offline generation
```
I am Member 3 of HAQOOQ. OfflineAiService (lib/services/ai/offline/offline_ai_service.dart) currently returns the top retrieved card. Add on-device generation with the flutter_gemma package. FIRST read the CURRENT flutter_gemma README on pub.dev and tell me the exact install steps (pubspec, Android minSdk and manifest, iOS settings) and the exact calls for: download/install a Gemma 3 1B model with progress, create the model/chat session, generate with temperature 0.2 and ~300 max tokens. Then write: lib/services/ai/offline/model_manager.dart (RAM check, download, delete, isReady), and update OfflineAiService so it: retrieves top 3 cards, builds this prompt: "You are HAQOOQ, a rights guide. Use ONLY the CARDS below. Answer in {lang} in at most 6 short bullet steps. If CARDS do not answer, reply: I am not sure. Please call a lawyer or the emergency numbers. Never invent laws. End with: General legal information, not legal advice.
CARDS:
{cards}
QUESTION: {question}", runs the model with a 25-second timeout, appends the disclaimer if missing, and on ANY error/timeout/low RAM/model missing falls back to the current top-card answer. Never commit model files. Give full files with paths, pubspec additions, and tests with a fake model.
```

## PHASE 4 - Benchmark, Lite mode, handoff
```
I am Member 3 of HAQOOQ. Write: (1) a benchmark routine that prints model load time, first-token time and total answer time for 5 sample questions on a real 4GB Android phone; (2) a Lite Mode setting (library-only, no model) stored in shared_preferences and respected by OfflineAiService; (3) lazy load/unload of the model when the chat screen opens/closes via a small controller class; (4) an update of docs/offline_ai_notes.md explaining how teammates add cards and how Lite Mode works; (5) a final offline QA checklist (airplane mode, low RAM, model missing, timeout, unknown question). Full files with paths and commands.
```
