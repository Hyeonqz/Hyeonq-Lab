# Kafka

## 1. Kafka Batch 처리 동작 원리
```markdown
1. poll() 호출
2. 브로커에서 fetch.min.bytes 만큼 데이터가 쌓이거나
3. fetch.max.wait.ms 시간이 지나면
4. 최대 max.poll.records 개수만큼 메시지를 가져옴
5. @KafkaListener 메소드 호출

```

### 2. Kafka 메시지 유실 방지
1. Redis 를 사용하여 메시지를 관리한다?
2. Application 이 종료되기 전 @PreDestroy 를 사용하여 메시지를 모두 소비시킨다
3. dead message 를 다른 토픽에서 관리한다?


### 3. Kafka Consumer Pool 설정?
@KafkaListener 를 사용하지 않고 직접 Kafka Consumer 를 사용한다면? <br>
위 설정이 유용하게 사용될 것 이다 <br>

이유는 @KafkaListener 는 자동으로 poll 을 통해 메시지를 관리하지만 위 어노테이션을 사용하지 않는다면 <br>
consumer 는 자동으로 메시지를 가져와 처리하지 않을 것이다 <br>

그리고 매번 수동으로 consumer 를 생성하고 로직을 처리하기에 특정시간에 요청이 몰리는 경우라면 위험하다 <br>

메시지를 가져오고 처리하는데 필요한 네트워크 오버헤드와 CPU 사용량이 늘어난다. <br>
특히, 각 메시지에 대해 별도의 트랜잭션을 시작하고 커밋하는 것은 비용이 많이 든다. <br>
DB와의 네트워크 통신, 트랜잭션 로그의 쓰기, 디스크 I/O 등에 의한 오버헤드가 각 메시지마다 발생하므로 메시지 처리 성능이 저하될 수 있다. <br>
뿐 만 아니라 각 메시지 처리에 대해 별도 DB Connection Pool이 고갈되어, 새로운 트랜잭션을 생성할 수 없게될 수도 있다 <br>






