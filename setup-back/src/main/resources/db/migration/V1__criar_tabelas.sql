-- V1: estrutura do banco (diagrama de classes).
-- Nunca altere uma migração já executada: crie V3__..., V4__... com as mudanças.

-- Pessoa -> Usuário / Admin  (o "papel" diferencia os dois)
CREATE TABLE usuario (
    id          BIGSERIAL    PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    email       VARCHAR(160) NOT NULL UNIQUE,
    senha_hash  VARCHAR(100) NOT NULL,
    papel       VARCHAR(20)  NOT NULL DEFAULT 'USUARIO' CHECK (papel IN ('USUARIO', 'ADMIN')),
    criado_em   TIMESTAMP    NOT NULL DEFAULT now()
);

-- Componente e suas especializações (PlacaMae, Processador, MemoriaRAM, PlacadeVideo,
-- Armazenamento, Fonte, Gabinete) numa tabela só: a coluna "categoria" diz qual é a peça
-- (herança SINGLE_TABLE do JPA). Colunas que não se aplicam à categoria ficam NULL.
CREATE TABLE componente (
    id              BIGSERIAL     PRIMARY KEY,
    categoria       VARCHAR(20)   NOT NULL
        CHECK (categoria IN ('placa', 'cpu', 'ram', 'gpu', 'armazenamento', 'fonte', 'gabinete')),
    marca           VARCHAR(60)   NOT NULL,
    modelo          VARCHAR(120)  NOT NULL,
    descricao       VARCHAR(255)  NOT NULL,
    preco           NUMERIC(10,2) NOT NULL CHECK (preco > 0),

    socket          VARCHAR(20),   -- placa, cpu
    tipo_memoria    VARCHAR(10),   -- placa (DDR4/DDR5)
    formato         VARCHAR(10),   -- placa (mATX/ATX/ITX)
    slots_ram       INTEGER,       -- placa
    ram_max_mhz     INTEGER,       -- placa
    slots_m2        INTEGER,       -- placa
    tipo            VARCHAR(10),   -- ram (DDR4/DDR5), armazenamento (M.2/SATA)
    frequencia_mhz  INTEGER,       -- ram
    tdp_w           INTEGER,       -- cpu, ram (por pente), gpu, armazenamento
    comprimento_mm  INTEGER,       -- gpu
    potencia_w      INTEGER,       -- fonte
    formatos        VARCHAR(60),   -- gabinete (ex.: "mATX,ATX")
    max_gpu_mm      INTEGER        -- gabinete
);
CREATE INDEX idx_componente_categoria ON componente (categoria);

-- Setup: pertence a um usuário (Usuário 1 -> 0..* Setup)
CREATE TABLE setup (
    id             BIGSERIAL     PRIMARY KEY,
    nome           VARCHAR(120)  NOT NULL,
    usuario_id     BIGINT        NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    preco_total    NUMERIC(12,2) NOT NULL,
    consumo_w      INTEGER       NOT NULL,
    recomendada_w  INTEGER       NOT NULL,
    criado_em      TIMESTAMP     NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_setup_usuario ON setup (usuario_id);

-- Setup 1..* <-> 0..* Componente, com quantidade (ex.: 2 pentes de RAM)
CREATE TABLE setup_item (
    id             BIGSERIAL PRIMARY KEY,
    setup_id       BIGINT    NOT NULL REFERENCES setup (id) ON DELETE CASCADE,
    componente_id  BIGINT    NOT NULL REFERENCES componente (id),
    quantidade     INTEGER   NOT NULL DEFAULT 1 CHECK (quantidade > 0)
);
CREATE INDEX idx_setup_item_setup ON setup_item (setup_id);
