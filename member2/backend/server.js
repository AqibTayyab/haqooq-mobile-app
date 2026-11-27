const { createApp } = require('./src/app');
const cfg = require('./src/config');
createApp().listen(cfg.port, () => console.log(`HAQOOQ backend on :${cfg.port} (model ${cfg.geminiModel}, key ${cfg.geminiKey ? 'set' : 'MISSING'})`));
