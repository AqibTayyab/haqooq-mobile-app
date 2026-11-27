# Product Requirements Document (PRD) — HAQOOQ

## 1. Summary
HAQOOQ helps ordinary Pakistani citizens know their rights, stay safe in an emergency and reach a verified lawyer. Works offline for rights guidance; uses Gemini online for deeper help.
Tagline: Know your rights. Stay safe. Reach the right lawyer.

## 2. Problem
People facing police trouble, FIRs, harassment, domestic issues, fraud, cybercrime or property/employment disputes usually do not know their rights, procedure, what evidence to save, or which lawyer to trust. Existing legal-AI tools in Pakistan (e.g. DigiLawyer, Wakeel, PakistanLawBot) mostly target research/drafting for lawyers or need internet; SOS apps (e.g. Punjab Women Safety App, FALAH SOS, Iyzil) lack deep legal guidance. The gap: citizen-first, offline-capable rights guidance + SOS + lawyer handover in one app.

## 3. Goals (hackathon)
- G1 Working Flutter app on Android (iOS if time permits).
- G2 Online AI answers via Gemini through a secure backend, grounded in HAQOOQ legal cards.
- G3 Offline mode: rights library + on-device AI (Gemma) answering from local cards.
- G4 SOS with trusted contacts, location, SMS/share fallback, Safety Check-In timer.
- G5 Lawyer directory, profile, reviews, booking, chat with a clearly-labelled demo lawyer.
- G6 Lawyer Briefing Pack generated from a conversation.

## 4. Non-goals (do not build)
Real lawyer verification, real payments, real police integration, court filing, deep case-law search, document OCR at scale, app-store release, production security audit. Mention only as roadmap.

## 5. Users
| Persona | Need |
|---|---|
| Bilal, delivery rider | Stopped by police, wants to know rights now |
| Ayesha, student | Travels late, wants Safety Check-In and SOS |
| Sana, working woman | Workplace harassment options and a lawyer |
| Ahmed, shop owner | Online fraud complaint steps |

## 6. Functional requirements
| ID | Requirement | Priority |
|---|---|---|
| FR1 | Language choice Urdu / English / Roman Urdu, persisted | Must |
| FR2 | Offline rights library: categories, search, article, source, last-reviewed date | Must |
| FR3 | AI chat with mode badge (Online Gemini / Offline Gemma / Library only) | Must |
| FR4 | Every AI reply shows sources, urgency, next steps, disclaimer, action buttons | Must |
| FR5 | Auto-fallback online -> offline AI -> library when connectivity or model fails | Must |
| FR6 | SOS button with 3-second cancel countdown, location+battery message to trusted contacts | Must |
| FR7 | Trusted contacts CRUD (max 5) | Must |
| FR8 | Safety Check-In timer; alert if not confirmed | Should |
| FR9 | Lawyer directory with filters, profile, reviews, booking | Must |
| FR10 | Demo lawyer chat | Must |
| FR11 | Lawyer Briefing Pack (summary, timeline, questions, urgency) shareable | Should |
| FR12 | Emergency numbers screen (Police 15 etc.; verify before release) | Must |
| FR13 | Voice input | Could |
| FR14 | Document upload analysis (online only) | Could |

## 7. Non-functional requirements
- Offline: FR2, FR3 (offline), FR6 (prepare message), FR7, FR12 work without internet.
- Performance: library search < 300 ms; offline AI answer target < 20 s on a 4 GB phone; online answer < 8 s typical.
- Privacy: encrypted sensitive local data, delete-my-data option, minimal data to backend.
- Accessibility: large SOS button, high contrast, Urdu RTL, readable Roman Urdu.
- Reliability: app never shows a blank error; always a fallback.

## 8. Success metrics (demo)
Full journey works live: ask question online -> turn off internet -> ask again offline -> SOS -> briefing -> demo lawyer chat -> booking. Zero crashes, zero secrets in repo.

## 9. Risks
| Risk | Mitigation |
|---|---|
| Gemini/API down or quota | Offline fallback + recorded demo video |
| Gemma too heavy | Use smaller model, or library-only mode |
| Wrong legal content | Official sources only, review by a law student/lawyer, source on every card |
| Merge conflicts | Module ownership + contracts + PR flow |
| Key leak | Backend proxy + .gitignore |
| Time | MVP only, phases with checkpoints |

Repository: https://github.com/AqibTayyab/haqooq-mobile-app
