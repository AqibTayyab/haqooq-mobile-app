import 'dart:async';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:haqooq/contracts/models.dart';
import 'package:haqooq/contracts/services.dart';

class AiUnavailableException implements Exception {
  final String message;
  final int? statusCode;
  AiUnavailableException(this.message, {this.statusCode});
  @override
  String toString() => 'AiUnavailableException($message, $statusCode)';
}

/// Calls OUR backend (never Gemini directly). No API key lives in the app.
class OnlineAiService implements AiService {
  OnlineAiService({
    required this.baseUrl,
    this.appKey = '',
    http.Client? client,
    this.timeout = const Duration(seconds: 12),
  }) : _client = client ?? http.Client();

  final String baseUrl;
  final String appKey;
  final Duration timeout;
  final http.Client _client;

  Map<String, String> get _headers => {
        'Content-Type': 'application/json; charset=utf-8',
        if (appKey.isNotEmpty) 'X-App-Key': appKey,
      };

  Uri _uri(String path) {
    var base = baseUrl;
    while (base.endsWith('/')) {
      base = base.substring(0, base.length - 1);
    }
    return Uri.parse(base + path);
  }

  @override
  Future<AskResponse> ask(AskRequest request) async {
    final json = await _post('/ai/ask', request.toJson());
    return AskResponse.fromJson(json, mode: AiMode.online);
  }

  /// Lawyer Briefing Pack JSON (Member 4 shows/shares it).
  Future<Map<String, dynamic>> brief({
    required String language,
    required List<Map<String, String>> conversation,
    String notes = '',
  }) =>
      _post('/ai/brief', {'language': language, 'conversation': conversation, 'notes': notes});

  Future<Map<String, dynamic>> _post(String path, Map<String, dynamic> body) async {
    try {
      final res = await _client
          .post(_uri(path), headers: _headers, body: jsonEncode(body))
          .timeout(timeout);
      if (res.statusCode != 200) {
        throw AiUnavailableException('Backend returned ' + res.statusCode.toString(), statusCode: res.statusCode);
      }
      final decoded = jsonDecode(utf8.decode(res.bodyBytes));
      if (decoded is! Map<String, dynamic>) {
        throw AiUnavailableException('Unexpected response shape');
      }
      return decoded;
    } on TimeoutException {
      throw AiUnavailableException('Timeout');
    } on AiUnavailableException {
      rethrow;
    } catch (e) {
      throw AiUnavailableException('Network or parsing error: ' + e.toString());
    }
  }
}
