import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/contracts/services.dart';

const _disclaimer = 'General legal information, not legal advice. Consult a licensed advocate.';

/// Offline answer built only from retrieved verified cards.
/// Phase 3 (Gemma) can wrap this: generate text, and fall back to this on any error.
class OfflineAiService implements AiService {
  final RagService _rag;
  OfflineAiService(this._rag);

  @override
  Future<AskResponse> ask(AskRequest request) async {
    final cards = await _rag.retrieve(request.question, request.language, k: 3);
    if (cards.isEmpty) {
      return const AskResponse(
        answer: 'I am not sure. Please call a lawyer or use the emergency numbers.',
        legalCategory: 'general', urgency: 'low', sources: [],
        nextSteps: ['Contact a lawyer', 'Use emergency numbers'], followUpQuestions: [],
        shouldContactLawyer: true, disclaimer: _disclaimer, mode: AiMode.offline);
    }
    final top = cards.first;
    final buf = StringBuffer(top.body);
    if (top.steps.isNotEmpty) {
      buf.write('\n\nSteps:');
      for (final s in top.steps) {
        buf.write('\n- $s');
      }
    }
    final text = buf.toString();
    final urgent = top.category == 'police_arrest' || top.category == 'domestic' || top.category == 'bail_court';
    return AskResponse(
      answer: text.length > 1200 ? text.substring(0, 1200) : text,
      legalCategory: top.category, urgency: urgent ? 'high' : 'medium',
      sources: cards.map((c) => SourceRef(c.title, c.sourceUrl)).toList(),
      nextSteps: top.steps, followUpQuestions: const [],
      shouldContactLawyer: urgent, disclaimer: _disclaimer, mode: AiMode.offline);
  }
}
