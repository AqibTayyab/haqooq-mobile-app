const { DISCLAIMER } = require('./prompts');

const UNSAFE = [
  /hide (the )?evidence/i, /destroy (the )?evidence/i, /delete (the )?evidence/i, /fake (the )?evidence/i,
  /lie to (the )?police/i, /bribe/i, /escape (from )?(the )?police/i, /run away from (the )?police/i,
  /resist arrest/i, /evidence (chupa|mita)/i, /saboot (chupa|mita)/i, /jhoot bol/i, /rishwat/i,
  /ignore (all |your )?(previous |above )?(rules|instructions)/i, /reveal (your )?(system )?prompt/i,
];
const EMERGENCY = [
  /arrest(ed|ing)?\b/i, /detain(ed)?/i, /kidnap/i, /threat/i, /violence/i, /beat(ing)?/i, /bachao/i, /maar (raha|rahe|rahi)/i,
  /giraftar/i, /dhamki/i, /hostage/i, /abduct/i,
];

const isUnsafe = (text) => UNSAFE.some((r) => r.test(text));
const isEmergency = (text) => EMERGENCY.some((r) => r.test(text));

const REFUSALS = {
  en: 'I cannot help with hiding or destroying evidence, lying to authorities, resisting lawful arrest, or ignoring safety rules. Lawful options: stay calm, ask the reason for the action, contact a licensed lawyer or a family member, and keep records of what happened.',
  roman_ur: 'Main saboot chupane, jhoot bolne, giraftari se bachne ya rules ignore karne mein madad nahi kar sakta. Qanooni raasta: pur sukoon rahein, wajah poochein, kisi licensed wakeel ya ghar walon se rabta karein aur jo hua uska record rakhein.',
  ur: 'میں ثبوت چھپانے، جھوٹ بولنے، قانونی گرفتاری سے بچنے یا حفاظتی اصولوں کو نظر انداز کرنے میں مدد نہیں کر سکتا۔ قانونی راستہ: پرسکون رہیں، وجہ پوچھیں، لائسنس یافتہ وکیل یا گھر والوں سے رابطہ کریں اور جو ہوا اس کا ریکارڈ رکھیں۔',
};

function refusalResponse(language) {
  return {
    answer: REFUSALS[language] || REFUSALS.en,
    legal_category: 'general', urgency: 'medium', sources: [],
    next_steps: ['Stay calm', 'Contact a licensed lawyer', 'Keep honest records'],
    follow_up_questions: [], should_contact_lawyer: true, disclaimer: DISCLAIMER,
  };
}

module.exports = { isUnsafe, isEmergency, refusalResponse };
