# HAQOOQ — Complete Project Documentation Pack
Build with AI Multan 2026 (SKYRA Pakistan) | Deadline: 27 Nov, 7:00 PM | Team of 4
Repository: https://github.com/AqibTayyab/haqooq-mobile-app

HAQOOQ is a Flutter mobile app: offline legal-rights library + AI legal assistant (Gemini online, Gemma on-device offline) + emergency SOS/Safety Check-In + verified-lawyer marketplace (demo lawyer for hackathon).

## What is in this pack
| File | Purpose |
|---|---|
| docs/00_CRITICAL_RULES.md | Mandatory rules. Everyone reads first |
| docs/01_PRD.md | Product requirements |
| docs/02_ARCHITECTURE.md | System architecture and decisions |
| docs/03_BLUEPRINT.md | Screens, flows, folder blueprint, build order |
| docs/04_DATA_AND_API.md | Database schema and API contracts |
| docs/05_AI_SYSTEM.md | Gemini + Gemma prompts, RAG, safety |
| docs/06_GIT_WORKFLOW.md | Branching, PRs, merge order, conflict avoidance |
| docs/07_TESTING_DEMO_SUBMISSION.md | Test plan, demo script, submission checklist |
| docs/08_LEGAL_CONTENT_GUIDE.md | How to write and verify legal cards |
| members/MEMBER1..4_*.md | Full technical knowledge + phases + one AI prompt per phase |
| members/00_MASTER_CONTEXT_PROMPT.md | Paste first in every new AI chat |
| starter_repo/ | Files to commit first so everyone builds on the same base |

## How to start (Member 1 / repo owner, ~20 minutes)
1. Clone the repo: git clone https://github.com/AqibTayyab/haqooq-mobile-app.git
2. Inside it run: flutter create . --org com.haqooq --project-name haqooq
3. Copy everything from starter_repo/ into the repo root (overwrite lib/main.dart and pubspec.yaml), copy docs/ and members/ into the repo.
4. flutter pub get && flutter analyze (must pass) then commit and push to main:
   git add . && git commit -m "chore: project bootstrap" && git push origin main
5. Invite the 3 teammates as collaborators. Turn on branch protection for main.
6. Each member opens their file in members/, pastes 00_MASTER_CONTEXT_PROMPT.md into a new AI chat, then pastes that phase's prompt.

Honest note: package versions in pubspec.yaml are starting points. If flutter pub get complains, run flutter pub upgrade --major-versions or adjust. Verify before the hackathon rush.
