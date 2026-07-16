// java 플러그인·JDK 21·JUnit·Spring Boot(JPA+MySQL) 의존성은 모두 루트 build.gradle 에서 적용된다.
// 이 파일에는 이 모듈에만 필요한 것만 둔다.
dependencies {
    testImplementation("com.tngtech.archunit:archunit:1.4.1")
    // JPA 예제(@DataJpaTest)를 MySQL 없이 빌드에서 검증하기 위한 임베디드 DB.
    // 운영/부트 실행은 application.yml 의 MySQL 을 쓴다.
    testRuntimeOnly("com.h2database:h2")
}
