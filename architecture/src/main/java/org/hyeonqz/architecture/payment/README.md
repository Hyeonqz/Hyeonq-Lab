# 결제 시스템 — 아키텍처 3종 대비 (승인 · 취소 · 정산)

같은 도메인(결제)·같은 규칙을 레이어드 / 헥사고날 / 클린 세 스타일로 구현해 나란히 놓았다.
이론은 `architecture/docs`의 책(특히 Step 06~08, 13)에 있고, 여기는 그 코드 실물이다.

## 도메인 규칙 (세 스타일 공통)

- **승인(approve)**: 금액이 양수면 `APPROVED` 로 생성한다.
- **취소(cancel)**: `APPROVED` 상태에서만 `CANCELLED` 로. 종료 상태(취소·정산 완료)는 취소 불가.
- **정산(settle)**: `APPROVED` 상태에서만 `SETTLED` 로. 수수료 = 금액 × 3%.

## 패키지

| 스타일 | 패키지 | 규칙이 사는 곳 | 영속성 |
|---|---|---|---|
| **레이어드** | `payment.layered` | `PaymentService` (트랜잭션 스크립트) | 인메모리 (빈혈 엔티티 + 원시 `BigDecimal`) |
| **헥사고날** | `payment.hexagonal` | `domain.Payment` (리치 애그리거트) | **JPA + MySQL** (도메인↔엔티티 매핑 어댑터) |
| **클린** | `payment.clean` | `entity.Payment` (엔티티) | 인메모리 게이트웨이 (인터랙터 + 입출력 경계 + 응답 모델) |

## 화살표(의존 방향)로 보는 차이

- **레이어드**: `Service → Repository(구체) → Entity`. 정책이 세부사항의 이름을 안다. 규칙이 서비스에 흩어진다.
- **헥사고날**: `adapter → domain ← application`. 바깥(어댑터)이 안(도메인 포트)을 향한다. 규칙은 애그리거트 한 곳.
- **클린**: `interactor → gateway(추상) ← 구현`, `interactor → entity`. 의존성 규칙(안쪽만 향함). 경계로 요청/응답 모델이 오간다.

헥사고날과 클린은 사실상 같은 원리(의존성 역전을 시스템 경계에)를 다른 어휘로 부른다 — 포트/어댑터 vs 입출력 경계/인터랙터. (책 Step 08 참조)

## 헥사고날의 JPA (깊게)

`payment.hexagonal.adapter.persistence` 가 핵심이다:

- `PaymentJpaEntity` — **영속 모델**. 도메인 애그리거트와 **별개 클래스**다(테이블 구조가 도메인을 오염시키지 못한다).
- `PaymentJpaRepository` — Spring Data JPA.
- `PaymentPersistenceAdapter` — 도메인 포트 `PaymentRepository` 를 구현하고, **도메인 애그리거트 ↔ JPA 엔티티**를 양방향 매핑한다. 이 매핑이 헥사고날/DDD 의 실체다.

운영은 MySQL(`application.yml`), 테스트는 임베디드 H2 로 실제 저장·복원을 검증한다(`PaymentPersistenceAdapterTest`).

## 테스트

```bash
./gradlew :architecture:test
```

- `layered.PaymentServiceTest` — 서비스로 규칙 검증(저장소 동반)
- `hexagonal.PaymentTest` — 도메인 규칙(객체 하나, 밀리초)
- `hexagonal.PaymentServiceTest` — 유스케이스 + 인메모리 포트 목(어댑터 교체 가능성 시연)
- `hexagonal.adapter.persistence.PaymentPersistenceAdapterTest` — **JPA/H2 왕복**(매핑 보존 증명)
- `clean.PaymentInteractorTest` — 인터랙터 + 인메모리 게이트웨이

그리고 `ArchitectureRulesTest`(적합도 함수)가 헥사고날 의존성 규칙을 이 패키지에도 강제한다 —
`payment.hexagonal.domain` 은 바깥을, `payment.hexagonal.application` 은 어댑터를 import 하면 빌드가 깨진다.
