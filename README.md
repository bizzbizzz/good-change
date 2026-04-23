# 🌱 Good Change

> 좋은변화 - 온라인 결제 시스템

---

## 📌 브랜치 전략

### 브랜치 네이밍 규칙
```
feature/기능명    # 새 기능
fix/버그명        # 버그 수정
hotfix/긴급수정   # 긴급 수정
```

### 작업 흐름
```bash
# 1. main 최신 코드 pull
git checkout main
git pull origin main

# 2. 브랜치 생성
git switch -c feature/기능명

# 3. 작업 후 커밋
git add .
git commit -m "feat: 기능명 추가"

# 4. push
git push origin feature/기능명

# 5. GitHub에서 PR 생성 → 상대방 리뷰 → 머지
```

---

## 📌 팀 규칙

```
1. main 직접 push 금지
2. 항상 브랜치 만들어서 작업
3. PR 올리면 상대방이 리뷰 후 머지
4. 본인 PR 본인 머지 금지
```

---

## 📌 커밋 메시지 규칙

| 태그 | 설명 |
|------|------|
| `feat` | 새 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 |
| `docs` | 문서 수정 |
| `chore` | 설정 변경 |

### 예시
```
feat: 회원 CRUD 추가
fix: 로그인 오류 수정
refactor: MemberService 리팩토링
docs: README 업데이트
chore: build.gradle 의존성 추가
```

---

## 📌 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.5 |
| ORM | MyBatis |
| DB | MySQL 5.x |
| 인증 | JWT (jjwt 0.12.6) |
| API 문서 | SpringDoc OpenAPI |
| 빌드 | Gradle |

---

## 📌 프로젝트 구조

```
src/main/java/org/best
├── config          # 설정 (Security, Swagger, JWT 등)
├── controller      # API 컨트롤러
├── service         # 비즈니스 로직
├── mapper          # MyBatis 매퍼
├── entity          # 엔티티
├── dto             # DTO
│   └── member
│   └── merchant
│   └── card
│   └── payment
│   └── settlement
├── filter          # JWT 필터
└── util            # 유틸리티

src/main/resources
├── mapper          # MyBatis XML
├── application.yml         # 공통 설정
├── application-local.yml   # 로컬 설정
└── application-prod.yml    # 서버 설정
```

---

## 📌 DB 테이블 구조

```
member          # 회원
card            # 카드
merchant        # 가맹점
merchant_category # 가맹점 업종
payment         # 결제 내역
settlement      # 정산 내역
member_role     # 회원 권한
allowed_ip      # 허용 IP
```

---

## 📌 API 문서

서버 실행 후 아래 URL 접속

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📌 실행 방법

### 로컬 실행
```bash
./gradlew bootRun
```

### 서버 배포
```bash
./gradlew build
java -jar build/libs/back-springboot-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
