# ⛺ CampKit — 캠핑 장비 위시리스트 & 예산 추천

캠핑에 필요한 장비를 등록하고, 사용자가 입력한 예산에 맞춰 구매 가능한 장비를 최대 3개 추천하는 Spring Boot 기반 개인 프로젝트입니다.

## 1. 프로젝트 개요

- **주제:** 캠핑 장비 위시리스트 및 예산 추천
- **개발 언어:** Java 21, HTML, CSS, JavaScript
- **프레임워크:** Spring Boot 4.1.2
- **빌드 도구:** Gradle Wrapper
- **테스트:** JUnit 5, Spring Boot Test, Postman
- **데이터 저장:** 메모리 기반 (DB 미사용)

## 2. 주요 기능

| 기능 | 설명 |
|---|---|
| 장비 등록 | 캠핑 장비 이름과 가격을 입력하고 ID 자동 생성 |
| 전체 목록 조회 | 등록된 캠핑 장비 전체 조회 |
| 단건 조회 | ID를 통해 특정 장비 조회 |
| 예산별 추천 | 예산 이하의 장비를 가격 오름차순으로 최대 3개 추천 |
| 입력값 검증 | 빈 이름, 음수 가격, 음수 예산 거부 |
| 웹 화면 | HTML·CSS·JavaScript 기반 위시리스트 및 추천 화면 |
| REST API | Controller를 통한 등록·조회·추천 API 제공 |

### 예산 추천 규칙

1. 개별 장비 가격이 예산 이하인 장비만 선택합니다.
2. 가격이 낮은 순서대로 정렬합니다.
3. 가격이 같으면 등록 순서를 유지합니다.
4. 정렬된 결과에서 최대 3개를 추천합니다.
5. 추천 과정에서 원본 목록의 순서는 변경하지 않습니다.
6. 조건에 맞는 장비가 없으면 빈 목록을 반환합니다.

## 3. 프로젝트 구조

```text
personal-project/
├── src/
│   ├── main/
│   │   ├── java/com/example/campkit/
│   │   │   ├── CampkitApplication.java
│   │   │   ├── controller/
│   │   │   │   └── CollectionController.java
│   │   │   ├── domain/
│   │   │   │   └── CollectionItem.java
│   │   │   ├── repository/
│   │   │   │   ├── CollectionRepository.java
│   │   │   │   └── MemoryCollectionRepository.java
│   │   │   └── service/
│   │   │       └── CollectionService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── css/
│   │           │   └── style.css
│   │           └── js/
│   │               └── app.js
│   └── test/java/com/example/campkit/
│       ├── CollectionServiceTest.java
│       └── integration/
│           └── CampkitApplicationTests.java
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## 4. 객체지향 설계

| 구성 요소 | 역할 |
|---|---|
| `CollectionItem` | 캠핑 장비 객체 및 입력값 검증 |
| `CollectionRepository` | 저장·조회 기능을 정의하는 인터페이스 |
| `MemoryCollectionRepository` | ArrayList를 활용한 메모리 저장소 구현 |
| `CollectionService` | 장비 등록·조회·예산 추천 비즈니스 로직 |
| `CollectionController` | HTTP 요청을 처리하고 Service 호출 |

**설계 특징**

- Repository 인터페이스와 구현체를 분리했습니다.
- Service는 Repository 인터페이스를 생성자로 주입받습니다.
- `@Service`, `@Repository`, `@RestController`를 사용해 Spring Bean을 관리합니다.
- Domain 객체에서 이름과 가격을 검증합니다.
- 추천 기능은 Stream API를 활용해 필터링·정렬·개수 제한을 처리합니다.

## 5. REST API 명세

기본 주소: `http://localhost:8080`

| Method | Endpoint | 기능 |
|---|---|---|
| POST | `/api/items` | 캠핑 장비 등록 |
| GET | `/api/items` | 전체 장비 조회 |
| GET | `/api/items/{id}` | ID 기반 단건 조회 |
| GET | `/api/items/recommend?budget=50000` | 예산별 장비 추천 |

### 5-1. 캠핑 장비 등록

**POST /api/items**

요청 형식: `x-www-form-urlencoded`

