# Claude Code Configuration - Backend Service

## Project Context

## Development Workflow

### Phase 1: Planning (필수)
모든 작업 요청은 **즉시 실행하지 않고** 다음 절차를 따릅니다:

1. 요청사항 분석 후 `.claude/plans/YYYYMMDD_HHmmss_[task-name].md` 생성
2. 실행 계획 내용:
    - 작업 목표 및 범위
    - 영향받는 컴포넌트 목록
    - 구현 단계별 상세 계획
    - 예상 리스크 및 대응 방안
    - 테스트 전략
3. 계획 검토 후 승인 시에만 Phase 2 진행

### Phase 2: Implementation
승인된 계획에 따라 구현을 진행합니다.

### Phase 3: Documentation (필수)
코드 변경사항 발생 시 `.claude/docs/changes/YYYYMMDD_[task-name].md` 생성:
```markdown
# Change Log: [Task Name]

## As-Is (변경 전)
- 현재 구조 및 로직 설명
- 문제점 또는 개선 필요 사항
- 코드 스니펫 (핵심 부분)

## To-Be (변경 후)
- 변경된 구조 및 로직 설명
- 개선 효과
- 코드 스니펫 (핵심 부분)

## Impact Analysis
- 영향받는 API 엔드포인트
- 데이터베이스 스키마 변경 여부
- 호환성 이슈
- 롤백 계획

## Testing
- 단위 테스트 추가/수정 내역
- 통합 테스트 시나리오
```

## Code Standards

### Kotlin Conventions
- 패키지 구조: `com.company.investment.{domain}.{layer}`
- DTO 네이밍: `{Entity}Request/Response/Dto`
- Service 메서드: 동사 + 명사 (e.g., `createInvestmentOrder`)

### Database
- JPA Entity: 불변성 고려한 설계
- QueryDSL 적극 활용
- 인덱스 전략 명시

### API Design
- RESTful 원칙 준수
- 응답 형식 표준화 (`ApiResponse<T>`)
- 에러 코드 체계 일관성

## Skills & Agents

### Available Skills
- `/skills/spring-boot-best-practices.md`: Spring Boot 권장사항
- `/skills/jpa-optimization.md`: JPA 성능 최적화
- `/skills/api-design.md`: API 설계 가이드

### Agents
- `/agents/code-reviewer.md`: 코드 리뷰 자동화
- `/agents/test-generator.md`: 테스트 코드 생성

## Commands
- `/commands/create-entity.md`: Entity 생성 템플릿
- `/commands/create-service.md`: Service 레이어 생성
- `/commands/create-api.md`: REST API 엔드포인트 생성

## Important Notes
- 금융 도메인 특성상 트랜잭션 무결성 최우선
- 보안 취약점 검토 필수 (SQL Injection, XSS 등)
- 로깅 전략: 민감정보 마스킹 필수