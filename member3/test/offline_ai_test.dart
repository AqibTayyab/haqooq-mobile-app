import 'package:flutter_test/flutter_test.dart';
import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/contracts/services.dart';
import 'package:haqooq/services/ai/offline/offline_ai_service.dart';

class FakeRag implements RagService {
  final bool empty;
  FakeRag({this.empty = false});
  @override
  Future<List<LegalCard>> retrieve(String q, String lang, {int k = 3}) async => empty
      ? []
      : const [
          LegalCard(
              id: 'x', title: 'If you are arrested', category: 'police_arrest', lang: 'en',
              body: 'Stay calm.', steps: ['Ask reason', 'Contact lawyer'], sourceName: 'Pakistan Code',
              sourceUrl: 'https://pakistancode.gov.pk', lastReviewed: '2026-11-27', keywords: ['arrest'])
        ];
}

void main() {
  test('answer has disclaimer, source, offline mode', () async {
    final r = await OfflineAiService(FakeRag()).ask(const AskRequest(question: 'arrest', language: 'en'));
    expect(r.mode, AiMode.offline);
    expect(r.answer.contains('Stay calm'), true);
    expect(r.disclaimer.contains('not legal advice'), true);
    expect(r.sources.first.url, 'https://pakistancode.gov.pk');
    expect(r.shouldContactLawyer, true);
  });
  test('unknown -> not sure', () async {
    final r = await OfflineAiService(FakeRag(empty: true)).ask(const AskRequest(question: 'zzz', language: 'en'));
    expect(r.answer.contains('not sure'), true);
    expect(r.shouldContactLawyer, true);
  });
}
