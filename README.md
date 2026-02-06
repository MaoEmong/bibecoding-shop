# MYSHOP (쇼핑몰 MVP)

Spring Boot 기반으로 구현한 한국어 쇼핑몰 웹 애플리케이션입니다.  
판매자/구매자 역할을 분리해 상품 조회, 장바구니, 주문, 판매자 주문 처리까지의 핵심 흐름(MVP)을 구현했습니다.

## 1. 프로젝트 목적과 범위

- 목적: 간단한 쇼핑몰 서비스 MVP 구현
- 구현 범위:
  - 인증(회원가입/로그인/로그아웃)
  - 상품(목록/상세/판매자 등록/수정/삭제)
  - 장바구니(담기/수량 변경/삭제/조회)
  - 주문(생성/구매자 내역/판매자 상태 변경)
- 언어/문서 기준: 한국어

관련 문서:

- 요구사항: `.docs/requirements.md`
- 기능 우선순위: `.docs/features.md`
- 화면/흐름: `.docs/ui-flow.md`
- 데이터 모델: `.docs/data-model.md`
- API 설계: `.docs/api.md`
- 테스트 전략: `.docs/test-strategy.md`
- 구현 로드맵: `.docs/roadmap.md`
- 리스크 점검: `.docs/risk-review.md`

## 2. 데모 사용자 시나리오

### 2.1 구매자 시나리오

1. 회원가입/로그인
2. 상품 목록 조회 및 상세 확인
3. 장바구니에 상품 추가
4. 장바구니 수량 조정/삭제
5. 주문 생성(결제)
6. 주문 완료 페이지 확인
7. 주문 내역/상세 확인

### 2.2 판매자 시나리오

1. 회원가입/로그인(판매자 역할)
2. 상품 등록
3. 판매자 상품 목록 확인
4. 상품 수정/삭제
5. 판매자 주문 목록 확인
6. 주문 상태를 `READY_TO_SHIP` -> `SHIPPING` -> `DELIVERED` 로 변경

## 3. 기술 스택

- Language: `Java 21`
- Framework: `Spring Boot 4.0.2`
- Build: `Gradle Wrapper`
- DB: `H2 (in-memory)`
- ORM: `Spring Data JPA`
- View Template: `Mustache`
- Validation: `jakarta.validation`
- Security:
  - Spring Security 설정은 최소화(`permitAll`) 상태
  - 실제 접근 제어는 인터셉터 + 세션 기반 로직으로 처리
- Test: `JUnit 5`, `Spring Boot Test`, `AssertJ`

핵심 의존성은 `build.gradle` 참고.

## 4. 로컬 실행 가이드

### 4.1 필수 환경

- JDK 21
- 로컬 포트 `8080` 사용 가능 상태

### 4.2 애플리케이션 실행

Windows:

```powershell
.\gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

접속 주소:

- 홈: `http://localhost:8080/`
- 상품 목록: `http://localhost:8080/products`
- H2 콘솔: `http://localhost:8080/h2-console`

H2 콘솔 접속 정보:

