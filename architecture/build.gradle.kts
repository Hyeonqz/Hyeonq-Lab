// java 플러그인·JDK 21·JUnit·Spring Boot(JPA+MySQL) 의존성은 모두 루트 build.gradle 에서 적용된다.
// 이 파일에는 이 모듈에만 필요한 것(ArchUnit 적합도 함수)만 둔다.
dependencies {
    testImplementation("com.tngtech.archunit:archunit:1.4.1")
}
