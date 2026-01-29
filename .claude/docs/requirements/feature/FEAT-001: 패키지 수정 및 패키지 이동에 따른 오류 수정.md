# FEAT-001

## 요구사항
### 1. 패키지 이동에 따른 실행 안돼는 오류 수정
- invest-api, invest-batch

기존에는 package io.github.Hyeonqz 외부에 
```java
package io.github.Hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.github.Hyeonqz"])
class InvestApiApplication

fun main(args: Array<String>) {
    runApplication<InvestApiApplication>(*args)
}

```

위 코드였다가

```java
package io.github.Hyeonqz.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.github.Hyeonqz"])
class InvestApiApplication

fun main(args: Array<String>) {
    runApplication<InvestApiApplication>(*args)
}

```

위 처럼 코드를 바꿧더니 실행이 안돼


위 문제를 해결해줄래?

invest-api, invest-batch 둘다 포함이야

나는 기본 패키지 경로안에서 패키지를 하나 더 만들어서 그 안에서 클래스를 관리하고 싶어



### 2. 전체 프로젝트 패키지 네이밍 변경
package io.github.Hyeonqz.api.config 위 같은 경로에서 <br>

Package name 'io.github.Hyeonqz.api.config' part should not start with an uppercase letter
위 메시지가 나오고 있어.

io.github.Hyeonqz -> io.github.hyeonqz 위 경로로 바꿔서 warn 이 안나오게할 수 있을까?
이건 전체 프로젝트를 대상으로 할꺼야 <br>

실제 내 github 는 Hyeonqz 이것이지만, kotlin 에서는 대문자 패캐지 경로가 관례가 아니니 그런것 같아

위 요구사항을 2가지를 들어줄래?