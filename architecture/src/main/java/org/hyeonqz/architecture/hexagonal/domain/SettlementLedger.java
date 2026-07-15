package org.hyeonqz.architecture.hexagonal.domain;

/**
 * 피주도(driven) 포트 — "정산을 기록한다"는 의도의 이름.
 * 콘솔이든, DB 원장이든, 카프카든 — 그것은 어댑터의 사정이다.
 */
public interface SettlementLedger {

    void record(Settlement settlement);
}