| Parameter | 예시 |
|---|---|
| name | 텐트 |
| price | 120000 |

응답 예시:

```json
{
  "id": 1,
  "name": "텐트",
  "price": 120000
}
```

### 5-2. 전체 장비 조회

**GET /api/items**

응답 예시:

```json
[
  {
    "id": 1,
    "name": "텐트",
    "price": 120000
  },
  {
    "id": 2,
    "name": "랜턴",
    "price": 15000
  }
]
```

### 5-3. 단건 조회

**GET /api/items/1**

응답 예시:

```json
{
  "id": 1,
  "name": "텐트",
  "price": 120000
}
```

### 5-4. 예산별 장비 추천

**GET /api/items/recommend?budget=50000**

등록된 장비가 다음과 같다고 가정합니다.

| 장비 | 가격 |
|---|---:|
| 텐트 | 120,000원 |
| 랜턴 | 15,000원 |
| 캠핑 의자 | 30,000원 |
| 버너 | 45,000원 |
| 카라비너 | 5,000원 |

추천 결과:

```json
[
  {
    "id": 5,
    "name": "카라비너",
    "price": 5000
  },
  {
    "id": 2,
    "name": "랜턴",
    "price": 15000
  },
  {
    "id": 3,
    "name": "캠핑 의자",
    "price": 30000
  }
]
```

버너 역시 예산 이하이지만 최대 3개 제한에 따라 추천 결과에서 제외됩니다.

## 6. 프로젝트 실행 방법

저장소를 Clone한 뒤 프로젝트 폴더로 이동합니다.

```bash
cd personal-project
```

**Windows (Git Bash)**

```bash
./gradlew.bat bootRun
```

**macOS / Linux**

```bash
./gradlew bootRun
```

Spring Boot 실행 후 웹 브라우저에서 다음 주소로 접속합니다.

http://localhost:8080/

REST API는 `/api/items`에서 확인할 수 있습니다.

## 7. 테스트

### 단위 테스트 및 통합 테스트 실행

**Windows**

```bash
./gradlew.bat test
```

**macOS / Linux**

```bash
./gradlew test
```

### 테스트 결과

**총 8개 테스트 통과 (단위 테스트 7개, Spring 통합 테스트 1개)**

| 테스트 항목 | 결과 |
|---|---|
| 정상 등록·전체 조회·ID 조회 | PASS |
| 빈 이름 및 음수 가격 거부 | PASS |
| 예산별 가격 정렬 및 최대 3개 추천 | PASS |
| 무료 장비와 음수 예산 검증 | PASS |
| 추천 이후 원본 순서 보존 | PASS |
| 존재하지 않는 ID 조회 예외 | PASS |
| 동일 가격 장비의 등록 순서 유지 | PASS |
| Spring 의존성 주입 확인 | PASS |

### Postman 테스트

Postman을 이용해 REST API의 요청 및 응답을 확인했습니다.

- `POST /api/items` — `x-www-form-urlencoded` 방식으로 장비 등록
- `GET /api/items` — 전체 목록 조회 확인
- `GET /api/items/{id}` — 단건 조회
- `GET /api/items/recommend` — 예산에 따른 장비 추천

## 8. 웹 화면

HTML, CSS, JavaScript를 분리하여 캠핑 장비 위시리스트 화면을 구현했습니다.

**주요 화면 구성**

- 캠핑 장비 이름 및 가격 입력
- 등록된 캠핑 장비 목록
- 예산 입력 및 최대 3개 장비 추천
- 잘못된 입력에 대한 안내 메시지

현재 웹 화면은 JavaScript 배열을 통해 데이터를 관리하는 프론트엔드 데모입니다.

Spring Boot REST API는 별도로 동작하며, 웹 화면과 API를 연결하는 `fetch()` 연동은 아직 적용하지 않았습니다.

## 9. 추가 개선 계획

- JavaScript `fetch()`를 활용한 REST API 연동
- 요청 오류에 대한 HTTP 상태 코드 및 예외 응답 개선
- 장비 이름 검색 및 구매 완료 상태 관리

---

**CampKit | SeSAC Spring Boot 개인 프로젝트 | 2026.11.08**