- JDBC URL: `jdbc:h2:mem:myproject;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Username: `sa`
- Password: (빈 값)

### 4.3 테스트 실행

```powershell
.\gradlew.bat test
```

테스트 리포트:

- `build/reports/tests/test/index.html`

## 5. 프로젝트 구조

### 5.1 Java 소스 구조

```text
src/main/java/com/example/myproject
├─ common
│  ├─ SessionUtil
│  ├─ AuthInterceptor
│  ├─ RoleInterceptor
│  ├─ GlobalExceptionHandler
│  ├─ GlobalModelAdvice
│  └─ HomeController
├─ config
│  ├─ SecurityConfig
│  └─ WebConfig
├─ user
│  ├─ User, UserRole, UserRepository, UserService
│  ├─ AuthController, AuthPageController, LogoutPageController, MyPageController
│  └─ dto/*
├─ product
│  ├─ Product, ProductStatus, ProductRepository, ProductService
│  ├─ ProductController
│  ├─ ProductPageController, ProductDetailPageController
│  ├─ ProductManagePageController, ProductEditPageController
│  └─ dto/*
├─ cart
│  ├─ Cart, CartItem, CartRepository, CartItemRepository, CartService
│  ├─ CartController, CartPageController
│  └─ dto/*
└─ order
   ├─ Order, OrderItem, OrderStatus
   ├─ OrderRepository, OrderItemRepository, OrderService
   ├─ OrderController
   ├─ OrderPageController, OrderHistoryPageController
   ├─ OrderDetailPageController, OrderCompletePageController
   ├─ SellerOrderPageController
   └─ dto/*
```

### 5.2 리소스 구조

```text
src/main/resources
├─ application.properties
├─ data.sql
├─ static/css/app.css
└─ templates
   ├─ index.mustache
   ├─ login.mustache / signup.mustache
   ├─ products.mustache / product-detail.mustache / product-create.mustache / product-edit.mustache
   ├─ cart.mustache
   ├─ order.mustache / order-complete.mustache / order-history.mustache / order-detail.mustache
   ├─ seller-products.mustache / seller-orders.mustache
   ├─ mypage.mustache
   └─ partials/header.mustache / partials/footer.mustache
```

## 6. 인증/권한 처리 방식

### 6.1 인증

- 로그인 성공 시 `HttpSession`에 `USER_ID` 저장
- 주문 완료 페이지 표시를 위해 `LAST_ORDER_ID` 저장
- 로그아웃 시 세션 무효화

세션 키:

- `USER_ID`
- `LAST_ORDER_ID`

관련 파일:

- `src/main/java/com/example/myproject/common/SessionUtil.java`
- `src/main/java/com/example/myproject/user/AuthController.java`
- `src/main/java/com/example/myproject/user/AuthPageController.java`

### 6.2 권한 제어

- API 인증 체크:
  - `AuthInterceptor`가 `/api/**` 접근 시 로그인 여부 검사
  - 인증 예외 경로: `/api/auth/**`, `/api/products`, `/api/products/*`, `/h2-console/**`
- 역할 체크:
  - BUYER API: `/api/cart/**`, `/api/orders`
  - SELLER API: `/api/seller/**`
  - BUYER 페이지: `/cart`, `/order`, `/order/complete`, `/orders`
  - SELLER 페이지: `/seller/**`

관련 파일:

- `src/main/java/com/example/myproject/config/WebConfig.java`
- `src/main/java/com/example/myproject/common/AuthInterceptor.java`
- `src/main/java/com/example/myproject/common/RoleInterceptor.java`

## 7. 도메인 모델 상세

### 7.1 User

- 역할: `SELLER`, `BUYER`
- 주요 필드: `email`, `passwordHash`, `name`, `role`
- 제약:
  - `email` unique
  - 비밀번호는 BCrypt 해시 저장

### 7.2 Product

- 소유자: 판매자(`seller_id`)
- 상태: `ACTIVE`, `INACTIVE`
- 주요 필드: `name`, `description`, `price`, `stockQuantity`
- 검증:
  - 가격/재고는 0 이상

### 7.3 Cart / CartItem

- 구매자당 장바구니 1개 (`UNIQUE(buyer_id)`)
- `CartItem`은 `(cart_id, product_id)` unique
- 수량 최소값: 1

### 7.4 Order / OrderItem

- 주문 상태:
  - `PLACED`
  - `READY_TO_SHIP`
  - `SHIPPING`
  - `DELIVERED`
  - `CANCELED`
- 주문 생성 시:
  - 재고 검증
  - 재고 차감
  - 총 금액 계산 후 저장

데이터 모델 상세는 `.docs/data-model.md` 참고.

## 8. 비즈니스 규칙 요약

- 회원가입 이메일 중복 불가
- BUYER만 장바구니/주문 가능
- SELLER만 상품 등록/수정/삭제 가능
- 상품 수정/삭제는 본인 상품만 가능
- 주문 생성 시 재고 부족이면 실패
- 장바구니 수량 변경 시 재고 초과 불가
- 주문이 있는 상품은 삭제 제한

## 9. API 상세 요약

실제 구현 경로 기준입니다. 세부 요청/응답 예시는 `.docs/api.md`를 참고하세요.

### 9.1 인증

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `POST /api/auth/logout`

### 9.2 상품

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/seller/products`

### 9.3 장바구니

- `GET /api/cart`
- `POST /api/cart/items`
- `PATCH /api/cart/items/{id}`
- `DELETE /api/cart/items/{id}`

### 9.4 주문

- `POST /api/orders`
- `GET /api/orders`

### 9.5 판매자 주문

- `GET /api/seller/orders`
- `PATCH /api/seller/orders/{id}/status`

## 10. 화면 라우트 맵

공통 페이지:

- `/`
- `/products`
- `/products/{id}`
- `/login`
- `/signup`
- `/logout`
- `/mypage`

구매자 페이지:

- `/cart`
- `/order`
- `/order/complete`
- `/orders`
- `/orders/{id}`

판매자 페이지:

- `/seller/products`
- `/seller/products/new`
- `/seller/products/{id}/edit`
- `/seller/orders`

## 11. 요청 흐름 예시

### 11.1 장바구니 추가

1. 구매자가 상품 상세(`/products/{id}`)에서 수량 입력
2. `POST /cart/items`(페이지 폼) 또는 `POST /api/cart/items` 호출
3. 서비스에서 장바구니 생성/조회
4. 상품 존재/재고 검증
5. 기존 아이템이 있으면 수량 합산, 없으면 신규 생성

### 11.2 주문 생성

1. 구매자가 `/order`에서 주문 확인
2. `POST /order/complete` 호출
3. 장바구니 아이템으로 `OrderCreateRequest` 구성
4. 주문 서비스에서 재고 검증/차감/주문 및 주문아이템 저장
5. 장바구니 비우기
6. 세션에 `LAST_ORDER_ID` 저장 후 `/order/complete`로 리다이렉트

### 11.3 판매자 주문 상태 변경

1. 판매자가 `/seller/orders`에서 상태 버튼 클릭
2. `POST /seller/orders/{id}/status` 호출
3. 서비스에서 주문 상태 갱신

## 12. 테스트 전략과 현재 테스트

문서 기준 테스트 전략:

- 단위 테스트: 도메인/서비스 규칙
- 통합 테스트: 컨트롤러 + 서비스 + DB
- 인수 시나리오 테스트: 핵심 플로우

현재 구현된 테스트:

- `ApiIntegrationTests#mvpFlowServices`
  - 판매자/구매자 생성
  - 상품 생성
  - 장바구니 추가
  - 주문 생성
  - 판매자 주문 상태 변경
- `MyprojectApplicationTests#contextLoads`

추가 권장:

- 권한 위반 케이스 테스트
- 재고 부족/없는 리소스 에러 테스트
- API 응답 포맷 정합성 테스트

## 13. 더미 데이터 및 계정 주의사항

- `data.sql`이 앱 시작 시 자동 로드됩니다.
- 포함 데이터:
  - 사용자 4명(판매자 2, 구매자 2)
  - 상품, 장바구니, 주문 샘플
- 더미 계정 비밀번호:
  - `password_hash = DUMMY_HASH`이므로 실제 로그인 불가
  - 로그인 확인은 회원가입으로 생성한 계정 사용

## 14. 설정 파일 핵심값

`src/main/resources/application.properties`:

- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.open-in-view=false`
- `spring.sql.init.mode=always`
- `spring.jpa.defer-datasource-initialization=true`
- `spring.h2.console.enabled=true`

의미:

- 앱 실행 시 스키마 업데이트 + `data.sql` 초기 데이터 적재
- OSIV 비활성화로 Lazy 로딩 관련 실수 방지(필요 조회는 fetch join으로 처리)

## 15. 예외 처리 정책

- 공통 예외 핸들러: `GlobalExceptionHandler`
- 처리 대상:
  - `IllegalArgumentException` -> `400 BAD_REQUEST`
  - `MethodArgumentNotValidException` -> `400 BAD_REQUEST`
  - `IllegalStateException` -> `401 UNAUTHORIZED`

역할 인터셉터 API 응답:

- 미인증: `401`, 코드 `UNAUTHORIZED`
- 권한 없음: `403`, 코드 `FORBIDDEN`

## 16. 현재 알려진 리스크

상세는 `.docs/risk-review.md` 참고.

핵심 요약:

- 판매자 주문 상태 변경 시 주문 소유 검증 보강 필요
- API 문서와 실제 응답 포맷 일부 불일치
- 문서의 인증 방식(토큰 예시)과 실제 구현(세션) 차이
- 일부 경로 권한 패턴 정합성 점검 필요

## 17. 개발 규칙 및 문서 운영

- 문서와 주석은 한국어 작성
- 기능 구현 전 요구사항/범위 먼저 고정
- 의사결정 및 변경사항은 날짜와 함께 기록
- 작업 로그: `.docs/.ask/YYYY-MM-DD.md`

## 18. 향후 개선 백로그(추천)

1. 권한 검증 강화(특히 판매자 주문 상태 변경)
2. API 응답 포맷 표준화(`success/data/error`)
3. 인증 방식 단일화(세션 유지 vs 토큰 전환)
4. 테스트 확장(권한/에러/회귀)
5. 주문 취소/배송 상세/리뷰/문의/위시리스트 확장

---

온보딩 기준:

- 문서 먼저: `.docs/README.md` -> `.docs/requirements.md` -> `.docs/features.md`
- 코드 시작점: `WebConfig`, `AuthController`, `ProductService`, `CartService`, `OrderService`
