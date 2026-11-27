import 'package:flutter/material.dart';
import 'contracts/mocks.dart';
import 'contracts/models.dart';

class HaqooqApp extends StatelessWidget {
  const HaqooqApp({super.key});
  @override
  Widget build(BuildContext context) => MaterialApp(
        title: 'HAQOOQ',
        theme: ThemeData(useMaterial3: true, colorSchemeSeed: const Color(0xFF0B4F6C)),
        home: const _Shell(),
      );
}

class _Shell extends StatefulWidget {
  const _Shell();
  @override
  State<_Shell> createState() => _ShellState();
}

class _ShellState extends State<_Shell> {
  int _i = 0;
  String _out = 'Tap Ask to test the mock AI service';
  Future<void> _ask() async {
    final r = await MockAiService().ask(const AskRequest(question: 'test', language: 'en'));
    setState(() => _out = '${r.answer}\n${r.disclaimer}');
  }

  @override
  Widget build(BuildContext context) {
    final tabs = <Widget>[
      const Center(child: Text('Home (Member 1)')),
      Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
        Padding(padding: const EdgeInsets.all(16), child: Text(_out)),
        FilledButton(onPressed: _ask, child: const Text('Ask')),
      ])),
      const Center(child: Text('Rights Library (Member 3 data)')),
      const Center(child: Text('SOS (Member 4)')),
      const Center(child: Text('Lawyers (Member 4)')),
    ];
    return Scaffold(
      appBar: AppBar(title: const Text('HAQOOQ')),
      body: tabs[_i],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _i,
        onDestinationSelected: (v) => setState(() => _i = v),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.home), label: 'Home'),
          NavigationDestination(icon: Icon(Icons.chat), label: 'AI'),
          NavigationDestination(icon: Icon(Icons.menu_book), label: 'Rights'),
          NavigationDestination(icon: Icon(Icons.sos), label: 'SOS'),
          NavigationDestination(icon: Icon(Icons.gavel), label: 'Lawyers'),
        ],
      ),
    );
  }
}
