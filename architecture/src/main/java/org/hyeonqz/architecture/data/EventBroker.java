package org.hyeonqz.architecture.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 메시지 브로커의 흉내 (카프카 자리). 두 가지 현실을 시연한다:
 * 1) 발행은 실패할 수 있다 (failNext로 주입) — 이중 쓰기 문제의 원인.
 * 2) 전달은 "최소 한 번(at-least-once)"이다 (redeliver로 중복 주입) — 멱등성이 필요한 이유.
 */
public class EventBroker {

    private final List<OutboxEvent> published = new ArrayList<>();
    private Consumer<OutboxEvent> subscriber = e -> { };
    private boolean failNext = false;

    public void subscribe(Consumer<OutboxEvent> subscriber) {
        this.subscriber = subscriber;
    }

    /** 다음 publish 한 번을 실패시킨다 (프로세스 크래시/네트워크 단절 시뮬레이션). */
    public void failNextPublish() {
        this.failNext = true;
    }

    public void publish(OutboxEvent event) {
        if (failNext) {
            failNext = false;
            throw new RuntimeException("broker unreachable"); // 발행 실패 — 부분 실패(Step 10)
        }
        published.add(event);
        subscriber.accept(event); // 구독자에게 전달
    }

    /** 같은 이벤트를 한 번 더 전달한다 — at-least-once의 중복. */
    public void redeliver(OutboxEvent event) {
        subscriber.accept(event);
    }

    public int publishedCount() {
        return published.size();
    }
}
