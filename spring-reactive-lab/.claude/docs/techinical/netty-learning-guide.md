# 순수 Netty 학습 가이드

> 목표: 고가용성 서버 개발, SSE(Server-Sent Events), 순수 Netty 기반 프로젝트 이해

---

## 전제 지식 (선행 학습)

Netty는 Java NIO를 추상화한 프레임워크입니다. 아래 개념을 먼저 이해해야 합니다.

| 선행 지식 | 설명 |
|----------|------|
| Java NIO | Channel, Buffer, Selector — Netty가 이를 추상화 |
| TCP/IP 소켓 기초 | 연결, 데이터 송수신 흐름 |
| 멀티스레딩 / 동시성 | `synchronized`, `volatile`, `ExecutorService` |
| 이벤트 루프 개념 | 단일 스레드로 다수 이벤트 처리하는 패턴 |

---

## 단계별 학습 로드맵

### 1단계: Java NIO 기초 (1~2주)

Netty가 내부적으로 처리하는 것들을 먼저 이해합니다.

```java
// Netty가 추상화하는 NIO 코드
ServerSocketChannel channel = ServerSocketChannel.open();
Selector selector = Selector.open();
channel.register(selector, SelectionKey.OP_ACCEPT);
// → Netty의 EventLoop가 이 역할을 담당
```

| 개념 | 설명 |
|------|------|
| `Channel` | 데이터 읽기/쓰기 통로 |
| `Buffer` | 데이터 컨테이너 (`ByteBuffer`) |
| `Selector` | 여러 Channel을 하나의 스레드로 감시 |
| blocking vs non-blocking | Netty의 존재 이유 |

---

### 2단계: Netty 핵심 구조 (2~3주)

```
EventLoopGroup (BossGroup)     → 연결 수락 (accept)
EventLoopGroup (WorkerGroup)   → 데이터 읽기/쓰기 처리
    └── EventLoop              → 하나의 스레드, 여러 Channel 담당
         └── Channel
              └── ChannelPipeline
                   ├── ChannelHandler 1 (decode)
                   ├── ChannelHandler 2 (business logic)
                   └── ChannelHandler 3 (encode)
```

**핵심 컴포넌트 학습 순서:**

```java
// 1. ServerBootstrap — 서버 설정 진입점
ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .childHandler(new ChannelInitializer<>() {
             protected void initChannel(Channel ch) {
                 ch.pipeline().addLast(new MyHandler());
             }
         });

// 2. ChannelHandler — 비즈니스 로직
public class MyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 데이터 처리
    }
}

// 3. ChannelFuture — 비동기 결과 처리
ChannelFuture future = channel.writeAndFlush(msg);
future.addListener(f -> {
    if (f.isSuccess()) { ... }
});
```

---

### 3단계: Pipeline & Handler 심화 (2주)

```
Inbound  →  [Decoder] → [BusinessLogic] → [Encoder]  → Outbound
         ←                                            ←
```

| Handler 종류 | 역할 |
|-------------|------|
| `ByteToMessageDecoder` | byte → 객체 변환 (수신) |
| `MessageToByteEncoder` | 객체 → byte 변환 (송신) |
| `SimpleChannelInboundHandler` | 타입 안전한 inbound 처리 |
| `ChannelDuplexHandler` | inbound + outbound 동시 처리 |

**연결 끊김 / 예외 처리:**

```java
@Override
public void channelInactive(ChannelHandlerContext ctx) {
    // 연결 끊어짐 처리
}

@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
}
```

---

### 4단계: 고가용성 핵심 설정 (2~3주)

```
고가용성을 위한 Netty 설정
├── EventLoop 스레드 수 튜닝
├── ChannelOption 설정 (SO_KEEPALIVE, TCP_NODELAY 등)
├── Idle 감지 (HeartBeat 구현)
├── 메모리 관리 (ByteBuf, ReferenceCounting)
└── Backpressure 처리
```

```java
// EventLoop 스레드 수 튜닝
EventLoopGroup workerGroup = new NioEventLoopGroup(
    Runtime.getRuntime().availableProcessors() * 2
);

// 연결 옵션 설정
bootstrap
    .option(ChannelOption.SO_BACKLOG, 1024)
    .childOption(ChannelOption.SO_KEEPALIVE, true)
    .childOption(ChannelOption.TCP_NODELAY, true);

// IdleStateHandler — HeartBeat 구현
pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
pipeline.addLast(new HeartBeatHandler());
```

**HeartBeat 핸들러 예시:**

```java
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close(); // 일정 시간 수신 없으면 연결 종료
            }
        }
    }
}
```

---

### 5단계: HTTP + SSE 구현 (1~2주)

순수 Netty로 HTTP + SSE를 직접 구현합니다.

```java
// Pipeline에 HTTP 코덱 추가
pipeline.addLast(new HttpServerCodec());
pipeline.addLast(new HttpObjectAggregator(65536));
pipeline.addLast(new ChunkedWriteHandler());
pipeline.addLast(new SseHandler()); // 직접 구현

// SSE 응답 헤더 설정
HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
response.headers()
        .set(CONTENT_TYPE, "text/event-stream")
        .set(TRANSFER_ENCODING, "chunked")
        .set(CACHE_CONTROL, "no-cache");
ctx.writeAndFlush(response);

// SSE 이벤트 전송
String sseData = "data: " + payload + "\n\n";
ctx.writeAndFlush(new DefaultHttpContent(
    Unpooled.copiedBuffer(sseData, CharsetUtil.UTF_8)
));
```

---

## 전체 학습 타임라인

| 기간 | 학습 내용 |
|------|----------|
| 1~2주 | Java NIO 기초 (Channel, Buffer, Selector) |
| 3~5주 | Netty 구조 (Bootstrap, EventLoop, Pipeline, Handler) |
| 6~7주 | Handler 심화 + ByteBuf 메모리 관리 |
| 8~9주 | 고가용성 설정 (IdleHandler, ChannelOption 튜닝) |
| 10~11주 | HTTP / SSE 직접 구현 |

---

## Spring WebFlux와의 관계

```
Spring WebFlux
    └── Project Reactor   ← reactive streams 구현체
         └── Netty        ← 내장 서버로 자동 사용
```

- WebFlux는 Netty를 추상화하여 사용
- 회사 프로젝트처럼 **순수 Netty**를 사용하는 경우 WebFlux 없이 직접 제어
- WebFlux 공부는 Netty를 익힌 후 역방향으로 보면 이해가 빠름

---

## 학습 리소스

| 리소스 | 설명 |
|--------|------|
| **Netty in Action** (Manning) | 가장 체계적인 Netty 교재 |
| `netty/netty` GitHub 공식 예제 | 실전 코드 참고 |
| Norman Maurer 블로그 | Netty 핵심 개발자 기술 블로그 |
| Java NIO and NIO.2 (책) | NIO 선행 학습용 |
