# Data Model and API Contracts

## SQLite (local)
legal_cards(id TEXT PK, title, category, lang, body, steps_json, source_name, source_url, last_reviewed, urgency_hint, keywords)
case_notes(id TEXT PK, title, description, timeline_json, ai_summary, lawyer_brief, created_at)
trusted_contacts(id TEXT PK, name, phone, relationship, is_primary)
incidents(id TEXT PK, lat, lng, battery, message, status, created_at)
settings(key TEXT PK, value)

## Remote (Firestore/Supabase) - optional for hackathon
lawyers, reviews, bookings, chat_messages (demo lawyer can be local JSON if time is short).

## Categories
police_arrest, fir, bail_court, women_safety, domestic, cybercrime, family, property, employment, consumer, emergency.

## API (backend)
All requests: JSON, HTTPS, header X-App-Key (public app identifier, not the Gemini key), max body 20 KB.

### POST /ai/ask
Request:
```json
{"question":"Police ne mujhe roka hai, kya karun?","language":"roman_ur","city":"Multan",
 "context":{"detained":false,"fir_registered":false,"violence":false},
 "cards":[{"id":"police_stop_001","title":"...","body":"...","source_name":"...","source_url":"..."}],
 "history":[{"role":"user","text":"..."}]}
```
Response:
```json
{"answer":"...","legal_category":"police_arrest","urgency":"high",
 "sources":[{"title":"...","url":"..."}],"next_steps":["..."],
 "follow_up_questions":["..."],"should_contact_lawyer":true,
 "disclaimer":"General legal information, not legal advice. Consult a licensed advocate."}
```
Errors: {"error":"code","message":"..."} codes: bad_request, rate_limited, upstream_error, unsafe_request.

### POST /ai/brief
Request {"language":"en","conversation":[...],"notes":"..."} -> Response {"summary","timeline":[{"time","event"}],"people_involved":[],"evidence_checklist":[],"questions_for_lawyer":[],"urgency"}

### POST /sos/log  {"lat","lng","battery","message","contact_ids":[]} -> {"status":"logged","alert_id":"..."}
### GET /lawyers?city=&specialization=&language= -> {"lawyers":[Lawyer]}
### GET /health -> {"ok":true}

## Dart contracts
See starter_repo/lib/contracts/*.dart. Same field names in snake_case JSON.
