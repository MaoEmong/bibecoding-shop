# 리스크 점검 문서

작성일: 2026-02-06
범위: 현재 구현 코드 기준 MVP 관련 기술 리스크

## 1. 판매자 주문 상태 변경 권한 검증 누락 (높음)
- 위치: `src/main/java/com/example/myproject/order/OrderService.java:91`, `src/main/java/com/example/myproject/order/OrderService.java:98`
- 내용: 상태 변경 시 호출자가 SELLER인지만 확인하고, 해당 주문이 본인 상품을 포함한 주문인지 검증하지 않습니다.
- 영향: 다른 판매자의 주문 상태를 변경할 가능성이 있어 권한 우회 문제가 발생할 수 있습니다.
- 대응 방향: 주문-주문아이템-상품(sellerId) 기준으로 변경 권한을 검증하는 로직을 추가합니다.

## 2. API 응답 규격 불일치 (중간)
- 위치:
  - `src/main/java/com/example/myproject/product/ProductController.java:24`
  - `src/main/java/com/example/myproject/cart/CartController.java:23`
  - `src/main/java/com/example/myproject/order/OrderController.java:23`
- 내용: 문서(`.docs/api.md`)의 공통 응답 포맷 `{ success, data/error }` 대신 Raw DTO/리스트를 직접 반환하고 있습니다.
- 영향: 클라이언트 구현 시 문서와 실제 응답이 달라 혼선 및 추가 분기 처리가 필요해집니다.
- 대응 방향: 공통 응답 래퍼를 도입하거나 문서를 실제 구현 기준으로 정합성 있게 갱신합니다.

## 3. 로그인 방식 불일치 (중간)
- 위치: `src/main/java/com/example/myproject/user/AuthController.java:37`, `src/main/java/com/example/myproject/user/AuthController.java:40`
- 내용: 문서에는 토큰 기반 응답 예시가 있으나, 실제 구현은 세션 저장 + `"ok"` 메시지 응답입니다.
- 영향: API 소비자 관점에서 인증 방식 해석이 달라 연동 오류 가능성이 있습니다.
- 대응 방향: 세션 기반으로 문서를 수정하거나, 토큰 기반으로 실제 구현을 맞추는 방향 중 하나를 선택합니다.

## 4. 웹 권한 인터셉터 패턴 누락 가능성 (낮음~중간)
- 위치: `src/main/java/com/example/myproject/config/WebConfig.java:37`, `src/main/java/com/example/myproject/order/OrderDetailPageController.java:25`
- 내용: 웹 구매자 보호 경로에 `/orders`는 포함되어 있지만 `/orders/{id}`는 패턴에 명시되어 있지 않습니다.
- 영향: 현재는 컨트롤러 내부 검증으로 방어되지만, 라우팅 확장 시 누락 리스크가 있습니다.
- 대응 방향: 인터셉터 경로를 `/orders/**`로 확장해 정책을 일관화합니다.

## 5. 미사용/빈 컨트롤러 존재 (낮음)
- 위치: `src/main/java/com/example/myproject/user/UserController.java:8`
- 내용: `@RequestMapping("/api/users")`만 선언된 빈 컨트롤러입니다.
- 영향: 유지보수 시 혼동을 유발할 수 있습니다.
- 대응 방향: 예정된 엔드포인트를 구현하거나, 사용 계획이 없다면 제거합니다.

## 작업 메모
- 본 문서는 "리스크 식별"만 수행했으며, 수정 작업은 별도 턴에서 진행합니다.
- 우선 처리 권장 순서: 1 -> 2/3 -> 4 -> 5
