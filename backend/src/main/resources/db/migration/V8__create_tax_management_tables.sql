CREATE TABLE tax
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(30)  NOT NULL UNIQUE,
    name             VARCHAR(100) NOT NULL,
    description      VARCHAR(500),
    classification   VARCHAR(40),
    calculation_type VARCHAR(30)  NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME
);

CREATE TABLE tax_rate
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tax_id      BIGINT         NOT NULL,
    value       DECIMAL(12, 4) NOT NULL,
    valid_from  DATE           NOT NULL,
    valid_until DATE,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tax_rate_tax
        FOREIGN KEY (tax_id)
            REFERENCES tax (id),

    CONSTRAINT chk_tax_rate_value
        CHECK (value >= 0),

    CONSTRAINT chk_tax_rate_validity
        CHECK (valid_until IS NULL OR valid_until >= valid_from)
);

CREATE INDEX idx_tax_rate_tax_validity
    ON tax_rate (tax_id, valid_from, valid_until);

CREATE TABLE tax_assignment
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tax_id      BIGINT      NOT NULL,
    scope       VARCHAR(20) NOT NULL,
    product_id  BIGINT,
    category_id BIGINT,
    operation   VARCHAR(20) NOT NULL,
    effect      VARCHAR(20) NOT NULL,
    priority    INT         NOT NULL DEFAULT 0,
    active      BOOLEAN     NOT NULL DEFAULT TRUE,
    valid_from  DATE,
    valid_until DATE,

    CONSTRAINT fk_tax_assignment_tax
        FOREIGN KEY (tax_id)
            REFERENCES tax (id),

    CONSTRAINT fk_tax_assignment_product
        FOREIGN KEY (product_id)
            REFERENCES product (id),

    CONSTRAINT fk_tax_assignment_category
        FOREIGN KEY (category_id)
            REFERENCES category (id),

    CONSTRAINT chk_tax_assignment_scope
        CHECK (
            (scope = 'GLOBAL'
                AND product_id IS NULL
                AND category_id IS NULL)
                OR
            (scope = 'CATEGORY'
                AND product_id IS NULL
                AND category_id IS NOT NULL)
                OR
            (scope = 'PRODUCT'
                AND product_id IS NOT NULL
                AND category_id IS NULL)
            ),

    CONSTRAINT chk_tax_assignment_validity
        CHECK (
            valid_until IS NULL
                OR valid_from IS NULL
                OR valid_until >= valid_from
            )
);

CREATE INDEX idx_tax_assignment_tax
    ON tax_assignment (tax_id);

CREATE INDEX idx_tax_assignment_product
    ON tax_assignment (product_id);

CREATE INDEX idx_tax_assignment_category
    ON tax_assignment (category_id);