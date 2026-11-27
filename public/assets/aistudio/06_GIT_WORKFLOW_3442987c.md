# Git Workflow
Repo: https://github.com/AqibTayyab/haqooq-mobile-app

## Branches
main (protected) | feature/ui (M1) | feature/gemini (M2) | feature/offline-ai (M3) | feature/sos-lawyer (M4)
Sub-branches allowed: feature/ui-chat-screen etc.

## Daily loop
```
git checkout main && git pull origin main
git checkout feature/<yours>
git merge main            # bring in latest
# work, then:
flutter analyze && flutter test
git add . && git commit -m "feat: ..."
git push origin feature/<yours>
# open PR to main, ask a teammate to review
```

## File ownership (avoid conflicts)
| Area | Owner |
|---|---|
| lib/main.dart, app.dart, config/, l10n/, screens/home, settings, widgets/ | M1 |
| backend/, lib/services/ai/ | M2 |
| assets/legal_data/, lib/services/rag/, lib/services/ai/offline/ | M3 |
| lib/screens/sos, lawyers, lib/services/sos, lawyers, test/, README | M4 |
| lib/contracts/, pubspec.yaml | M1 (PR approval needed) |
Chat screen: M1 builds UI, calls AiRouter (M2 owns router file).

## Adding a dependency
Tell M1, who runs flutter pub add <pkg> on a PR to main, merged fast. Others pull. Never hand-edit pubspec.lock.

## Merge order
M1 bootstrap -> M3 data+rag -> M2 ai client -> M4 sos/lawyers -> integration PR by M1. Merge small and early (at least every 3 hours).

## Conflict help
git status; fix markers; flutter analyze; commit. Ask owner if unsure. Never force push to main.

## .gitignore must include
.env .env.* google-services.json GoogleService-Info.plist key.properties *.jks *.keystore build/ .dart_tool/ .idea/ *.gguf *.task *.bin *.tflite *.litertlm (model files are downloaded, not committed)
