-- table: roles
CREATE TABLE role
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- por defecto
INSERT INTO role (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER');

-- table: sucursal
CREATE TABLE sucursal
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone   VARCHAR(30),
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);


-- table: user
CREATE TABLE user
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    complete_name VARCHAR(100) NOT NULL,
    role_id       BIGINT       NOT NULL,
    sucursal_id   BIGINT,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_user_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal (id) ON DELETE SET NULL
);

-- table: category
CREATE TABLE category
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    active      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- table: supplier
CREATE TABLE supplier
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    cellphone VARCHAR(100),
    phone     VARCHAR(30),
    email     VARCHAR(100),
    note      TEXT,
    active    BOOLEAN      NOT NULL DEFAULT TRUE
);

-- table: product
CREATE TABLE product
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    barcode        VARCHAR(50) UNIQUE,
    name           VARCHAR(100)   NOT NULL,
    description    TEXT,
    purchase_price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    selling_price  DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    minimum_stock  INT            NOT NULL DEFAULT 0,
    category_id    BIGINT,
    supplier_id    BIGINT,
    imagen_url     VARCHAR(255),
    active         BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES supplier (id) ON DELETE SET NULL
);

-- table: stock_sucursal
CREATE TABLE stock_sucursal
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id  BIGINT NOT NULL,
    sucursal_id BIGINT NOT NULL,
    amount      INT    NOT NULL DEFAULT 0,
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal (id) ON DELETE CASCADE,
    CONSTRAINT uk_product_sucursal UNIQUE (product_id, sucursal_id)
);