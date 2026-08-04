ALTER TABLE purchase_requests
    ADD CONSTRAINT uk_purchase_request_workspace_id UNIQUE (workspace_id, id);

ALTER TABLE purchase_orders
    DROP CONSTRAINT IF EXISTS purchase_orders_purchase_request_id_fkey;

-- 애플리케이션의 scoped lookup을 우회하는 결함이 생겨도 발주와 원 요청이
-- 서로 다른 workspace를 참조하는 row는 PostgreSQL이 최종 거부한다.
ALTER TABLE purchase_orders
    ADD CONSTRAINT fk_purchase_order_workspace_request
    FOREIGN KEY (workspace_id, purchase_request_id)
    REFERENCES purchase_requests (workspace_id, id)
    ON DELETE CASCADE;
