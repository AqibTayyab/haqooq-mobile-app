# How to combine all 4 members into one app

## Repo layout on GitHub
```
haqooq-mobile-app/
  docs/  members/  starter_repo/          shared docs, base app, contracts
  member1/ member2/ member3/ member4/     each member's work (own branch + PR)
  tools/assemble_app.sh                   builds the runnable app
```
Everyone works only inside their own memberN/ folder, so nobody overwrites anyone.

## Branch and PR flow
1. git checkout -b feature/<name>  (feature/ui, feature/gemini, feature/offline-ai, feature/sos-lawyer)
2. Add files only under memberN/, commit, git push -u origin feature/<name>, open a Pull Request.
3. Repo owner merges in this order: member1, member3, member2, member4. After each merge everyone runs git pull origin main.

## Assemble and run
```bash
git checkout main && git pull origin main
bash tools/assemble_app.sh      # flutter create app + starter_repo + member1..4 overlays
cd app
flutter pub get
flutter pub add flutter_gemma   # Member 3 dependency; follow its README for platform setup
flutter analyze && flutter test
flutter run --dart-define=BACKEND_BASE_URL=https://YOUR_BACKEND --dart-define=APP_PUBLIC_KEY=haqooq-demo-app
```
Overlay order: starter_repo, then member1, 2, 3, 4. Later overlays overwrite same-named files and the script prints a WARNING when that happens. Fix by renaming one file.

## Wiring checklist (Member 1, in lib/main.dart)
1. Build RagService and OfflineAiService (Member 3), HealthChecker and OnlineAiService (Member 2).
2. Build AiRouter(online, offline, rag, health) and provide it with Provider.
3. Chat screen calls router.ask(AskRequest(...)); the badge reads router.mode.
4. SOS and lawyer screens (Member 4) get their services from Provider.
5. Swap Mock services for real ones one at a time; keep a useMocks flag.

## When something breaks
- Compile error in someone else's folder: tell the owner, do not edit it.
- Short on time: remove the broken member from the overlay loop in assemble_app.sh and keep that module on its Mock. The app still runs.

## Final push
```bash
git checkout -b release/final
git add app tools && git commit -m "Final integrated HAQOOQ app"
git push -u origin release/final   # merge to main after it runs on a real phone
```
