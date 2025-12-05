-- V1__create_TOP_UP_BALANCE_tables.sql

-- ========================================
-- Tabla principal: Transacción de balance
-- ========================================
CREATE TABLE TOP_UP_BALANCE (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR2(100) NOT NULL,
    confirmation_id VARCHAR2(100),
    status VARCHAR2(100) NOT NULL,
    reason VARCHAR2(100),
    code VARCHAR2(100),
    amount NUMERIC(18,6) NOT NULL,
    currency VARCHAR2(10) NOT NULL,
    LOGICAL_RESOURCE_id BIGINT NOT NULL,
    CHANNEL_id BIGINT NOT NULL,
    REQUESTOR_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    created_by VARCHAR2(100) DEFAULT 'SYSTEM', -- //NOSONAR
    last_modified_at TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR2(100) DEFAULT 'SYSTEM' -- //NOSONAR
);

COMMENT ON TABLE TOP_UP_BALANCE IS 'Representa una transacción que afecta el saldo del cliente, como recargas o consumos.';
COMMENT ON COLUMN TOP_UP_BALANCE.id IS 'Identificador único de la transacción.';
COMMENT ON COLUMN TOP_UP_BALANCE.external_id IS 'Identificador externo de la transacción (por ejemplo, de sistemas externos).';
COMMENT ON COLUMN TOP_UP_BALANCE.confirmation_id IS 'Identificador externo de la recarga (por ejemplo, de Top Up Hub).';
COMMENT ON COLUMN TOP_UP_BALANCE.status IS 'Estado de la transacción (por ejemplo, create, cancelled).';
COMMENT ON COLUMN TOP_UP_BALANCE.reason IS 'Descripción del error o motivo de la transacción (por ejemplo, insufficient balance).';
COMMENT ON COLUMN TOP_UP_BALANCE.code IS 'Código de error';
COMMENT ON COLUMN TOP_UP_BALANCE.amount IS 'Monto afectado en la transacción.';
COMMENT ON COLUMN TOP_UP_BALANCE.currency IS 'Moneda del monto (por ejemplo, MXN, USD).';
COMMENT ON COLUMN TOP_UP_BALANCE.LOGICAL_RESOURCE_id IS 'Recurso lógico al que aplica (ej. MSISDN).';
COMMENT ON COLUMN TOP_UP_BALANCE.CHANNEL_id IS 'Canal a través del cual se originó la transacción.';
COMMENT ON COLUMN TOP_UP_BALANCE.REQUESTOR_id IS 'Solicitante de la transacción.';
COMMENT ON COLUMN TOP_UP_BALANCE.created_at IS 'Marca de tiempo de creación de la transacción.';
COMMENT ON COLUMN TOP_UP_BALANCE.created_by IS 'Usuario o sistema que creó el registro.';
COMMENT ON COLUMN TOP_UP_BALANCE.last_modified_at IS 'Marca de tiempo de la última modificación.';
COMMENT ON COLUMN TOP_UP_BALANCE.last_modified_by IS 'Usuario o sistema que realizó la última modificación.';

