/// flutter run --dart-define=BACKEND_BASE_URL=https://your-backend --dart-define=APP_PUBLIC_KEY=haqooq-demo-app
class AiConfig {
  static const String backendBaseUrl = String.fromEnvironment('BACKEND_BASE_URL', defaultValue: '');
  static const String appPublicKey = String.fromEnvironment('APP_PUBLIC_KEY', defaultValue: '');
  static bool get isConfigured => backendBaseUrl.isNotEmpty;
}
