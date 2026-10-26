CREATE TABLE sale_payment
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    sale_id        BIGINT         NOT NULL,
    payment_method VARCHAR(20)    NOT NULL,
    amount         DECIMAL(12, 2) NOT NULL,

    CONSTRAINT pk_sale_payment
        PRIMARY KEY (id),

    CONSTRAINT fk_sale_payment_sale
        FOREIGN KEY (sale_id)
            REFERENCES sale (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_sale_payment_amount
        CHECK (amount > 0),

    CONSTRAINT chk_sale_payment_method
        CHECK (payment_method IN (
                                  'EFECTIVO',
                                  'TRANSFERENCIA',
                                  'TARJETA'
            ))
);

CREATE INDEX idx_sale_payment_sale_id
    ON sale_payment (sale_id);
