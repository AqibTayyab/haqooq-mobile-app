import 'dart:convert';
import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/services/ai/online_ai_service.dart';

void main() {
  const req = AskRequest(question: 'police ne roka', language: 'roman_ur');

  test('parses a good backend response', () async {
    final client = MockClient((r) async {
      expect(r.url.path, '/ai/ask');
      expect(r.headers['X-App-Key'], 'k');
      return http.Response(
        jsonEncode({
          'answer': 'Stay calm', 'legal_category': 'police_arrest', 'urgency': 'high',
          'sources': [{'title': 'T', 'url': 'u'}], 'next_steps': ['a'], 'follow_up_questions': [],
          'should_contact_lawyer': true, 'disclaimer': 'd'
        }),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      );
    });
    final s = OnlineAiService(baseUrl: 'https://x.test/', appKey: 'k', client: client);
    final r = await s.ask(req);
    expect(r.urgency, 'high');
    expect(r.mode, AiMode.online);
  });

  test('throws AiUnavailableException on 502', () async {
    final client = MockClient((_) async => http.Response('{}', 502));
    final s = OnlineAiService(baseUrl: 'https://x.test', client: client);
    expect(() => s.ask(req), throwsA(isA<AiUnavailableException>()));
  });

  test('throws on network error', () async {
    final client = MockClient((_) async => throw Exception('offline'));
    final s = OnlineAiService(baseUrl: 'https://x.test', client: client);
    expect(() => s.ask(req), throwsA(isA<AiUnavailableException>()));
  });
}
