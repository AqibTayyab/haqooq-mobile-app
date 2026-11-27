class Tokenizer {
  static const _stop = <String>{
    'the', 'a', 'an', 'is', 'are', 'was', 'were', 'i', 'me', 'my', 'you', 'your', 'he', 'she', 'it',
    'to', 'of', 'in', 'on', 'at', 'for', 'and', 'or', 'if', 'do', 'does', 'did', 'can', 'what', 'how',
    'why', 'kya', 'kaise', 'kyun', 'mera', 'meri', 'mujhe', 'mein', 'hai', 'hain', 'tha', 'thi',
    'kar', 'karein', 'karo', 'se', 'ko', 'ka', 'ki', 'ne', 'ho'
  };
  static const _syn = <String, String>{
    'giraftar': 'arrest', 'giraftari': 'arrest', 'hirasat': 'custody', 'thana': 'police',
    'roka': 'stop', 'rokay': 'stop', 'dhamki': 'threat', 'tang': 'harassment',
    'zamanat': 'bail', 'naukri': 'job', 'shikayat': 'complaint', 'kharab': 'faulty',
  };

  List<String> tokenize(String text) {
    final cleaned = text.toLowerCase().replaceAll(RegExp(r'[^\w\s]'), ' ');
    return cleaned
        .split(RegExp(r'\s+'))
        .where((t) => t.isNotEmpty && !_stop.contains(t))
        .map((t) => _syn[t] ?? t)
        .toList();
  }
}
