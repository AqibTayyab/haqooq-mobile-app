# HAQOOQ Backend API (Member 2)
Headers: Content-Type: application/json, X-App-Key: <APP_PUBLIC_KEY>. Limits: body 50 KB, question 2000 chars, 30 requests/min/IP.

## GET /health -> {"ok":true,"model":"...","keyConfigured":true}  (no X-App-Key needed)

## POST /ai/ask
Request: {"question","language":"ur|en|roman_ur","city","context":{},"cards":[{"id","title","body","source_name","source_url"}],"history":[{"role","text"}]}
Response (same shape for refusals): {"answer","legal_category","urgency":"low|medium|high","sources":[{"title","url"}],"next_steps":[],"follow_up_questions":[],"should_contact_lawyer":bool,"disclaimer"}
- Unsafe requests (hide evidence, lie to police, prompt injection) get a 200 refusal; Gemini is not called.
- Emergency words force urgency=high and should_contact_lawyer=true.
- sources are filtered to the provided cards (the model cannot invent sources).
- Invalid model JSON is retried once, then 502 upstream_error.
Errors: 400 bad_request | 401 unauthorized | 429 rate_limited | 502 upstream_error. Body {"error","message"}.

## POST /ai/brief
Request: {"language","conversation":[{"role","text"}],"notes"}
Response: {"summary","timeline":[{"time","event"}],"people_involved":[],"evidence_checklist":[],"questions_for_lawyer":[],"urgency"}

## POST /sos/log
Request {"lat","lng","battery"} -> {"status":"logged","alert_id"}. In-memory demo log only.

Change vs docs/04: unsafe_request is a normal 200 refusal, not an error code (simpler for the client).
