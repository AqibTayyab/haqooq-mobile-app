import 'models.dart';
import 'services.dart';

class MockRagService implements RagService {
  @override
  Future<List<LegalCard>> retrieve(String query, String lang, {int k = 3}) async => [];
}

class MockAiService implements AiService {
  @override
  Future<AskResponse> ask(AskRequest request) async => const AskResponse(
    answer: 'Mock answer: stay calm, ask the reason, contact a lawyer or family member.',
    legalCategory: 'police_arrest', urgency: 'medium', sources: [SourceRef('Mock source', 'https://pakistancode.gov.pk')],
    nextSteps: ['Stay calm', 'Ask the reason', 'Contact a lawyer'], followUpQuestions: [],
    shouldContactLawyer: true, disclaimer: 'General legal information, not legal advice. Consult a licensed advocate.',
    mode: AiMode.libraryOnly);
}

class MockSosService implements SosService {
  final List<TrustedContact> _c = [];
  @override Future<List<TrustedContact>> contacts() async => List.of(_c);
  @override Future<void> saveContact(TrustedContact c) async => _c.add(c);
  @override Future<void> deleteContact(String id) async => _c.removeWhere((e) => e.id == id);
  @override Future<SosResult> triggerSos({required List<TrustedContact> contacts, required String language}) async =>
    const SosResult(false, 'I need help. (mock)');
}

class MockLawyerService implements LawyerService {
  @override
  Future<List<Lawyer>> search({String? city, String? specialization, String? language}) async => const [
    Lawyer(id: 'demo_1', name: 'Demo Lawyer (Not a Real Lawyer)', city: 'Multan',
      bio: 'Demo Account - Not a Real Lawyer.', specializations: ['criminal'], languages: ['ur', 'en'],
      rating: 4.8, fee: 0, isVerified: false, isDemo: true)];
}
