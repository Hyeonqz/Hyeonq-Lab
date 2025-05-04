## netty

## 기본 동작




### 개념 정리
#### @Sharable
Netty 에서 `@ChannelHandler.Sharable`은 해당 핸들러 인스턴스를 여러 채널(ChannelPipeline) 간에 안전하게 공유할 수 있음을 나타냅니다. <br>
즉, 이 어노테이션이 붙은 클래스는 스레드 세이프(Thread-safe) 해야 하며, 싱글톤 인스턴스로 다수의 클라이언트와 공유될 수 있는 Handler를 의미합니다.<br>

Netty 는 각 클라이언트 접속마다 새로운 `ChannelPipeline` 을 생성하며, 일반적으로는 그 안에는 새로운 ChannelHandler 인스턴스가 등록된다 <br>
@Sharable 이 선언된 핸들러는 다수의 `ChannelPipeline` 에 동일한 인스턴슬들 등록할 수 있다 <br>

