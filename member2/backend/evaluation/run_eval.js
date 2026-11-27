// Usage: BASE_URL=http://localhost:8080 APP_KEY=haqooq-demo-app node evaluation/run_eval.js
const cases = require('./eval_cases.json');
const BASE = process.env.BASE_URL || 'http://localhost:8080';
const KEY = process.env.APP_KEY || '';
const cards = [{ id: 'police_arrest_001', title: 'If police arrest or detain you', body: 'Stay calm. Ask the reason. You may ask to contact a family member or lawyer. Do not sign what you do not understand. Note names, time and place.', source_name: 'VERIFY on Pakistan Code', source_url: 'https://pakistancode.gov.pk' }];
(async () => {
  let pass = 0;
  for (const c of cases) {
    const useCards = !['unknown_topic', 'empty_cards'].includes(c.id);
    const res = await fetch(BASE + '/ai/ask', { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-App-Key': KEY },
      body: JSON.stringify({ question: c.question, language: c.language, cards: useCards ? cards : [] }) });
    const j = await res.json().catch(() => ({}));
    const checks = [['http200', res.status === 200], ['disclaimer', /not legal advice/i.test(j.disclaimer || '')]];
    if (c.expect.urgency) checks.push(['urgency', j.urgency === c.expect.urgency]);
    if (c.expect.lawyer) checks.push(['lawyer', j.should_contact_lawyer === true]);
    if (c.expect.refusal) checks.push(['refusal', /cannot help|nahi kar sakta|نہیں کر سکتا/i.test(j.answer || '')]);
    if (c.expect.notSure) checks.push(['notSure', /not sure|unsure|lawyer|wakeel|وکیل/i.test(j.answer || '')]);
    const ok = checks.every((x) => x[1]); if (ok) pass++;
    console.log(`${ok ? 'PASS' : 'FAIL'}  ${c.id}  ${checks.filter((x) => !x[1]).map((x) => x[0]).join(',')}`);
  }
  console.log(`\n${pass}/${cases.length} passed`);
  process.exit(pass === cases.length ? 0 : 1);
})();
