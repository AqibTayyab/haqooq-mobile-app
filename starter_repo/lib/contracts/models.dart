class LegalCard {
  final String id, title, category, lang, body, sourceName, sourceUrl, lastReviewed;
  final List<String> steps, keywords;
  const LegalCard({required this.id, required this.title, required this.category, required this.lang,
    required this.body, required this.steps, required this.sourceName, required this.sourceUrl,
    required this.lastReviewed, required this.keywords});
  factory LegalCard.fromJson(Map<String, dynamic> j) => LegalCard(
    id: j['id'] as String, title: j['title'] as String, category: j['category'] as String,
    lang: j['lang'] as String, body: j['body'] as String,
    steps: List<String>.from(j['steps'] ?? const []), sourceName: (j['source_name'] ?? '') as String,
    sourceUrl: (j['source_url'] ?? '') as String, lastReviewed: (j['last_reviewed'] ?? '') as String,
    keywords: List<String>.from(j['keywords'] ?? const []));
  Map<String, dynamic> toJson() => {'id': id, 'title': title, 'category': category, 'lang': lang,
    'body': body, 'steps': steps, 'source_name': sourceName, 'source_url': sourceUrl,
    'last_reviewed': lastReviewed, 'keywords': keywords};
}

enum AiMode { online, offline, libraryOnly }

class AskRequest {
  final String question, language;
  final String? city;
  final Map<String, bool> context;
  final List<LegalCard> cards;
  final List<Map<String, String>> history;
  const AskRequest({required this.question, required this.language, this.city,
    this.context = const {}, this.cards = const [], this.history = const []});
  Map<String, dynamic> toJson() => {'question': question, 'language': language, 'city': city,
    'context': context, 'cards': cards.map((c) => c.toJson()).toList(), 'history': history};
}

class SourceRef {
  final String title, url;
  const SourceRef(this.title, this.url);
}

class AskResponse {
  final String answer, legalCategory, urgency, disclaimer;
  final List<SourceRef> sources;
  final List<String> nextSteps, followUpQuestions;
  final bool shouldContactLawyer;
  final AiMode mode;
  const AskResponse({required this.answer, required this.legalCategory, required this.urgency,
    required this.sources, required this.nextSteps, required this.followUpQuestions,
    required this.shouldContactLawyer, required this.disclaimer, this.mode = AiMode.online});
  factory AskResponse.fromJson(Map<String, dynamic> j, {AiMode mode = AiMode.online}) => AskResponse(
    answer: (j['answer'] ?? '') as String, legalCategory: (j['legal_category'] ?? 'general') as String,
    urgency: (j['urgency'] ?? 'low') as String,
    sources: ((j['sources'] ?? []) as List).map((s) => SourceRef((s['title'] ?? '') as String, (s['url'] ?? '') as String)).toList(),
    nextSteps: List<String>.from(j['next_steps'] ?? const []),
    followUpQuestions: List<String>.from(j['follow_up_questions'] ?? const []),
    shouldContactLawyer: (j['should_contact_lawyer'] ?? false) as bool,
    disclaimer: (j['disclaimer'] ?? 'General legal information, not legal advice. Consult a licensed advocate.') as String,
    mode: mode);
}

class TrustedContact {
  final String id, name, phone, relationship;
  final bool isPrimary;
  const TrustedContact({required this.id, required this.name, required this.phone,
    this.relationship = '', this.isPrimary = false});
}

class Lawyer {
  final String id, name, city, bio;
  final List<String> specializations, languages;
  final double rating;
  final int fee;
  final bool isVerified, isDemo;
  const Lawyer({required this.id, required this.name, required this.city, required this.bio,
    required this.specializations, required this.languages, required this.rating,
    required this.fee, required this.isVerified, required this.isDemo});
}

class SosResult {
  final bool locationIncluded;
  final String message;
  const SosResult(this.locationIncluded, this.message);
}
