const express = require('express');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
const cfg = require('./config');
const { ASK_SYSTEM, BRIEF_SYSTEM } = require('./prompts');
const V = require('./validate');
const S = require('./safety');
const realGemini = require('./gemini');

function createApp({ gemini = realGemini } = {}) {
  const app = express();
  app.set('trust proxy', 1);
  app.use(cors({ origin: cfg.allowedOrigins === '*' ? true : cfg.allowedOrigins.split(',') }));
  app.use(express.json({ limit: '50kb' }));
  app.use(rateLimit({ windowMs: 60 * 1000, max: 30, standardHeaders: true, legacyHeaders: false,
    message: { error: 'rate_limited', message: 'Too many requests, slow down.' } }));

  app.get('/health', (_req, res) => res.json({ ok: true, model: cfg.geminiModel, keyConfigured: !!cfg.geminiKey }));

  app.use((req, res, next) => {
    if (cfg.appKey && req.get('X-App-Key') !== cfg.appKey) {
      return res.status(401).json({ error: 'unauthorized', message: 'Missing or wrong X-App-Key' });
    }
    next();
  });

  const run = (fn) => async (req, res) => {
    try { await fn(req, res); } catch (err) {
      const code = err.code || 'upstream_error';
      const status = code === 'bad_request' ? 400 : code === 'rate_limited' ? 429 : 502;
      console.error('[error]', code, err.message);
      res.status(status).json({ error: code, message: code === 'bad_request' ? err.message : 'AI service unavailable, use offline mode.' });
    }
  };

  async function askGemini(system, userText, normalize) {
    let lastErr;
    for (let i = 0; i < 2; i++) {
      try {
        const text = await gemini.callGemini({ systemPrompt: system, userText });
        return normalize(V.parseModelJson(text));
      } catch (e) {
        lastErr = e;
        if (e.code === 'rate_limited') throw e;
      }
    }
    const e = new Error(lastErr ? lastErr.message : 'invalid output'); e.code = 'upstream_error'; throw e;
  }

  app.post('/ai/ask', run(async (req, res) => {
    const r = V.parseAskRequest(req.body);
    const combined = r.question + ' ' + r.history.map((h) => h.text).join(' ');
    if (S.isUnsafe(combined)) return res.json(S.refusalResponse(r.language));
    const cardsText = r.cards.map((c, i) => `[${i + 1}] title: ${c.title}\nsource: ${c.source_name} ${c.source_url}\n${c.body}`).join('\n---\n') || '(no cards provided)';
    const hist = r.history.map((h) => `${h.role}: ${h.text}`).join('\n');
    const userText = `LANGUAGE: ${r.language}\nCITY: ${r.city || 'unknown'}\nCONTEXT: ${JSON.stringify(r.context)}\n<cards>\n${cardsText}\n</cards>\n<history>\n${hist}\n</history>\n<question>\n${r.question}\n</question>`;
    const out = await askGemini(ASK_SYSTEM, userText, (raw) => V.normalizeAsk(raw, r.cards));
    if (S.isEmergency(combined)) { out.urgency = 'high'; out.should_contact_lawyer = true; }
    res.json(out);
  }));

  app.post('/ai/brief', run(async (req, res) => {
    const r = V.parseBriefRequest(req.body);
    const convo = r.conversation.map((h) => `${h.role}: ${h.text}`).join('\n');
    const userText = `LANGUAGE: ${r.language}\n<conversation>\n${convo}\n</conversation>\n<notes>\n${r.notes}\n</notes>`;
    res.json(await askGemini(BRIEF_SYSTEM, userText, V.normalizeBrief));
  }));

  // Demo log only: in memory, no persistence, minimal fields.
  const sosLog = [];
  app.post('/sos/log', run(async (req, res) => {
    const b = req.body || {};
    const entry = {
      alert_id: 'sos_' + Date.now().toString(36),
      lat: typeof b.lat === 'number' ? b.lat : null, lng: typeof b.lng === 'number' ? b.lng : null,
      battery: typeof b.battery === 'number' ? b.battery : null, at: new Date().toISOString(),
    };
    sosLog.push(entry); if (sosLog.length > 200) sosLog.shift();
    res.json({ status: 'logged', alert_id: entry.alert_id });
  }));

  app.use((_req, res) => res.status(404).json({ error: 'not_found' }));
  return app;
}

module.exports = { createApp };
