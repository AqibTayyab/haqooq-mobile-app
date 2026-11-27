# HAQOOQ Backend (Member 2)
Node 20 + Express proxy for Gemini. The Gemini key lives ONLY here (environment variable).

## Run locally
```bash
cd member2/backend
npm install
cp .env.example .env      # edit GEMINI_API_KEY and verify GEMINI_MODEL in Google AI Studio
set -a; source .env; set +a
npm start
```
Test: `npm test` (mocked Gemini, no key needed), `bash curl_tests.sh`, then `npm run eval` (needs real key).

## Phone testing on same Wi-Fi
Use your laptop LAN IP, e.g. http://192.168.x.x:8080, as BACKEND_BASE_URL. Android may block plain http in release builds (debug is fine). A deployed https URL is better.

## Deploy (pick one, ~10 minutes)
- Render/Railway: New Web Service from the GitHub repo, root directory member2/backend (or backend), build `npm install`, start `npm start`, add env vars GEMINI_API_KEY, GEMINI_MODEL, APP_PUBLIC_KEY in the dashboard. Free tiers sleep when idle: open /health before the demo.
- Google Cloud Run (hackathon cloud credits): `gcloud run deploy haqooq-backend --source . --region asia-south1 --allow-unauthenticated --set-env-vars GEMINI_MODEL=...,APP_PUBLIC_KEY=... --set-secrets GEMINI_API_KEY=...` (key in Secret Manager). Claim credits via https://g.dev/ai/usecloudcredits.

## Security
- .env is gitignored; never commit a key. If a key leaks: revoke and replace it.
- X-App-Key is NOT a secret; it only blocks casual abuse. Real protection = server-side key + rate limit.
- Cards and user text are passed as DATA; injection phrases are refused before calling Gemini.
- /sos/log is an in-memory demo log, not a real alerting system.
