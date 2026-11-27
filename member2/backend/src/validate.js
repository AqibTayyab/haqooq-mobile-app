const cfg = require('./config');
const { DISCLAIMER } = require('./prompts');
const LANGS = ['ur', 'en', 'roman_ur'];
const URG = ['low', 'medium', 'high'];

function badRequest(msg) { const e = new Error(msg); e.code = 'bad_request'; return e; }
const str = (v, max = 4000) => (typeof v === 'string' ? v.slice(0, max) : '');
const arrStr = (v, max = 10) => (Array.isArray(v) ? v.filter((x) => typeof x === 'string').slice(0, max).map((x) => x.slice(0, 500)) : []);

function parseAskRequest(b) {
  if (!b || typeof b !== 'object') throw badRequest('Body must be JSON');
  const question = str(b.question, cfg.maxQuestionChars).trim();
  if (!question) throw badRequest('question is required');
  const language = LANGS.includes(b.language) ? b.language : 'en';
  const cards = (Array.isArray(b.cards) ? b.cards : []).slice(0, cfg.maxCards).map((c) => ({
    id: str(c && c.id, 100), title: str(c && c.title, 200), body: str(c && c.body, cfg.maxCardChars),
    source_name: str(c && c.source_name, 200), source_url: str(c && c.source_url, 500),
  })).filter((c) => c.title && c.body);
  const history = (Array.isArray(b.history) ? b.history : []).slice(-6).map((h) => ({
    role: h && h.role === 'assistant' ? 'assistant' : 'user', text: str(h && h.text, 600),
  }));
  const context = (b.context && typeof b.context === 'object') ? b.context : {};
  return { question, language, city: str(b.city, 80), context, cards, history };
}

function parseBriefRequest(b) {
  if (!b || typeof b !== 'object') throw badRequest('Body must be JSON');
  const language = LANGS.includes(b.language) ? b.language : 'en';
  const conversation = (Array.isArray(b.conversation) ? b.conversation : []).slice(-20).map((h) => ({
    role: h && h.role === 'assistant' ? 'assistant' : 'user', text: str(h && h.text, 800),
  }));
  const notes = str(b.notes, 3000);
  if (!conversation.length && !notes.trim()) throw badRequest('conversation or notes required');
  return { language, conversation, notes };
}

function parseModelJson(text) {
  let t = text.trim();
  t = t.replace(/^```(?:json)?/i, '').replace(/```$/, '').trim();
  try { return JSON.parse(t); } catch (_) {
    const s = t.indexOf('{'), e = t.lastIndexOf('}');
    if (s >= 0 && e > s) return JSON.parse(t.slice(s, e + 1));
    throw new Error('invalid json');
  }
}

function normalizeAsk(raw, cards) {
  const allowed = cards.map((c) => ({ title: c.title, url: c.source_url }));
  let sources = (Array.isArray(raw.sources) ? raw.sources : [])
    .map((s) => ({ title: str(s && s.title, 200), url: str(s && s.url, 500) }))
    .filter((s) => allowed.some((a) => a.title === s.title || (a.url && a.url === s.url)));
  if (!sources.length) sources = allowed.slice(0, 3);
  return {
    answer: str(raw.answer, 3000) || 'I am not sure. Please consult a licensed lawyer.',
    legal_category: str(raw.legal_category, 60) || 'general',
    urgency: URG.includes(raw.urgency) ? raw.urgency : 'medium',
    sources,
    next_steps: arrStr(raw.next_steps, 8),
    follow_up_questions: arrStr(raw.follow_up_questions, 3),
    should_contact_lawyer: raw.should_contact_lawyer === true,
    disclaimer: DISCLAIMER,
  };
}

function normalizeBrief(raw) {
  const tl = (Array.isArray(raw.timeline) ? raw.timeline : []).slice(0, 15)
    .map((x) => ({ time: str(x && x.time, 80) || 'unknown', event: str(x && x.event, 400) }));
  return {
    summary: str(raw.summary, 2000) || 'unknown',
    timeline: tl, people_involved: arrStr(raw.people_involved, 10),
    evidence_checklist: arrStr(raw.evidence_checklist, 12),
    questions_for_lawyer: arrStr(raw.questions_for_lawyer, 10),
    urgency: URG.includes(raw.urgency) ? raw.urgency : 'medium',
  };
}

module.exports = { parseAskRequest, parseBriefRequest, parseModelJson, normalizeAsk, normalizeBrief };
