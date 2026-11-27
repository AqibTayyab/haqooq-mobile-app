import 'package:flutter/foundation.dart';
import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/contracts/services.dart';
import 'health_checker.dart';

/// ONLINE (Gemini via backend) -> OFFLINE (Member 3 Gemma+RAG) -> LIBRARY_ONLY.
/// UI listens to [mode] for the green / orange / grey badge.
class AiRouter extends ChangeNotifier implements AiService {
  AiRouter({
    required this.online,
    required this.rag,
    required this.health,
    this.offline,
  });

  final AiService online;
  final AiService? offline;
  final RagService rag;
  final HealthChecker health;

  AiMode _mode = AiMode.online;
  AiMode get mode => _mode;

  void _set(AiMode m) {
    if (m != _mode) {
      _mode = m;
      notifyListeners();
    }
  }

  @override
  Future<AskResponse> ask(AskRequest request) async {
    final cards = request.cards.isNotEmpty
        ? request.cards
        : await rag.retrieve(request.question, request.language, k: 3);
    final withCards = AskRequest(
      question: request.question,
      language: request.language,
      city: request.city,
      context: request.context,
      cards: cards,
      history: request.history,
    );

    if (await health.isOnline()) {
      try {
        final r = await online.ask(withCards);
        _set(AiMode.online);
        return r;
      } catch (_) {
        // fall through to offline
      }
    }
    final off = offline;
    if (off != null) {
      try {
        final r = await off.ask(withCards);
        _set(AiMode.offline);
        return r;
      } catch (_) {
        // fall through to library
      }
    }
    _set(AiMode.libraryOnly);
    return _libraryResponse(cards);
  }

  AskResponse _libraryResponse(List<LegalCard> cards) {
    const disclaimer = 'General legal information, not legal advice. Consult a licensed advocate.';
    if (cards.isEmpty) {
      return const AskResponse(
        answer: 'I am not sure. Please contact a licensed lawyer or call the emergency numbers.',
        legalCategory: 'general',
        urgency: 'medium',
        sources: [],
        nextSteps: [],
        followUpQuestions: [],
        shouldContactLawyer: true,
        disclaimer: disclaimer,
        mode: AiMode.libraryOnly,
      );
    }
    final top = cards.first;
    return AskResponse(
      answer: top.body,
      legalCategory: top.category,
      urgency: 'medium',
      sources: [for (final c in cards) SourceRef(c.title, c.sourceUrl)],
      nextSteps: top.steps,
      followUpQuestions: const [],
      shouldContactLawyer: true,
      disclaimer: disclaimer,
      mode: AiMode.libraryOnly,
    );
  }
}
