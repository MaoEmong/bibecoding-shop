# API 설계 (MVP)

기본 규칙
- 인증 필요: 로그인이 필요한 API는 Authorization 헤더 사용
- 역할: SELLER/BUYER 권한 확인
- 모든 시간은 ISO-8601 문자열 사용(예: 2026-02-05T17:30:00Z)

## 공통 응답 형태
- 성공: `{ "success": true, "data": ... }`
- 실패: `{ "success": false, "error": { "code": "...", "message": "..." } }`

## 인증
### POST /api/auth/signup
- 설명: 회원가입(판매자/구매자)
- 요청
```
{
  "email": "buyer@example.com",
  "password": "string",
  "name": "홍길동",
  "role": "BUYER"
}
```
- 응답
```
{
  "id": 1,
  "email": "buyer@example.com",
  "name": "홍길동",
  "role": "BUYER",
  "createdAt": "2026-02-05T17:30:00Z"
}
```

### POST /api/auth/login
- 설명: 로그인
- 요청
```
{
  "email": "buyer@example.com",
  "password": "string"
}
```
- 응답
```
{
  "accessToken": "jwt-token",
  "role": "BUYER"
}
```

### POST /api/auth/logout
- 설명: 로그아웃
- 요청: 없음
- 응답
```
{ "message": "ok" }
```

## 상품
### GET /api/products
- 설명: 상품 목록/검색/필터
- 쿼리: `q`, `minPrice`, `maxPrice`, `page`, `size`
- 응답
```
[
  {
    "id": 10,
    "name": "상품명",
    "price": 12000,
    "stockQuantity": 5,
    "status": "ACTIVE"
  }
]
```

### GET /api/products/{id}
- 설명: 상품 상세
- 응답
```
{
  "id": 10,
  "sellerId": 3,
  "name": "상품명",
  "description": "상품 설명",
  "price": 12000,
  "stockQuantity": 5,
  "status": "ACTIVE"
}
```

### POST /api/seller/products
- 설명: 상품 등록(판매자)
- 요청
```
{
  "name": "상품명",
  "description": "상품 설명",
  "price": 12000,
  "stockQuantity": 10
}
```
- 응답
```
{
  "id": 10,
  "name": "상품명",
  "description": "상품 설명",
  "price": 12000,
  "stockQuantity": 10,
  "status": "ACTIVE",
  "createdAt": "2026-02-05T17:30:00Z"
}
```

## 장바구니 (구매자)
### GET /api/cart
- 설명: 장바구니 조회
- 응답
```
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
```
{
  "productId": 10,
  "quantity": 2
}
```
- 응답
```
{
  "id": 1,
  "productId": 10,
  "quantity": 2
}
```

### PATCH /api/cart/items/{id}
- 설명: 장바구니 수량 변경
- 요청
```
{
  "quantity": 3
}
```
- 응답
```
{
  "id": 1,
  "productId": 10,
  "quantity": 3
}
```

### DELETE /api/cart/items/{id}
- 설명: 장바구니 삭제
- 응답
```
{ "message": "ok" }
```

## 주문 (구매자)
### POST /api/orders
- 설명: 주문 생성(구매)
- 요청
```
{
  "items": [
    { "productId": 10, "quantity": 2 }
  ]
}
```
- 응답
```
{
  "id": 100,
  "status": "PLACED",
  "totalPrice": 24000,
  "createdAt": "2026-02-05T17:30:00Z"
}
```

### GET /api/orders
- 설명: 주문 내역 조회
- 응답
```
[
  {
    "id": 100,
    "status": "PLACED",
    "totalPrice": 24000,
    "createdAt": "2026-02-05T17:30:00Z"
  }
]
```

## 판매자 주문 처리
### GET /api/seller/orders
- 설명: 판매자 주문 내역
- 응답
```
[
  {
    "id": 100,
    "buyerId": 5,
    "status": "PLACED",
    "totalPrice": 24000,
    "createdAt": "2026-02-05T17:30:00Z"
  }
]
```

### PATCH /api/seller/orders/{id}/status
- 설명: 주문 상태 변경(배송 준비/배송 중/배송 완료)
- 요청
```
{
  "status": "SHIPPING"
}
```
- 응답
```
{
  "id": 100,
  "status": "SHIPPING",
  "updatedAt": "2026-02-05T17:30:00Z"
}
```

## 상태값
- 주문 상태: PLACED | READY_TO_SHIP | SHIPPING | DELIVERED | CANCELED
- 상품 상태: ACTIVE | INACTIVE
- 사용자 역할: SELLER | BUYER
