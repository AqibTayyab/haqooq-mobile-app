# Member 2 phase prompts (paste members/00_MASTER_CONTEXT_PROMPT.md first)
Phases 1-4 are already built in this package. Use these prompts to review, deploy and improve.

## Phase 1 - Backend scaffold and deploy
Review backend/ and help me deploy it to Render or Cloud Run with the Gemini key as an environment secret. Give exact click-by-click steps and a curl test.

## Phase 2 - /ai/ask quality
Review backend/src/app.js and prompts.js. Improve the system prompt for Urdu and Roman Urdu answers without breaking the JSON contract or weakening safety rules.

## Phase 3 - Brief and evaluation
Run npm run eval against my deployed backend, explain every failing case and suggest minimal prompt or filter fixes.

## Phase 4 - Flutter client wiring
Review lib/services/ai/*.dart against lib/contracts and show how to wire AiRouter into Provider in main.dart so the chat screen shows the mode badge.
