#!/usr/bin/env bash
# Run from the repo root. Needs the Flutter SDK.
set -e
OUT=app
rm -rf "$OUT"
flutter create "$OUT" --org com.haqooq --project-name haqooq
rm -f "$OUT/test/widget_test.dart"

overlay () {   # overlay <source_dir> <label>
  local src="$1" label="$2"
  [ -d "$src" ] || return 0
  echo "Overlaying $label from $src"
  for sub in lib assets test backend; do
    if [ -d "$src/$sub" ]; then
      mkdir -p "$OUT/$sub"
      (cd "$src/$sub" && find . -type f) | while read -r f; do
        if [ -e "$OUT/$sub/$f" ] && [ "$label" != "starter" ]; then echo "  WARNING overwriting $sub/$f"; fi
      done
      cp -r "$src/$sub/." "$OUT/$sub/"
    fi
  done
  if [ "$label" = "starter" ]; then
    for f in pubspec.yaml analysis_options.yaml .gitignore .env.example; do
      [ -f "$src/$f" ] && cp "$src/$f" "$OUT/$f"
    done
  fi
}
overlay starter_repo starter
for m in member1 member2 member3 member4; do overlay "$m" "$m"; done
echo "Done. Next: cd $OUT && flutter pub get && flutter analyze && flutter test"