-- =========================
-- Tabla: Canal
-- =========================
CREATE TABLE CHANNEL (
    id BIGSERIAL PRIMARY KEY,
--    name VARCHAR2(100) NOT NULL,
    external_id VARCHAR2(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    created_by VARCHAR2(100) DEFAULT 'SYSTEM', -- //NOSONAR
    last_modified_at TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR2(100) DEFAULT 'SYSTEM' -- //NOSONAR
);

COMMENT ON TABLE CHANNEL IS 'Canal de distribución o punto de interacción donde se ejecuta la transacción.';
COMMENT ON COLUMN CHANNEL.id IS 'Identificador único del canal.';
--COMMENT ON COLUMN CHANNEL.name IS 'Nombre del canal.';
COMMENT ON COLUMN CHANNEL.external_id IS 'Identificador heredado o funcional del canal (ej. B012).';
COMMENT ON COLUMN CHANNEL.created_at IS 'Marca de tiempo de creación del canal.';
COMMENT ON COLUMN CHANNEL.created_by IS 'Usuario o sistema que creó el canal.';
COMMENT ON COLUMN CHANNEL.last_modified_at IS 'Marca de tiempo de la última modificación del canal.';
COMMENT ON COLUMN CHANNEL.last_modified_by IS 'Usuario o sistema que realizó la última modificación del canal.';

-- =========================
-- Tabla: Recurso lógico
-- =========================
CREATE TABLE LOGICAL_RESOURCE (
    id BIGSERIAL PRIMARY KEY,
--    name VARCHAR2(100) NOT NULL,
    type VARCHAR2(50) NOT NULL,
    value_ VARCHAR2(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT now(),
    created_by VARCHAR2(100) DEFAULT 'SYSTEM', -- //NOSONAR
    last_modified_at TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR2(100) DEFAULT 'SYSTEM' -- //NOSONAR
);

COMMENT ON TABLE LOGICAL_RESOURCE IS 'Recurso lógico sobre el que se realiza la operación, por ejemplo, un MSISDN.';
COMMENT ON COLUMN LOGICAL_RESOURCE.id IS 'Identificador único del recurso.';
--COMMENT ON COLUMN LOGICAL_RESOURCE.name IS 'Nombre del recurso.';
COMMENT ON COLUMN LOGICAL_RESOURCE.type IS 'Tipo de recurso lógico (ej. MSISDN, ICCID, etc.).';
COMMENT ON COLUMN LOGICAL_RESOURCE.value_ IS 'Valor del recurso lógico (ej. número telefónico).';
COMMENT ON COLUMN LOGICAL_RESOURCE.created_at IS 'Marca de tiempo de creación del recurso.';
COMMENT ON COLUMN LOGICAL_RESOURCE.created_by IS 'Usuario o sistema que creó el recurso.';
COMMENT ON COLUMN LOGICAL_RESOURCE.last_modified_at IS 'Marca de tiempo de la última modificación del recurso.';
COMMENT ON COLUMN LOGICAL_RESOURCE.last_modified_by IS 'Usuario o sistema que realizó la última modificación del recurso.';

-- =========================
-- Tabla: Solicitante
-- =========================
CREATE TABLE REQUESTOR (
    id BIGSERIAL PRIMARY KEY,
--    name VARCHAR2(100) NOT NULL,
    external_id VARCHAR2(50) UNIQUE NOT NULL,
    role VARCHAR2(50),
    created_at TIMESTAMP DEFAULT now(),
    created_by VARCHAR2(100) DEFAULT 'SYSTEM', -- //NOSONAR
    last_modified_at TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR2(100) DEFAULT 'SYSTEM' -- //NOSONAR
);

COMMENT ON TABLE REQUESTOR IS 'Entidad que solicita la transacción, por ejemplo, un punto de venta o sistema externo.';
COMMENT ON COLUMN REQUESTOR.id IS 'Identificador único del solicitante.';
--COMMENT ON COLUMN REQUESTOR.name IS 'Nombre del solicitante.';
COMMENT ON COLUMN REQUESTOR.external_id IS 'Identificador heredado o funcional del solicitante (ej. ID de POS).';
COMMENT ON COLUMN REQUESTOR.role IS 'Rol funcional del solicitante (ej. point-of-sales, distributor).';
COMMENT ON COLUMN REQUESTOR.created_at IS 'Marca de tiempo de creación del solicitante.';
COMMENT ON COLUMN REQUESTOR.created_by IS 'Usuario o sistema que creó el solicitante.';
COMMENT ON COLUMN REQUESTOR.last_modified_at IS 'Marca de tiempo de la última modificación del solicitante.';
COMMENT ON COLUMN REQUESTOR.last_modified_by IS 'Usuario o sistema que realizó la última modificación del solicitante.';

-- =========================
-- Relaciones
-- =========================
ALTER TABLE TOP_UP_BALANCE
    ADD CONSTRAINT fk_LOGICAL_RESOURCE
    FOREIGN KEY (LOGICAL_RESOURCE_id)
    REFERENCES LOGICAL_RESOURCE (id);

ALTER TABLE TOP_UP_BALANCE
    ADD CONSTRAINT fk_CHANNEL
    FOREIGN KEY (CHANNEL_id)
    REFERENCES CHANNEL (id);

ALTER TABLE TOP_UP_BALANCE
    ADD CONSTRAINT fk_REQUESTOR
    FOREIGN KEY (REQUESTOR_id)
    REFERENCES REQUESTOR (id);
