# MEMBER 3 - Legal Database, RAG and Offline AI
Repo: https://github.com/AqibTayyab/haqooq-mobile-app   Branch: feature/offline-ai

You own: assets/legal_data/, lib/services/rag/, lib/services/ai/offline/, docs/offline_ai_notes.md, test/rag_test.dart, test/offline_ai_test.dart.

## Setup
```bash
git clone https://github.com/AqibTayyab/haqooq-mobile-app.git
cd haqooq-mobile-app
git checkout -b feature/offline-ai
flutter pub get
```
Copy this package's folders (assets, lib, test, docs) into the repo root, keeping paths.
Make sure pubspec.yaml lists the asset folder `assets/legal_data/` and has sqflite, path, path_provider (the starter pubspec already does).

## Run tests
```bash
flutter analyze
flutter test test/rag_test.dart test/offline_ai_test.dart
```

## What is included (working, no Gemma needed)
- 12 English + 12 Roman Urdu draft cards (assets/legal_data). They are DRAFTS: verify each on https://pakistancode.gov.pk and a reviewer must sign off before you present them as fact.
- SQLite loader, tokenizer with Roman Urdu synonyms, intent classifier with emergency detection, keyword RAG with category boost.
- OfflineAiService: answers from the top retrieved card (fully offline, instant). This is the guaranteed fallback.
- Gemma 3 1B generation is Phase 3 (see PHASE_PROMPTS_FOR_CLAUDE.md). The flutter_gemma API changes between versions, so I did not hard-code it; generate it with the prompt using the current README.

## Rules
- Never commit model files (*.task, *.gguf, *.bin, *.tflite, *.litertlm).
- Every card keeps source_url and last_reviewed. Do not invent section numbers.
- Do not edit lib/contracts/ or other members' files. Need a contract change? Tell Member 1.
- Imports: files assume the starter structure lib/contracts/models.dart and lib/contracts/services.dart, package name haqooq.
