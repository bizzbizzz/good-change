# 🌱 Good Change - 백엔드

> 좋은변화 희망바우처 온라인 결제 시스템 REST API 서버

---

## 📌 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| ORM | MyBatis |
| DB | MySQL 8.0.45 |
| NoSQL | MongoDB (소켓 로그) |
| 인증 | JWT (jjwt) |
| API 문서 | SpringDoc OpenAPI (Swagger) |
| 빌드 | Gradle |
| 커넥션 풀 | HikariCP |
| AOP | ApiLogAspect (API 로그) |

---

## 📌 서버 환경

| 서비스 | 포트 | 설명 |
|--------|------|------|
| Apache | 80 | 리버스 프록시 |
| Spring Boot JAR | 8080 | 백엔드 API |
| Tomcat | 8090 | JSP 프론트 WAR |
| MySQL | 3306 | 메인 DB |
| MongoDB | 27017 | 소켓 로그 |
| Socket Server | 5000 | 한마음 단말기 연동 |
| OS | Ubuntu 24.04 | - |

---

## 📌 배포 구조

```
Apache (80)
├── /api/      → Spring Boot JAR (8080)
├── /uploads/  → Spring Boot JAR (8080)
└── /          → Tomcat WAR (8090, JSP 프론트)
```

| 경로 | 설명 |
|------|------|
| `~/springboot/` | Spring Boot JAR |
| `/var/lib/tomcat10/webapps/ROOT` | JSP 프론트 WAR |
| `~/springboot/uploads/board/` | 업로드 파일 |
| `~/good_batch/` | 배치 서버 |

---

## 📌 DB 테이블 구조

```
role              # 권한 (SUPER_ADMIN, ADMIN, MERCHANT, USER)
member            # 회원
member_token      # JWT 토큰 저장 (로그아웃/중복로그인 방지)
card              # 카드 (is_primary: 고유카드 여부)
card_list         # 카드번호 풀 (등록 가능한 카드번호 목록)
card_reissue_history # 카드 재발급 이력
merchant          # 가맹점
merchant_category # 가맹점 업종
payment           # 결제 내역 (파티셔닝 - 일별)
settlement        # 정산 내역 (파티셔닝 - 월별)
allowed_ip        # 허용 IP
site_config       # 사이트 설정 (URL, 전화번호 등)
banner            # 메인 슬라이드 배너
board             # 게시판 (공지/서식자료/언론보도)
common_file       # 공통 파일 (게시판 첨부/에디터 이미지)
member_inquiry    # 회원가입 문의
password_reset_token # 비밀번호 재설정 토큰
```

### 파티션 테이블
- `payment` → `transmission_date` 기준 일별 파티션
- `settlement` → `settlement_date` 기준 월별 파티션

### common_file refType 규칙
| refType | 설명 |
|---------|------|
| `notice_editor` | 공지사항 에디터 이미지 |
| `resource_editor` | 서식/자료 에디터 이미지 |
| `press_editor` | 언론보도 에디터 이미지 |
| `resource_attach` | 서식/자료 첨부파일 |
| `press_attach` | 언론보도 첨부파일 |

---

## 📌 프로젝트 구조

```
src/main/java/org/best/backspringboot
├── AOP             # ApiLogAspect (API 로그)
├── config          # Security, Swagger, JWT, XSS, CORS, Mongo 설정
├── controller      # API 컨트롤러
├── service         # 비즈니스 로직
├── mapper          # MyBatis 매퍼
├── entity          # 엔티티
├── dto             # DTO
│   ├── member
│   ├── merchant
│   ├── card
│   ├── cardHistory
│   ├── payment
│   ├── settlement
│   ├── board
│   ├── banner
│   ├── statistics
│   ├── allowedip
│   └── file
├── exception       # GlobalExceptionHandler
└── util            # JwtUtil, JwtFilter

src/main/resources
├── mapper                  # MyBatis XML
├── application.yaml        # 공통 설정
├── application-local.yml   # 로컬 설정
└── application-prod.yml    # 서버 설정 (git 제외)
```

---

## 📌 API 목록

### 회원 `/api/members`
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/members | 회원 등록 |
| POST | /api/members/login | 로그인 (JWT 반환) |
| POST | /api/members/logout | 로그아웃 (토큰 삭제) |
| GET | /api/members/check-id | 아이디 중복체크 |
| GET | /api/members | 전체 조회 (페이징) |
| GET | /api/members/{memberId} | 단건 조회 |
| PATCH | /api/members/{memberId} | 수정 (비밀번호 변경 포함) |
| DELETE | /api/members/{memberId} | 삭제 |
| POST | /api/members/password-reset/request | 비밀번호 재설정 요청 |
| POST | /api/members/password-reset/confirm | 비밀번호 재설정 확인 |

