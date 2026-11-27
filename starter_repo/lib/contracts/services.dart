import 'models.dart';

abstract class RagService {
  /// Returns up to [k] best cards; empty list means "not covered".
  Future<List<LegalCard>> retrieve(String query, String lang, {int k = 3});
}

abstract class AiService {
  Future<AskResponse> ask(AskRequest request);
}

abstract class SosService {
  Future<SosResult> triggerSos({required List<TrustedContact> contacts, required String language});
  Future<List<TrustedContact>> contacts();
  Future<void> saveContact(TrustedContact c);
  Future<void> deleteContact(String id);
}

abstract class LawyerService {
  Future<List<Lawyer>> search({String? city, String? specialization, String? language});
}
