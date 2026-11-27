import 'package:flutter_test/flutter_test.dart';
import 'package:haqooq/contracts/models.dart';

void main() {
  test('AskResponse parses', () {
    final r = AskResponse.fromJson({
      'answer': 'ok', 'legal_category': 'police_arrest', 'urgency': 'high',
      'sources': [], 'next_steps': ['a'], 'follow_up_questions': [],
      'should_contact_lawyer': true, 'disclaimer': 'd'
    });
    expect(r.urgency, 'high');
  });
}
