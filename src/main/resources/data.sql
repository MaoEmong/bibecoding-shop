-- Users (password_hash is placeholder; use signup for real login)
INSERT INTO users (id, email, password_hash, name, role, created_at, updated_at) VALUES
  (1, 'seller1@example.com', 'DUMMY_HASH', '판매자1', 'SELLER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'seller2@example.com', 'DUMMY_HASH', '판매자2', 'SELLER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'buyer1@example.com',  'DUMMY_HASH', '구매자1', 'BUYER',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 'buyer2@example.com',  'DUMMY_HASH', '구매자2', 'BUYER',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Products
INSERT INTO products (id, seller_id, name, description, price, stock_quantity, status, created_at, updated_at) VALUES
  (1, 1, '에코백', '심플한 에코백', 5000, 20, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, '머그컵', '보온/보냉 머그컵', 12000, 15, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 2, '키링', '데일리 키링', 8000, 30, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 2, '스티커', '감성 스티커', 3000, 50, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Carts (buyers)
INSERT INTO carts (id, buyer_id, created_at, updated_at) VALUES
  (1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cart items
INSERT INTO cart_items (id, cart_id, product_id, quantity, created_at, updated_at) VALUES
  (1, 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 2, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Orders
INSERT INTO orders (id, buyer_id, status, total_price, created_at, updated_at) VALUES
  (1, 3, 'PLACED', 18000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 4, 'READY_TO_SHIP', 12000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Order items
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, created_at, updated_at) VALUES
  (1, 1, 1, 2, 5000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, 3, 1, 8000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 2, 2, 1, 12000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);