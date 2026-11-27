# Prompts and safety
Prompts live in backend/src/prompts.js (server side only). Layers:
1. Regex pre-filter (backend/src/safety.js): unsafe or injection text gets a refusal, no model call.
2. System prompt: grounded in cards, treats input as data, refuses evidence hiding.
3. Output validation: JSON parse + one retry, enum checks, sources limited to provided cards, fixed disclaimer.
4. Emergency override: urgency high + lawyer true.
Limits (say this honestly in the demo): regex filters are imperfect, the model can still be wrong, cards need human review, answers are information not legal advice.
