import 'package:flutter_test/flutter_test.dart';
import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/contracts/services.dart';
import 'package:haqooq/services/ai/ai_router.dart';
import 'package:haqooq/services/ai/health_checker.dart';

class _Rag implements RagService {
  @override
  Future<List<LegalCard>> retrieve(String q, String l, {int k = 3}) async => const [];
}

class _Health extends HealthChecker {
  _Health(this.up) : super(baseUrl: 'https://x.test');
  final bool up;
  @override
  Future<bool> isOnline({bool force = false}) async => up;
}

class _Ai implements AiService {
  _Ai(this.fail, this.mode);
  final bool fail;
  final AiMode mode;
  @override
  Future<AskResponse> ask(AskRequest r) async {
    if (fail) throw Exception('boom');
    return AskResponse(
        answer: 'ok', legalCategory: 'g', urgency: 'low', sources: const [],
        nextSteps: const [], followUpQuestions: const [], shouldContactLawyer: false,
        disclaimer: 'd', mode: mode);
  }
}

const _req = AskRequest(question: 'q', language: 'en');

void main() {
  test('uses online when healthy', () async {
    final r = AiRouter(online: _Ai(false, AiMode.online), offline: _Ai(false, AiMode.offline), rag: _Rag(), health: _Health(true));
    expect((await r.ask(_req)).mode, AiMode.online);
    expect(r.mode, AiMode.online);
  });

  test('falls back to offline when backend down', () async {
    final r = AiRouter(online: _Ai(false, AiMode.online), offline: _Ai(false, AiMode.offline), rag: _Rag(), health: _Health(false));
    expect((await r.ask(_req)).mode, AiMode.offline);
    expect(r.mode, AiMode.offline);
  });

  test('falls back to offline when online throws', () async {
    final r = AiRouter(online: _Ai(true, AiMode.online), offline: _Ai(false, AiMode.offline), rag: _Rag(), health: _Health(true));
    expect((await r.ask(_req)).mode, AiMode.offline);
  });

  test('library only when everything fails', () async {
    final r = AiRouter(online: _Ai(true, AiMode.online), offline: _Ai(true, AiMode.offline), rag: _Rag(), health: _Health(false));
    final res = await r.ask(_req);
    expect(res.mode, AiMode.libraryOnly);
    expect(res.shouldContactLawyer, true);
    expect(r.mode, AiMode.libraryOnly);
  });
}
