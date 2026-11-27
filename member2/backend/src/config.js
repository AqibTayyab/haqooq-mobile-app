module.exports = {
  port: parseInt(process.env.PORT || '8080', 10),
  geminiKey: process.env.GEMINI_API_KEY || '',
  geminiModel: process.env.GEMINI_MODEL || 'gemini-2.5-flash',
  appKey: process.env.APP_PUBLIC_KEY || '',
  allowedOrigins: process.env.ALLOWED_ORIGINS || '*',
  geminiTimeoutMs: 20000,
  maxQuestionChars: 2000,
  maxCards: 5,
  maxCardChars: 1500,
};
