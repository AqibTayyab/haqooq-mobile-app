const cfg = require('./config');

function fail(msg, code) { const e = new Error(msg); e.code = code; return e; }

async function callGemini({ systemPrompt, userText }) {
  if (!cfg.geminiKey) throw fail('GEMINI_API_KEY is not configured on the server', 'upstream_error');
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${encodeURIComponent(cfg.geminiModel)}:generateContent`;
  const body = {
    systemInstruction: { parts: [{ text: systemPrompt }] },
    contents: [{ role: 'user', parts: [{ text: userText }] }],
    generationConfig: { temperature: 0.2, maxOutputTokens: 1200, responseMimeType: 'application/json' },
  };
  const ctrl = new AbortController();
  const timer = setTimeout(() => ctrl.abort(), cfg.geminiTimeoutMs);
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'x-goog-api-key': cfg.geminiKey },
      body: JSON.stringify(body),
      signal: ctrl.signal,
    });
    if (res.status === 429) throw fail('Gemini quota/rate limit', 'rate_limited');
    if (!res.ok) throw fail(`Gemini HTTP ${res.status}`, 'upstream_error');
    const data = await res.json();
    const parts = data && data.candidates && data.candidates[0] && data.candidates[0].content && data.candidates[0].content.parts;
    const text = (parts || []).map((p) => p.text || '').join('');
    if (!text) throw fail('Empty Gemini response', 'upstream_error');
    return text;
  } catch (err) {
    if (err.name === 'AbortError') throw fail('Gemini timeout', 'upstream_error');
    if (!err.code) err.code = 'upstream_error';
    throw err;
  } finally {
    clearTimeout(timer);
  }
}

module.exports = { callGemini };
