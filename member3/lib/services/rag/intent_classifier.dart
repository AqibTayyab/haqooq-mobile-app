class IntentResult {
  final String category;
  final bool isEmergency;
  const IntentResult(this.category, this.isEmergency);
}

class IntentClassifier {
  static const _emergencyWords = [
    'arresting', 'detained', 'custody', 'giraftar', 'giraftari', 'maar raha', 'maar rahi',
    'bachao', 'help me', 'kidnap', 'threatened', 'dhamki', 'khatra', 'danger', 'arrest kar'
  ];

  static const _map = <String, List<String>>{
    'police_arrest': ['police', 'stop', 'roka', 'thana', 'arrest', 'giraftar', 'custody', 'detain'],
    'fir': ['fir', 'complaint', 'shikayat', 'police station'],
    'bail_court': ['bail', 'zamanat', 'court', 'hearing'],
    'women_safety': ['harassment', 'eve teasing', 'street', 'unsafe', 'tang'],
    'domestic': ['domestic', 'husband', 'ghar', 'abuse', 'violence'],
    'cybercrime': ['fraud', 'scam', 'online', 'hack', 'cyber', 'blackmail'],
    'consumer': ['consumer', 'product', 'refund', 'receipt', 'faulty', 'warranty'],
    'employment': ['salary', 'unpaid', 'job', 'termination', 'employer', 'naukri'],
  };

  IntentResult classify(String query) {
    final q = query.toLowerCase();
    final emergency = _emergencyWords.any(q.contains);
    for (final e in _map.entries) {
      if (e.value.any(q.contains)) return IntentResult(e.key, emergency);
    }
    return IntentResult('general', emergency);
  }
}
