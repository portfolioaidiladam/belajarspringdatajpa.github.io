CREATE DATABASE belajar_spring_data_jpa;

USE belajar_spring_data_jpa;

CREATE TABLE categories
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    created_date datetime NULL,
    last_modified_date datetime NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE categories
ADD COLUMN created_date DATETIME;
ALTER TABLE categories
ADD COLUMN last_modified_date DATETIME;


TRUNCATE TABLE categories;
DROP TABLE categories;

SELECT *
FROM categories;

desc categories;

CREATE TABLE products
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    price       BIGINT       NOT NULL,
    category_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY fk_products_categories (category_id) REFERENCES categories (id)
) ENGINE InnoDB;

SELECT *
FROM products;

DELETE
FROM products
WHERE name = 'Samsung Galaxy S9';

ALTER TABLE categories
    ADD COLUMN created_date TIMESTAMP;

ALTER TABLE categories
    ADD COLUMN last_modified_date TIMESTAMP;

SELECT * FROM categories;
