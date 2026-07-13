# Hyeonkyu Lab

평소에 궁금했던 주제들을 직접 작성해보고 실험해보기 위한 멀티모듈 프로젝트입니다.

## 개요

- **언어**: Java 21
- **빌드 도구**: Gradle (멀티모듈)
- 각 모듈은 하나의 관심사/실험 주제를 담당합니다.

## 모듈

| 모듈 | 설명 |
|---|---|
| [architecture](./architecture) | 소프트웨어 아키텍처 패턴 실험 |

## 실행

```bash
./gradlew build                  # 전체 빌드
./gradlew :architecture:test     # 특정 모듈 테스트
```
