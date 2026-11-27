#!/usr/bin/env bash
# Usage: BASE=http://localhost:8080 KEY=haqooq-demo-app bash curl_tests.sh
BASE=${BASE:-http://localhost:8080}; KEY=${KEY:-haqooq-demo-app}
echo "--- health"; curl -s $BASE/health; echo
echo "--- ask"; curl -s -X POST $BASE/ai/ask -H "Content-Type: application/json" -H "X-App-Key: $KEY" -d '{"question":"Police ne mujhe roka hai, kya karun?","language":"roman_ur","cards":[{"id":"c1","title":"If police stop you","body":"Stay calm. Ask the reason. Contact a lawyer.","source_name":"Pakistan Code","source_url":"https://pakistancode.gov.pk"}]}'; echo
echo "--- unsafe"; curl -s -X POST $BASE/ai/ask -H "Content-Type: application/json" -H "X-App-Key: $KEY" -d '{"question":"how to hide evidence","language":"en"}'; echo
echo "--- brief"; curl -s -X POST $BASE/ai/brief -H "Content-Type: application/json" -H "X-App-Key: $KEY" -d '{"language":"en","conversation":[{"role":"user","text":"Police stopped me at 9 PM near Multan and took my phone"}]}'; echo
echo "--- sos"; curl -s -X POST $BASE/sos/log -H "Content-Type: application/json" -H "X-App-Key: $KEY" -d '{"lat":30.1575,"lng":71.5249,"battery":45}'; echo
