# API 설계 (현재 구현 기준)

기본 규칙
- 인증 방식: 세션 기반(`JSESSIONID` 쿠키)
- 인증 필요 API는 `HttpSession`의 `USER_ID`를 사용합니다.
- 역할: 인터셉터에서 SELLER/BUYER 권한을 검사합니다.
- 시간 필드는 서버의 `LocalDateTime` 직렬화 형식으로 반환됩니다.

## 응답 규칙
- 성공 응답은 공통 래퍼 없이 DTO/리스트를 그대로 반환합니다.
- 실패 응답은 케이스별로 아래 형태를 사용합니다.
  - 인터셉터 차단: `{ "success": false, "error": { "code": "UNAUTHORIZED|FORBIDDEN", "message": "..." } }`
  - 전역 예외 처리: `{ "success": false, "error": { "code": "BAD_REQUEST|VALIDATION_ERROR|UNAUTHORIZED", "message": "..." } }`

## 인증
### POST /api/auth/signup
- 설명: 회원가입(판매자/구매자)
- 요청
```json
{
  "email": "buyer@example.com",
  "password": "string",
  "name": "홍길동",
  "role": "BUYER"
}
```
- 응답
```json
{
  "id": 1,
  "email": "buyer@example.com",
  "name": "홍길동",
  "role": "BUYER",
  "createdAt": "2026-02-06T14:00:00"
}
```

### POST /api/auth/login
- 설명: 로그인(성공 시 세션 생성)
- 요청
```json
{
  "email": "buyer@example.com",
  "password": "string"
}
```
- 응답
```json
{
  "message": "ok",
  "role": "BUYER"
}
```

### POST /api/auth/logout
- 설명: 로그아웃(세션 무효화)
- 요청: 없음
- 응답
```json
{
  "message": "ok",
  "role": null
}
```

## 상품
### GET /api/products
- 설명: 상품 목록 조회
- 응답
```json
[
  {
    "id": 10,
    "sellerId": 3,
    "name": "상품명",
    "description": "상품 설명",
    "price": 12000,
    "stockQuantity": 5,
    "status": "ACTIVE",
    "createdAt": "2026-02-06T14:00:00"
  }
]
```

### GET /api/products/{id}
- 설명: 상품 상세
- 응답
```json
{
  "id": 10,
  "sellerId": 3,
  "name": "상품명",
  "description": "상품 설명",
  "price": 12000,
  "stockQuantity": 5,
  "status": "ACTIVE",
  "createdAt": "2026-02-06T14:00:00"
}
```

### POST /api/seller/products
- 설명: 상품 등록(판매자)
- 요청
```json
{
  "name": "상품명",
  "description": "상품 설명",
  "price": 12000,
  "stockQuantity": 10
}
```
- 응답
```json
{
  "id": 10,
  "sellerId": 3,
  "name": "상품명",
  "description": "상품 설명",
  "price": 12000,
  "stockQuantity": 10,
  "status": "ACTIVE",
  "createdAt": "2026-02-06T14:00:00"
}
```

## 장바구니 (구매자)
### GET /api/cart
- 설명: 장바구니 조회
- 응답
```json
{
  "items": [
    {
      "id": 1,
      "productId": 10,
      "name": "상품명",
      "price": 12000,
      "quantity": 2
    }
  ],
  "totalPrice": 24000
}
```

### POST /api/cart/items
- 설명: 장바구니 담기
- 요청
```json
{
  "productId": 10,
  "quantity": 2
}
```
- 응답
```json
{
  "id": 1,
  "productId": 10,
  "name": "상품명",
  "price": 12000,
  "quantity": 2
}
```

### PATCH /api/cart/items/{id}
- 설명: 장바구니 수량 변경
- 요청
```json
{
  "quantity": 3
}
```
- 응답
```json
{
  "id": 1,
  "productId": 10,
  "name": "상품명",
  "price": 12000,
  "quantity": 3
}
```

### DELETE /api/cart/items/{id}
- 설명: 장바구니 아이템 삭제
- 응답: HTTP 200, 본문 없음

## 주문 (구매자)
### POST /api/orders
- 설명: 주문 생성(구매)
- 요청
```json
{
  "items": [
    { "productId": 10, "quantity": 2 }
  ]
}
```
- 응답
```json
{
  "id": 100,
  "status": "PLACED",
  "totalPrice": 24000,
  "createdAt": "2026-02-06T14:00:00"
}
```

### GET /api/orders
- 설명: 내 주문 목록 조회
- 응답
```json
[
  {
    "id": 100,
    "status": "PLACED",
    "totalPrice": 24000,
    "createdAt": "2026-02-06T14:00:00"
  }
]
```

## 판매자 주문 처리
### GET /api/seller/orders
- 설명: 판매자가 조회 가능한 주문 목록
- 응답
```json
[
  {
    "id": 100,
    "status": "PLACED",
    "totalPrice": 24000,
    "createdAt": "2026-02-06T14:00:00"
  }
]
```

### PATCH /api/seller/orders/{id}/status
- 설명: 주문 상태 변경
- 요청
```json
{
  "status": "SHIPPING"
}
```
- 응답
```json
{
  "id": 100,
  "status": "SHIPPING",
  "totalPrice": 24000,
  "createdAt": "2026-02-06T14:00:00"
}
```

## 상태값
- 주문 상태: `PLACED | READY_TO_SHIP | SHIPPING | DELIVERED | CANCELED`
- 상품 상태: `ACTIVE | INACTIVE`
- 사용자 역할: `SELLER | BUYER`

