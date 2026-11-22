-- =========================================================================
-- PROJETO PRÁTICO SAEP 2026 - ARENAXP
-- Script de criação do banco de dados, tabelas e carga inicial
-- =========================================================================

DROP DATABASE IF EXISTS arenaxp_db;
CREATE DATABASE arenaxp_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE arenaxp_db;

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE equipamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_patrimonio VARCHAR(30) NOT NULL UNIQUE,
    nome VARCHAR(120) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    especificacao_tecnica VARCHAR(255) NOT NULL,
    quantidade_atual INT NOT NULL DEFAULT 0,
    estoque_minimo INT NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE movimentacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimentacao VARCHAR(15) NOT NULL,
    quantidade INT NOT NULL,
    data_hora DATETIME NOT NULL,
    observacao_evento VARCHAR(255),
    equipamento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_movimentacao_equipamento
        FOREIGN KEY (equipamento_id) REFERENCES equipamentos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movimentacao_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Credenciais de teste (senhas armazenadas em SHA-256):
-- admin@arenaxp.com     -> ArenaXP@2026#Admin
-- operador1@arenaxp.com -> Operador@2026
-- operador2@arenaxp.com -> Operador@2026
INSERT INTO usuarios (id, nome, email, senha, perfil) VALUES
(1, 'Administrador ArenaXP', 'admin@arenaxp.com', '08333b66ad8f303f04d5628cd81b509d4bef776c5ce9e3e9a4af06defb30a400', 'ADMIN'),
(2, 'Lucas Almeida (Operador)', 'operador1@arenaxp.com', '309ad0abc2de2493f4506d3dc2f8b6fd7c4ac977c44c352359e5538e25944397', 'OPERADOR'),
(3, 'Mariana Costa (Operador)', 'operador2@arenaxp.com', '309ad0abc2de2493f4506d3dc2f8b6fd7c4ac977c44c352359e5538e25944397', 'OPERADOR');

INSERT INTO equipamentos (id, codigo_patrimonio, nome, categoria, especificacao_tecnica, quantidade_atual, estoque_minimo) VALUES
(1, 'PAT-001', 'Headset Gamer', 'Periféricos', 'Surround 7.1 USB, Microfone com cancelamento de ruído', 15, 3),
(2, 'PAT-002', 'Monitor 24"', 'Monitores', 'Painel IPS Full HD 165Hz, Tempo de resposta 1ms', 5, 2),
(3, 'PAT-003', 'Mouse Gamer', 'Periféricos', 'Sensor óptico 16000 DPI, 6 botões programáveis', 7, 3),
(4, 'PAT-004', 'Notebook Gamer', 'Computadores', 'Intel Core i7, 16GB RAM, SSD 512GB, RTX 4060', 4, 1),
(5, 'PAT-005', 'Controle Xbox', 'Controles', 'Wireless Series X/S Preto com cabo USB-C', 6, 2);

INSERT INTO movimentacoes (id, tipo_movimentacao, quantidade, data_hora, observacao_evento, equipamento_id, usuario_id) VALUES
(1, 'ENTRADA', 5, '2026-11-20 09:30:00', 'Reposição de estoque de periféricos para torneio', 3, 1),
(2, 'SAIDA', 1, '2026-11-20 14:15:00', 'Substituição na bancada de transmissão Arena 01', 2, 2),
(3, 'ENTRADA', 3, '2026-11-21 10:00:00', 'Compra lote adicional de headsets', 1, 1),
(4, 'SAIDA', 2, '2026-11-21 16:45:00', 'Empréstimo para etapa classificatória console', 5, 3);
