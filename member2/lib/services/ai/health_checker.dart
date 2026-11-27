import 'dart:async';
import 'package:http/http.dart' as http;

/// Cheap reachability check against OUR backend (/health), cached briefly.
class HealthChecker {
  HealthChecker({
    required this.baseUrl,
    http.Client? client,
    this.cacheFor = const Duration(seconds: 15),
    this.timeout = const Duration(seconds: 3),
  }) : _client = client ?? http.Client();

  final String baseUrl;
  final Duration cacheFor;
  final Duration timeout;
  final http.Client _client;
  DateTime? _at;
  bool _last = false;

  Future<bool> isOnline({bool force = false}) async {
    if (baseUrl.isEmpty) return false;
    final now = DateTime.now();
    if (!force && _at != null && now.difference(_at!) < cacheFor) return _last;
    var base = baseUrl;
    while (base.endsWith('/')) {
      base = base.substring(0, base.length - 1);
    }
    try {
      final res = await _client.get(Uri.parse(base + '/health')).timeout(timeout);
      _last = res.statusCode == 200;
    } catch (_) {
      _last = false;
    }
    _at = now;
    return _last;
  }
}
