# Member 2 Package - Gemini Backend + Flutter AI Client
Branch: feature/gemini | Put this package's contents in: member2/ (repo root)

| Path | What |
|---|---|
| backend/ | Node 20 Express proxy: /health, /ai/ask, /ai/brief, /sos/log; Dockerfile; 8 tests; eval set; curl tests |
| lib/config/ai_config.dart | Backend URL + public key via --dart-define |
| lib/services/ai/online_ai_service.dart | Calls our backend, implements AiService |
| lib/services/ai/health_checker.dart | Cached /health ping |
| lib/services/ai/ai_router.dart | Online -> offline -> library-only; ChangeNotifier mode badge |
| test/ | Dart tests (MockClient) for service and router |
| docs/ | API, integration, safety, COMBINE_ALL_MEMBERS.md |
| tools/assemble_app.sh | Copy to repo-root tools/ ; builds the runnable app from all member folders |

## Quick start
```bash
cd backend && npm install && npm test
cp .env.example .env     # add GEMINI_API_KEY, verify GEMINI_MODEL in AI Studio
set -a; source .env; set +a; npm start
bash curl_tests.sh
```
## Honest status
- Not run in my sandbox: npm install and the tests (no internet there), the real Gemini call, and Flutter analyze/test. I only syntax-checked the JavaScript. Run `npm test` first; it needs no key.
- The default GEMINI_MODEL is a guess; confirm the current model name in Google AI Studio.
- Dart imports assume the app package is named `haqooq` and that lib/contracts exists (starter_repo).
- The backend must be deployed or reachable on your LAN for online mode; free hosts sleep.
- Never commit .env or any key.
