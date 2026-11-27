import 'dart:convert';
import 'package:flutter/services.dart' show rootBundle;
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:haqooq/contracts/models.dart';

class DatabaseService {
  static const _dbName = 'haqooq_cards.db';
  static const _version = 1;
  Database? _db;
  List<LegalCard>? _cache;

  Future<Database> get _database async {
    if (_db != null) return _db!;
    final dir = await getDatabasesPath();
    _db = await openDatabase(join(dir, _dbName), version: _version, onCreate: (db, v) async {
      await db.execute(
          'CREATE TABLE legal_cards(id TEXT PRIMARY KEY, title TEXT, category TEXT, lang TEXT, '
          'body TEXT, steps TEXT, source_name TEXT, source_url TEXT, last_reviewed TEXT, keywords TEXT)');
      await _seed(db);
    });
    return _db!;
  }

  Future<void> _seed(Database db) async {
    for (final asset in const [
      'assets/legal_data/cards_en.json',
      'assets/legal_data/cards_ru.json',
    ]) {
      final list = jsonDecode(await rootBundle.loadString(asset)) as List;
      for (final j in list) {
        final c = LegalCard.fromJson(j as Map<String, dynamic>);
        await db.insert(
            'legal_cards',
            {
              'id': c.id, 'title': c.title, 'category': c.category, 'lang': c.lang, 'body': c.body,
              'steps': jsonEncode(c.steps), 'source_name': c.sourceName, 'source_url': c.sourceUrl,
              'last_reviewed': c.lastReviewed, 'keywords': jsonEncode(c.keywords),
            },
            conflictAlgorithm: ConflictAlgorithm.replace);
      }
    }
  }

  Future<List<LegalCard>> allCards() async {
    if (_cache != null) return _cache!;
    final db = await _database;
    final rows = await db.query('legal_cards');
    _cache = rows
        .map((r) => LegalCard(
              id: r['id'] as String, title: r['title'] as String, category: r['category'] as String,
              lang: r['lang'] as String, body: r['body'] as String,
              steps: List<String>.from(jsonDecode(r['steps'] as String) as List),
              sourceName: (r['source_name'] ?? '') as String, sourceUrl: (r['source_url'] ?? '') as String,
              lastReviewed: (r['last_reviewed'] ?? '') as String,
              keywords: List<String>.from(jsonDecode(r['keywords'] as String) as List),
            ))
        .toList();
    return _cache!;
  }
}
