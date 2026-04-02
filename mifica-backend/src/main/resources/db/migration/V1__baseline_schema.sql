-- Baseline de schema para produção com Flyway
-- Banco alvo: MySQL 8+

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    enabled BIT(1) NOT NULL,
    reputacao INT,
    nivel VARCHAR(40),
    role VARCHAR(30) NOT NULL,
    data_nascimento DATE,
    telefone VARCHAR(30),
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);


CREATE TABLE IF NOT EXISTS usuario_conquistas (
    usuario_id BIGINT NOT NULL,
    conquista VARCHAR(255),
    CONSTRAINT fk_usuario_conquista_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS contrato (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(500),
    endereco_blockchain VARCHAR(255),
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS transacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    remetente VARCHAR(120) NOT NULL,
    destinatario VARCHAR(120) NOT NULL,
    valor DOUBLE NOT NULL,
    data_transacao DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS desafio_gamificado (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    pontos INT NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS badge (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS solicitacao_credito (
    id BIGINT NOT NULL AUTO_INCREMENT,
    valor_solicitado DECIMAL(15,2) NOT NULL,
    descricao VARCHAR(500),
    prazo_pagamento DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    data_solicitacao DATETIME(6) NOT NULL,
    usuario_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_solicitacao_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);


CREATE TABLE IF NOT EXISTS avaliacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nota INT NOT NULL,
    comentario VARCHAR(500),
    data DATETIME(6) NOT NULL,
    avaliador_id BIGINT,
    avaliado_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_avaliacao_avaliador
        FOREIGN KEY (avaliador_id) REFERENCES usuarios(id),
    CONSTRAINT fk_avaliacao_avaliado
        FOREIGN KEY (avaliado_id) REFERENCES usuarios(id)
);


CREATE TABLE IF NOT EXISTS historico_reputacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email_usuario VARCHAR(180) NOT NULL,
    reputacao_anterior INT,
    reputacao_nova INT,
    data_alteracao DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS gamification_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    points INT,
    level INT,
    PRIMARY KEY (id)
);


-- Envers: auditoria de entidades críticas
CREATE TABLE IF NOT EXISTS revinfo (
    rev INT NOT NULL AUTO_INCREMENT,
    revtstmp BIGINT,
    PRIMARY KEY (rev)
);

CREATE TABLE IF NOT EXISTS contrato_aud (
    id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype TINYINT,
    nome VARCHAR(120),
    descricao VARCHAR(500),
    endereco_blockchain VARCHAR(255),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_contrato_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE IF NOT EXISTS transacao_aud (
    id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype TINYINT,
    remetente VARCHAR(120),
    destinatario VARCHAR(120),
    valor DOUBLE,
    data_transacao DATETIME(6),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_transacao_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE IF NOT EXISTS desafio_gamificado_aud (
    id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype TINYINT,
    titulo VARCHAR(150),
    descricao VARCHAR(500),
    pontos INT,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_desafio_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);