### 카드 `/api/cards`
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/cards | 카드 등록 |
| GET | /api/cards | 전체 조회 |
| GET | /api/cards/{cardNumber} | 카드번호로 조회 |
| GET | /api/cards/member/{memberId} | 회원별 조회 |
| GET | /api/cards/info | 카드 정보 조회 |
| PATCH | /api/cards/{cardId} | 카드 수정 |
| DELETE | /api/cards/{cardId} | 삭제 |

### 가맹점 `/api/merchants`
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/merchants | 가맹점 등록 |
| POST | /api/merchants/register | 가맹점+회원 통합 등록 |
| GET | /api/merchants | 전체 조회 (페이징) |
| GET | /api/merchants/{merchantId} | 단건 조회 |
| GET | /api/merchants/member/{memberId} | 회원별 조회 |
| GET | /api/merchants/categories | 업종 목록 조회 |
| PATCH | /api/merchants/{merchantId} | 수정 |
| DELETE | /api/merchants/{merchantId} | 삭제 |

### 결제 `/api/payments`
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/payments | 결제 |
| GET | /api/payments | 전체 조회 (페이징+검색) |
| GET | /api/payments/{paymentId} | 단건 조회 |
| PATCH | /api/payments/{paymentId}/cancel | 결제 취소 (15일 이내) |
| DELETE | /api/payments/{paymentId} | 삭제 (관리자) |

### 정산 `/api/settlements`
| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/settlements | 전체 조회 (페이징) |
| GET | /api/settlements/{settlementId} | 단건 조회 |
| PATCH | /api/settlements/{settlementId}/status | 상태 변경 |

### 배너 `/api/banners`
| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/banners | 전체 조회 (검색+페이징) |
| GET | /api/banners?useYn=Y | 노출 배너만 (프론트용) |
| GET | /api/banners/{bannerId} | 단건 조회 |
| POST | /api/banners | 등록 (이미지 업로드) |
| PATCH | /api/banners/{bannerId} | 수정 (이미지 선택적 교체) |
| DELETE | /api/banners/{bannerId} | 삭제 (서버 파일 함께 삭제) |

### 게시판 `/api/boards`
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/boards | 게시글 등록 |
| GET | /api/boards | 전체 조회 |
| GET | /api/boards/{boardId} | 단건 조회 |
| GET | /api/boards/types | 게시판 타입 목록 |
| PATCH | /api/boards/{boardId} | 수정 |
| DELETE | /api/boards/{boardId} | 삭제 |
| POST | /api/boards/image | 에디터 이미지 업로드 |
| POST | /api/boards/{boardId}/thumbnail | 썸네일 업로드 |
| DELETE | /api/boards/{boardId}/thumbnail | 썸네일 삭제 |
| POST | /api/boards/{boardId}/files | 첨부파일 업로드 |
| DELETE | /api/boards/files/{fileId} | 첨부파일 삭제 |
| GET | /api/boards/files/{fileId}/download | 첨부파일 다운로드 |

### 사이트 설정 `/api/site-config`
| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/site-config | 전체 조회 |
| GET | /api/site-config/{configKey} | 단건 조회 |
| POST | /api/site-config | 등록 |
| PATCH | /api/site-config/{configKey} | 수정 |
| DELETE | /api/site-config/{configKey} | 삭제 |

### 통계 `/api/stats`
| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/stats/daily-payment | 일별 결제 통계 |
| GET | /api/stats/monthly-settlement | 월별 정산 통계 |
| GET | /api/stats/stream | SSE 실시간 스트림 |
| GET | /api/stats/stream/live | SSE 실시간 라이브 |

### 기타
| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/logs | API 로그 조회 (MongoDB) |
| POST | /api/allowed-ips | 허용 IP 등록 |
| GET | /api/allowed-ips | 전체 조회 |
| GET | /api/allowed-ips/merchant/{merchantId} | 가맹점별 조회 |
| PUT | /api/allowed-ips/merchant/{merchantId} | 수정 |
| DELETE | /api/allowed-ips/{ipId} | 삭제 |

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
| SUPER_ADMIN | 최고 관리자 |
| ADMIN | 전체 데이터 접근 |
| USER | 본인 포인트/결제내역/카드 |
| MERCHANT | 본인 가맹점 결제/정산 |

