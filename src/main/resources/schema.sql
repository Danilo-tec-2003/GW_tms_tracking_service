-- ==============================================
-- Script SQL (DDL) - Sistema de Rastreamento TMS
-- Banco: MySQL
-- ==============================================

-- Tabela: tb_order
CREATE TABLE tb_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_code VARCHAR(255) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL
);

-- Tabela: tb_occurrence
CREATE TABLE tb_occurrence (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    occurrence_timestamp DATETIME NOT NULL,
    order_id BIGINT NOT NULL,
    CONSTRAINT fk_occurrence_order FOREIGN KEY (order_id)
        REFERENCES tb_order (id)
        ON DELETE CASCADE
);
