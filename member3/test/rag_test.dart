import 'package:flutter_test/flutter_test.dart';
import 'package:haqooq/services/rag/intent_classifier.dart';
import 'package:haqooq/services/rag/tokenizer.dart';

void main() {
  final c = IntentClassifier();
  final t = Tokenizer();

  test('police stop', () => expect(c.classify('Police ne mujhe roka hai').category, 'police_arrest'));
  test('arrest is emergency', () {
    final r = c.classify('Police mujhe arrest kar rahi hai');
    expect(r.category, 'police_arrest');
    expect(r.isEmergency, true);
  });
  test('fraud', () => expect(c.classify('Online fraud ho gaya').category, 'cybercrime'));
  test('salary', () => expect(c.classify('meri salary nahi mili').category, 'employment'));
  test('unknown', () => expect(c.classify('hello world').category, 'general'));
  test('synonym', () => expect(t.tokenize('mujhe giraftar kiya'), contains('arrest')));
  test('stopword removed', () => expect(t.tokenize('kya mujhe'), isEmpty));
}
