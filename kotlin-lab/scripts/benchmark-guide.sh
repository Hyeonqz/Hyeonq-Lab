#!/bin/bash
# ============================================================
#  벤치마크 실행 가이드
# ============================================================

# 스크립트 위치 기준으로 프로젝트 루트를 자동 계산
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "📁 프로젝트 루트: $PROJECT_ROOT"
cd "$PROJECT_ROOT" || { echo "❌ 프로젝트 루트 이동 실패"; exit 1; }

# ── STEP 1: OSIV=true 로 실행 ────────────────────────────────
# application.yml → open-in-view: true 확인 후 실행
echo ""
echo "▶ STEP 1: OSIV=true 테스트 실행 중..."
./gradlew test --tests "*OsivBenchmarkTest" \
    2>&1 | tee "$SCRIPT_DIR/result-osiv-true.log"

# ── STEP 2: OSIV=false 로 실행 ───────────────────────────────
# application.yml → open-in-view: false 로 수정 후 실행
echo ""
echo "▶ STEP 2: OSIV=false 테스트 실행 중..."
./gradlew test --tests "*OsivBenchmarkTest" \
    2>&1 | tee "$SCRIPT_DIR/result-osiv-false.log"

# ── STEP 3: 결과 비교 ─────────────────────────────────────────
echo ""
echo "▶ STEP 3: 결과 비교"
diff "$SCRIPT_DIR/result-osiv-true.log" "$SCRIPT_DIR/result-osiv-false.log"

echo ""
echo "✅ 벤치마크 완료. 결과 파일:"
echo "   $SCRIPT_DIR/result-osiv-true.log"
echo "   $SCRIPT_DIR/result-osiv-false.log"