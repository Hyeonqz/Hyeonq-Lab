package org.hyeonqz.architecture.hexagonal.adapter;

import org.hyeonqz.architecture.hexagonal.domain.Settlement;
import org.hyeonqz.architecture.hexagonal.domain.SettlementLedger;

/**
 * 피주도 어댑터 — "정산 채널 추가"라는 변경 시나리오의 주인공.
 * 카프카 발행으로 바꾸려면? KafkaLedger를 옆에 추가한다. 이 diff는 여기서 끝난다.
 */
public class ConsoleLedger implements SettlementLedger {

    @Override
    public void record(Settlement settlement) {
        System.out.println("[원장] " + settlement);
    }
}
