# Offline AI notes (Member 3)
- Cards: assets/legal_data/cards_en.json, cards_ru.json (DRAFT until verified on Pakistan Code and reviewed).
- DatabaseService loads cards into SQLite on first run and caches in memory.
- RAG: keyword scoring (title x3, keywords x3, body x1) + category boost; minimum score 3; top 3.
- OfflineAiService: answers from the top card (instant, offline). Gemma generation is an optional upgrade that must fall back to this.
- Add a card: edit the JSON, keep source_url + last_reviewed, run flutter test, get reviewer sign-off.
- Never commit model files (*.task, *.gguf, *.bin, *.tflite, *.litertlm).
