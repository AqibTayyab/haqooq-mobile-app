# How others use Member 2's work
Member 1 (UI): use AiRouter (implements AiService) in the chat screen; listen to router.mode for the badge (online green, offline orange, libraryOnly grey).
```dart
final health = HealthChecker(baseUrl: AiConfig.backendBaseUrl);
final online = OnlineAiService(baseUrl: AiConfig.backendBaseUrl, appKey: AiConfig.appPublicKey);
final router = AiRouter(online: online, offline: offlineAiOrNull, rag: ragService, health: health);
// ChangeNotifierProvider.value(value: router)
```
Run: flutter run --dart-define=BACKEND_BASE_URL=https://YOUR_BACKEND --dart-define=APP_PUBLIC_KEY=haqooq-demo-app
Member 3: pass your OfflineAiService as `offline:` and your RagService as `rag:`. The router sends the same top cards to Gemini that you use offline.
Member 4: OnlineAiService.brief(...) returns the Lawyer Briefing Pack JSON (summary, timeline, evidence_checklist, questions_for_lawyer, urgency).
Dependency needed: http (already in starter pubspec).
