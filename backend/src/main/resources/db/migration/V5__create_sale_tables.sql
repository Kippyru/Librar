-- table: venta
CREATE TABLE venta
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    sucursal_id      BIGINT         NOT NULL,
    cash_register_id BIGINT         NOT NULL,
    total_amount     DECIMAL(12, 2) NOT NULL,
    payment_method   ENUM('EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'MIXTO') NOT NULL,
    status           ENUM('COMPLETED', 'CANCELED') NOT NULL DEFAULT 'COMPLETED',
    created_at       DATETIME       NOT NULL,
    CONSTRAINT fk_venta_user FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_venta_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal (id),
    CONSTRAINT fk_venta_cash_register FOREIGN KEY (cash_register_id) REFERENCES cash_register (id)
);

-- table: detalle_venta
CREATE TABLE detalle_venta
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id   BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INT            NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal   DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (venta_id) REFERENCES venta (id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_venta_product FOREIGN KEY (product_id) REFERENCES product (id)
);