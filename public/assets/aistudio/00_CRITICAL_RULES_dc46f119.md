# CRITICAL RULES (mandatory for all 4 members)

## Security
1. NEVER put the Gemini API key, Firebase keys or any secret in Flutter code, pubspec.yaml or GitHub. Keys live only in backend environment variables.
2. All Gemini calls go through the backend proxy (Cloud Function / Edge Function). The app only calls our own endpoint.
3. .env, .env.*, google-services.json, GoogleService-Info.plist, *.jks, key.properties, build/, .dart_tool/ must be in .gitignore BEFORE the first commit. If a key leaks: revoke it immediately and create a new one.
4. Do not send unnecessary personal data to Gemini. Never use private case data for training.

## Git
5. Never push to main directly. One feature branch per member, PR for every change, at least one review by someone else.
6. Pull latest main before starting and before opening a PR: git pull origin main.
7. Only edit files in your own module. Shared files (pubspec.yaml, main.dart, routes) are changed only by Member 1, or by PR with Member 1 approval.
8. Commit small and often: feat:, fix:, docs:, refactor:, test:.
9. Code on main must always pass: flutter pub get, flutter analyze, flutter test.

## Legal and product safety
10. HAQOOQ gives general legal INFORMATION, not legal advice. Every AI answer shows: "General legal information, not legal advice. Consult a licensed advocate."
11. AI answers from the verified HAQOOQ legal cards first. If the cards do not cover the question, say so and recommend a lawyer. Never invent sections, case names or punishments.
12. Never encourage lying, hiding or destroying evidence, resisting lawful arrest, or evading investigation.
13. For detention, violence, threats or urgent criminal matters: show emergency contacts and lawyer escalation first, AI second.
14. Never auto-share location with police. Police/legal-aid alerts need explicit user consent.
15. The demo lawyer is labelled "Demo Account - Not a Real Lawyer" everywhere. Never present a fake profile as real.
16. Do not promise fully-offline SOS. Location delivery needs mobile data, Wi-Fi or SMS. Offline = rights library, offline AI, emergency contacts, local notes.

## Engineering
17. Test on a real Android phone, not only the emulator, including airplane mode.
18. Every screen needs loading, empty, error and offline states.
19. Code only against the interfaces in lib/contracts/. Do not change a contract without telling everyone.
20. If a feature fails near the deadline, ship the fallback and say so in the demo. Do not hide it.