- 만료시간: **4시간** (`14400000ms`)
- 로그아웃 시 `member_token` 테이블에서 토큰 삭제

---

## 📌 소켓 서버 (PartnerServer)

한마음 결제 단말기 연동 소켓 서버

| 항목 | 값 |
|------|----|
| 포트 | 5000 |
| 전문 크기 | 400 Byte |
| 인코딩 | EUC-KR |
| 전문번호 | 0500(승인요청) / 0510(승인응답) / 0600(취소요청) / 0610(취소응답) |
| 가맹점 조회 | business_number 기준 |
| chase_no | 비어있으면 서버 자동생성 (yyyyMMddHHmmssSSS) |

### 응답코드
| 코드 | 설명 |
|------|------|
| 0000 | 성공 |
| 0001 | 실패 |
| 1001 | 가맹점 없음 |
| 1002 | 가맹점번호 없음 |
| 1003 | 포인트 부족 |
| 3001 | 중복 거래 |
| 9999 | 서버 오류 |

---

## 📌 브랜치 전략

| 브랜치 | 역할 | 설명 |
|--------|------|------|
| `feature/기능명` | 기능 개발 | 기능 단위로 분기하여 작업 |
| `fix/버그명` | 버그 수정 | 버그 수정 전용 브랜치 |
| `main` | 임시 버퍼 | feature/fix 브랜치를 머지하는 통합 브랜치 |
| `RELEASE` | **배포 브랜치** | main → RELEASE 머지 시 자동 배포 |

> ⚠️ **RELEASE 브랜치 push는 퇴근 전(저녁)에만 진행합니다.**
> 배포 후 문제 발생 시 즉시 대응이 어려우므로 업무시간 중 배포를 지양합니다.

### 작업 흐름

```
feature/기능명 브랜치 생성
       ↓
개발 완료 후 main으로 PR 머지
       ↓
검증 완료 후 퇴근 전 RELEASE로 머지
       ↓
GitHub Actions 자동 배포 ✅
```

```bash
# 브랜치 생성 및 작업
git checkout main && git pull
git switch -c feature/기능명

# 작업 후 커밋
git add . && git commit -m "feat: 기능명 추가"
git push origin feature/기능명

# PR → main 머지 → 검증 → 퇴근 전 RELEASE 머지
git checkout RELEASE && git merge main
git push origin RELEASE
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

## 📌 CI/CD (GitHub Actions)

### 백엔드 배포 흐름 (gradle.yml)

```
RELEASE 브랜치 push
       ↓
GitHub Actions
  1. JDK 17 세팅
  2. Gradle 빌드 (테스트 스킵)
  3. SCP → ~/springboot/
  4. SSH → start.sh 실행
       ↓
배포 완료 ✅
```

### GitHub Secrets

| Secret | 값 |
|--------|----|
| `SSH_HOST` | 서버 IP |
| `SSH_USERNAME` | `bizline` |
| `SSH_PRIVATE_KEY` | PEM 개인키 전체 내용 |
| `SSH_PORT` | `22` |

### 서버 디렉토리 구조

```
~/springboot/
├── back-springboot-0.0.1-SNAPSHOT.jar
├── application-prod.yml    ← git 제외, 서버에 직접 관리
├── start.sh
└── logs/
```

### sudoers 설정

```
bizline ALL=(ALL) NOPASSWD: /usr/bin/systemctl, /bin/rm, /bin/cp, /bin/chown
```

---

## 📌 실행 방법

### 로컬 실행

```bash
./gradlew bootRun
```

### 서버 수동 재시작

```bash
~/springboot/start.sh
```

### 로그 확인

```bash
tail -f ~/springboot/logs/logs-$(date '+%Y%m%d')*.log
```

### API 문서

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📌 application.yaml 주요 설정

```yaml
jwt:
  secret: good-change-secret-key-must-be-at-least-256-bits-long-for-security
  expiration: 14400000  # 4시간

file:
  upload-path: /home/bizline/springboot/uploads/board/
  upload-url: /uploads/board/

spring:
  batch:
    jdbc:
      initialize-schema: never
  main:
    allow-bean-definition-overriding: true
```

---

## 📌 보안 설정

| 항목 | 설명 |
|------|------|
| JWT | 토큰 기반 인증 (4시간 만료) |
| XSS | XssFilter 적용 |
| CORS | allowed-origins 설정 |
| 로그아웃 | member_token 테이블 토큰 삭제 |
| API 로그 | AOP + MongoDB 저장 |
