# 🌱 Good Change

> 좋은변화 - 희망바우처 온라인 결제 시스템

---

## 📌 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.5 |
| ORM | MyBatis |
| DB | MySQL 8.0.45 |
| 인증 | JWT (jjwt 0.12.6) |
| API 문서 | SpringDoc OpenAPI |
| 빌드 | Gradle |
| 커넥션 풀 | HikariCP |

---

## 📌 서버 환경

| 서비스 | 상태 | 버전 |
|--------|------|------|
| Java | ✅ 실행 중 | JDK 17 |
| MySQL | ✅ 실행 중 | 8.0.45 |
| Apache | ✅ 실행 중 | 2.4.58 |
| OS | Ubuntu 24.04 | - |

---

## 📌 DB 테이블 구조

```
role              # 권한 (ADMIN, USER, MERCHANT)
member            # 회원
card              # 카드
merchant          # 가맹점
merchant_category # 가맹점 업종
payment           # 결제 내역 (파티셔닝 - 일별)
settlement        # 정산 내역 (파티셔닝 - 월별)
allowed_ip        # 허용 IP
```

### 파티션 테이블
- `payment` → `transmission_date` 기준 일별 파티션
- `settlement` → `settlement_date` 기준 월별 파티션
- 파티션 자동 생성/삭제 (MySQL Event Scheduler)

---

## 📌 프로젝트 구조

```
src/main/java/org/best/backspringboot
├── config          # Security, Swagger, JWT, XSS 설정
├── controller      # API 컨트롤러
├── service         # 비즈니스 로직
├── mapper          # MyBatis 매퍼
├── entity          # 엔티티
├── dto             # DTO
│   ├── member
│   ├── merchant
│   ├── card
│   ├── payment
│   └── settlement
├── filter          # JWT 필터
└── util            # JwtUtil, JwtFilter

src/main/resources
├── mapper          # MyBatis XML
├── application.yml         # 공통 설정
├── application-local.yml   # 로컬 설정
└── application-prod.yml    # 서버 설정
```

---

## 📌 API 목록

| 분류 | Method | URL | 설명 |
|------|--------|-----|------|
| 회원 | POST | /api/members | 회원 등록 |
| 회원 | POST | /api/members/login | 로그인 |
| 회원 | GET | /api/members/check-id | 아이디 중복체크 |
| 회원 | GET | /api/members | 전체 조회 |
| 회원 | GET | /api/members/{memberId} | 단건 조회 |
| 회원 | PATCH | /api/members/{loginId} | 수정 |
| 회원 | DELETE | /api/members/{loginId} | 삭제 |
| 카드 | POST | /api/cards | 카드 등록 |
| 카드 | GET | /api/cards/{cardNumber} | 카드번호로 조회 |
| 카드 | GET | /api/cards/member/{memberId} | 회원별 조회 |
| 카드 | GET | /api/cards | 전체 조회 |
| 카드 | DELETE | /api/cards/{cardId} | 삭제 |
| 가맹점 | POST | /api/merchants | 가맹점 등록 |
| 가맹점 | GET | /api/merchants/{merchantId} | 단건 조회 |
| 가맹점 | GET | /api/merchants/member/{memberId} | 회원별 조회 |
| 가맹점 | GET | /api/merchants | 전체 조회 |
| 가맹점 | PATCH | /api/merchants/{merchantId} | 수정 |
| 가맹점 | DELETE | /api/merchants/{merchantId} | 삭제 |
| 결제 | POST | /api/payments | 결제 |
| 결제 | GET | /api/payments | 전체 조회 |
| 결제 | GET | /api/payments/{paymentId} | 단건 조회 |
| 결제 | PATCH | /api/payments/{paymentId}/cancel | 결제 취소 |
| 결제 | DELETE | /api/payments/{paymentId} | 삭제 |
| 정산 | GET | /api/settlements | 전체 조회 |
| 정산 | GET | /api/settlements/{settlementId} | 단건 조회 |
| 정산 | PATCH | /api/settlements/{settlementId}/status | 상태 변경 |

---

## 📌 JWT 토큰 구조

```json
{
  "sub": "loginId",
  "memberId": 1,
  "role": "MERCHANT",
  "merchantId": 5,
  "iat": 1234567890,
  "exp": 1234567890
}
```

| role | 설명 |
|------|------|
| ADMIN | 전체 데이터 접근 |
| USER | 본인 포인트/결제내역/카드 |
| MERCHANT | 본인 가맹점 결제/정산 |

---

## 📌 테스트 코드

### Service 단위 테스트
| 파일 | 설명 |
|------|------|
| MemberServiceTest | 회원 CRUD + 로그인 + 중복체크 |
| MerchantServiceTest | 가맹점 CRUD + 사업자번호 중복 |
| PaymentServiceTest | 결제/취소 + 포인트부족/15일초과 |
| CardServiceTest | 카드 CRUD + 3장 제한 |
| SettlementServiceTest | 정산 조회 + 상태변경 |
| JwtUtilTest | 토큰 생성/추출/검증 |

### Controller API 테스트
| 파일 | 설명 |
|------|------|
| MemberControllerTest | 회원 API 13개 |
| MerchantControllerTest | 가맹점 API 12개 |
| CardControllerTest | 카드 API 10개 |
| PaymentControllerTest | 결제 API 13개 |
| SettlementControllerTest | 정산 API 12개 |

---

## 📌 보안 설정

```
ufw       → 포트별 허용 IP 제한
ipset     → 국가별 IP 차단 (중국/일본/EU)
JWT       → 토큰 기반 인증
XSS       → XssFilter 적용
```

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
| `test` | 테스트 코드 |

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
