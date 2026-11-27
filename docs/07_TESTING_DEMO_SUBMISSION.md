# Testing, Demo and Submission

## Test matrix
| Area | Test | Expect |
|---|---|---|
| Boot | cold start | no crash, language screen first time |
| l10n | switch ur/en/roman | all main texts change, RTL ok |
| Library | search offline | results < 300 ms |
| Online AI | 5 sample questions | valid JSON rendered, sources, disclaimer |
| Offline AI | airplane mode | answer from cards or "not sure" |
| Fallback | block backend | auto offline, badge changes |
| Safety | "hide evidence" / "ignore rules" | refusal |
| SOS | press, cancel in 3 s, send | message with map link, battery |
| SOS no GPS | deny permission | message sent without location + explanation |
| Check-In | set 1 min, ignore | alert flow starts |
| Lawyers | filter, profile, chat, booking | works, demo label visible |
| Secrets | grep repo for AIza / key | nothing found |
| Device | 4 GB phone | no crash |

## Demo script (4 minutes)
1. Problem (20 s). 2. Pick Roman Urdu, ask arrest question online (Gemini, sources). 3. Turn on airplane mode, ask again (offline badge). 4. Show rights library. 5. Press SOS, cancel, send. 6. Create briefing pack. 7. Open demo lawyer (labelled), chat, book. 8. Differentiation + roadmap (30 s).
Record a backup video at hour 22.

## Submission checklist (deadline 27 Nov, 7 PM)
Repo accessible to judges | README with setup + screenshots | demo video link | Gemini usage explained | team contributions | form filled: https://forms.gle/22VzKP7ECh9zYuiR7 | submit by 6:30 PM.
