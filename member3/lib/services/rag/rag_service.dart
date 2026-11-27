import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/contracts/services.dart';
import 'database_service.dart';
import 'intent_classifier.dart';
import 'tokenizer.dart';

class HaqooqRagService implements RagService {
  final DatabaseService _db;
  final Tokenizer _tokenizer;
  final IntentClassifier _classifier;

  HaqooqRagService({DatabaseService? db, Tokenizer? tokenizer, IntentClassifier? classifier})
      : _db = db ?? DatabaseService(),
        _tokenizer = tokenizer ?? Tokenizer(),
        _classifier = classifier ?? IntentClassifier();

  IntentResult classify(String q) => _classifier.classify(q);

  @override
  Future<List<LegalCard>> retrieve(String query, String lang, {int k = 3}) async {
    final intent = _classifier.classify(query);
    final tokens = _tokenizer.tokenize(query);
    if (tokens.isEmpty) return [];
    final cards = await _db.allCards();
    final scored = <MapEntry<LegalCard, double>>[];
    for (final c in cards) {
      if (lang != 'any' && c.lang != lang) continue;
      final title = _tokenizer.tokenize(c.title).toSet();
      final kws = c.keywords.expand(_tokenizer.tokenize).toSet();
      final body = _tokenizer.tokenize(c.body).toSet();
      double score = 0;
      for (final t in tokens) {
        if (title.contains(t)) score += 3;
        if (kws.contains(t)) score += 3;
        if (body.contains(t)) score += 1;
      }
      if (score > 0 && c.category == intent.category) score += 2;
      if (score > 0) scored.add(MapEntry(c, score));
    }
    scored.sort((a, b) => b.value.compareTo(a.value));
    return scored.where((e) => e.value >= 3).take(k).map((e) => e.key).toList();
  }
}
