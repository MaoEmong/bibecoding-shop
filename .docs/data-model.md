# 데이터 모델 설계

## 개요
- 사용자 유형은 판매자/구매자로 구분됩니다.
- MVP 기준 기능(회원/상품/장바구니/주문/배송) 중심으로 설계합니다.

## 공통 규칙
- PK는 `id`(BIGINT, AUTO INCREMENT)
- 시간 컬럼은 `created_at`, `updated_at` (TIMESTAMP)
- 모든 금액은 원화 기준 `price`(INT)로 저장
- 문자열 길이는 기본 255, 이메일은 320

## 엔티티 상세

### User
- id (PK)
- email (VARCHAR(320), UNIQUE, NOT NULL)
- password_hash (VARCHAR(255), NOT NULL)
- name (VARCHAR(100), NOT NULL)
- role (ENUM: SELLER | BUYER, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)

인덱스
- UNIQUE(email)

### Product
- id (PK)
- seller_id (FK -> User.id, NOT NULL)
- name (VARCHAR(200), NOT NULL)
- description (TEXT, NOT NULL)
- price (INT, NOT NULL)
- stock_quantity (INT, NOT NULL)
- status (ENUM: ACTIVE | INACTIVE, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)

제약
- price >= 0
- stock_quantity >= 0

인덱스
- IDX_product_seller_id
- IDX_product_status
- (name) 검색용 인덱스 고려

### Cart
- id (PK)
- buyer_id (FK -> User.id, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)

제약
- UNIQUE(buyer_id)  // 구매자당 1개 장바구니

인덱스
- UNIQUE(buyer_id)

### CartItem
- id (PK)
- cart_id (FK -> Cart.id, NOT NULL)
- product_id (FK -> Product.id, NOT NULL)
- quantity (INT, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)

제약
- quantity >= 1
- UNIQUE(cart_id, product_id)  // 동일 상품 중복 담기 방지

인덱스
- IDX_cart_item_cart_id
- IDX_cart_item_product_id

### Order
- id (PK)
- buyer_id (FK -> User.id, NOT NULL)
- status (ENUM: PLACED | READY_TO_SHIP | SHIPPING | DELIVERED | CANCELED, NOT NULL)
- total_price (INT, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)

제약
- total_price >= 0

인덱스
- IDX_order_buyer_id
- IDX_order_status

### OrderItem
- id (PK)
- order_id (FK -> Order.id, NOT NULL)
- product_id (FK -> Product.id, NOT NULL)
- quantity (INT, NOT NULL)
- unit_price (INT, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)

제약
- quantity >= 1
- unit_price >= 0

인덱스
- IDX_order_item_order_id
- IDX_order_item_product_id

## 관계
- User(SELLER) 1:N Product
- User(BUYER) 1:1 Cart
- Cart 1:N CartItem
- User(BUYER) 1:N Order
- Order 1:N OrderItem
- Product 1:N OrderItem

## MVP 스코프 비고
- 배송 정보는 주문 상태로 대체하고, 상세 주소는 다음 단계에서 추가합니다.
- 리뷰/문의/찜 기능은 다음 단계에서 설계합니다.
