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
├── config          # Security, Swagger, JWT, XSS, CORS 설정
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
├── mapper                  # MyBatis XML
├── application.yml         # 공통 설정
├── application-local.yml   # 로컬 설정
└── application-prod.yml    # 서버 설정 (git 제외)
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

## 📌 CI/CD (GitHub Actions)

### 전체 흐름

```
git push origin main
       ↓
GitHub Actions 실행
  1. JDK 17 세팅
  2. Gradle 캐시
  3. Gradle 빌드 (테스트 스킵)
       ↓
SCP로 서버 전송
  4. JAR 파일 → ~/springboot/
       ↓
SSH 접속
  5. start.sh 실행 (Graceful shutdown → 재시작)
       ↓
배포 완료 ✅
```

### GitHub Secrets 등록

```
GitHub 레포 → Settings → Secrets and variables → Actions
```

| Secret | 값 |
|--------|----|
| `SSH_HOST` | 서버 IP |
| `SSH_USERNAME` | `bizline` |
| `SSH_PRIVATE_KEY` | PEM 개인키 전체 내용 |
| `SSH_PORT` | `22` |

### SSH 키 생성 (최초 1회)

```bash
# 서버에서 실행
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions -N ""

# 공개키 authorized_keys에 추가
cat ~/.ssh/github_actions.pub >> ~/.ssh/authorized_keys

# 개인키 내용 복사 → GitHub Secret SSH_PRIVATE_KEY에 등록
cat ~/.ssh/github_actions
```

### start.sh (Graceful Shutdown)

```bash
# 서버 ~/springboot/start.sh 위치
# SIGTERM 전송 → 30초 대기 → 강제 종료 → 재시작
```

### 서버 디렉토리 구조

```
~/springboot/
├── back-springboot-0.0.1-SNAPSHOT.jar
├── application-prod.yml    ← git 제외, 서버에 직접 관리
├── start.sh
└── logs/
    └── logs-yyyyMMdd_HHmmss.log
```

### application-prod.yml (서버에만 존재)

```yaml
server:
  shutdown: graceful

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/good-change?useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
    username: root
    password: 비밀번호
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 10
      connection-timeout: 10000
      idle-timeout: 600000
      max-lifetime: 1800000
  lifecycle:
    timeout-per-shutdown-phase: 30s

cors:
  allowed-origins: https://도메인명

logging:
  level:
    org.best: INFO
```

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
# GitHub main 브랜치에 push 시 자동 배포
git push origin main

# 수동 실행
~/springboot/start.sh
```

### 로그 확인
```bash
tail -f ~/springboot/logs/logs-$(date '+%Y%m%d')*.log
```
