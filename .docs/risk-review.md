# 리스크 점검 문서

작성일: 2026-02-06
범위: 현재 구현 코드 기준 MVP 관련 기술 리스크

## 1. 판매자 주문 상태 변경 권한 검증 누락 (높음)
- 위치: `src/main/java/com/example/myproject/order/OrderService.java`
- 내용: 상태 변경 시 호출자가 SELLER인지만 확인하고, 해당 주문이 본인 상품을 포함한 주문인지 검증하지 않습니다.
- 영향: 다른 판매자의 주문 상태를 변경할 가능성이 있어 권한 우회 문제가 발생할 수 있습니다.
- 대응 방향: 주문-주문아이템-상품(sellerId) 기준으로 변경 권한을 검증하는 로직을 추가합니다.

## 2. 주문 상태 전이 규칙 부재 (중간)
- 위치: `src/main/java/com/example/myproject/order/OrderService.java`
- 내용: 상태를 임의로 변경할 수 있어 역행 전이 등 비정상 흐름이 허용됩니다.
- 영향: 운영 데이터 무결성 및 비즈니스 규칙 위반 가능성이 있습니다.
- 대응 방향: 허용 전이(예: `PLACED -> READY_TO_SHIP -> SHIPPING -> DELIVERED`)만 허용하는 검증을 추가합니다.

## 3. 웹 권한 인터셉터 패턴 누락 가능성 (낮음~중간)
- 위치: `src/main/java/com/example/myproject/config/WebConfig.java`
- 내용: 웹 구매자 보호 경로에 `/orders`는 포함되어 있지만 `/orders/{id}`는 패턴에 명시되어 있지 않습니다.
- 영향: 현재는 컨트롤러 내부 검증으로 방어되지만, 라우팅 확장 시 누락 리스크가 있습니다.
- 대응 방향: 인터셉터 경로를 `/orders/**`로 확장해 정책을 일관화합니다.

## 4. 미사용/빈 컨트롤러 존재 (낮음)
- 위치: `src/main/java/com/example/myproject/user/UserController.java`
- 내용: `@RequestMapping("/api/users")`만 선언된 빈 컨트롤러입니다.
- 영향: 유지보수 시 혼동을 유발할 수 있습니다.
- 대응 방향: 예정된 엔드포인트를 구현하거나, 사용 계획이 없다면 제거합니다.

## 작업 메모
- 문서 정합성 이슈(API 응답 래퍼/토큰 인증 불일치)는 `.docs/api.md` 갱신으로 반영 완료했습니다.
- 우선 처리 권장 순서: 1 -> 2 -> 3 -> 4
