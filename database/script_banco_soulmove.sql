-- ============================================================
-- SOULMOVE - SCRIPT DE CRIACAO DO BANCO DE DADOS (ORACLE)
-- Equipe Un-glitch Tech - FIAP - Sprint 4
-- Executar no schema do aluno (oracle.fiap.com.br)
-- ============================================================

-- ------------------------------------------------------------
-- Limpeza (ignora erros se os objetos ainda nao existirem)
-- ------------------------------------------------------------
-- DROP TABLE TB_SOULMOVE_VIAGEM;
-- DROP TABLE TB_SOULMOVE_TRANSPORTE;
-- DROP TABLE TB_SOULMOVE_USUARIO;
-- DROP SEQUENCE SQ_SOULMOVE_USUARIO;
-- DROP SEQUENCE SQ_SOULMOVE_TRANSPORTE;
-- DROP SEQUENCE SQ_SOULMOVE_VIAGEM;

-- ------------------------------------------------------------
-- Tabela de usuarios do programa
-- ------------------------------------------------------------
CREATE TABLE TB_SOULMOVE_USUARIO (
    ID_USUARIO       NUMBER(9)      NOT NULL,
    NM_USUARIO       VARCHAR2(100)  NOT NULL,
    NR_CPF           VARCHAR2(14)   NOT NULL,
    DS_EMAIL         VARCHAR2(100)  NOT NULL,
    NR_BILHETE       VARCHAR2(20)   NOT NULL,
    ST_ATIVO         NUMBER(1)      DEFAULT 1 NOT NULL,
    NR_SALDO_PONTOS  NUMBER(9)      DEFAULT 0 NOT NULL,
    CONSTRAINT PK_SOULMOVE_USUARIO PRIMARY KEY (ID_USUARIO),
    CONSTRAINT UN_SOULMOVE_USUARIO_CPF UNIQUE (NR_CPF),
    CONSTRAINT CK_SOULMOVE_USUARIO_ATIVO CHECK (ST_ATIVO IN (0, 1))
);

-- ------------------------------------------------------------
-- Tabela de meios de transporte (Onibus/Metro em tabela unica,
-- diferenciados pela coluna discriminadora TP_MODAL)
-- ------------------------------------------------------------
CREATE TABLE TB_SOULMOVE_TRANSPORTE (
    ID_TRANSPORTE    NUMBER(9)      NOT NULL,
    TP_MODAL         VARCHAR2(10)   NOT NULL,
    DS_IDENTIFICACAO VARCHAR2(60)   NOT NULL,
    DS_COMBUSTIVEL   VARCHAR2(30)   NOT NULL,
    VL_FATOR_EMISSAO NUMBER(8,4)    NOT NULL,
    VL_CONSUMO       NUMBER(8,4)    NOT NULL,
    ST_PARTICIPA     NUMBER(1)      DEFAULT 1 NOT NULL,
    CONSTRAINT PK_SOULMOVE_TRANSPORTE PRIMARY KEY (ID_TRANSPORTE),
    CONSTRAINT CK_SOULMOVE_TRANSPORTE_MODAL CHECK (TP_MODAL IN ('ONIBUS', 'METRO')),
    CONSTRAINT CK_SOULMOVE_TRANSPORTE_PART CHECK (ST_PARTICIPA IN (0, 1))
);

-- ------------------------------------------------------------
-- Tabela de viagens (entidade central do dominio)
-- ------------------------------------------------------------
CREATE TABLE TB_SOULMOVE_VIAGEM (
    ID_VIAGEM        NUMBER(9)      NOT NULL,
    ID_USUARIO       NUMBER(9)      NOT NULL,
    ID_TRANSPORTE    NUMBER(9)      NOT NULL,
    VL_DISTANCIA_KM  NUMBER(8,2)    NOT NULL,
    NR_DURACAO_MIN   NUMBER(5)      NOT NULL,
    DT_VIAGEM        DATE           NOT NULL,
    VL_CO2_EVITADO   NUMBER(10,4)   DEFAULT 0 NOT NULL,
    NR_PONTOS        NUMBER(9)      DEFAULT 0 NOT NULL,
    ST_VALIDADA      NUMBER(1)      DEFAULT 0 NOT NULL,
    CONSTRAINT PK_SOULMOVE_VIAGEM PRIMARY KEY (ID_VIAGEM),
    CONSTRAINT FK_VIAGEM_USUARIO FOREIGN KEY (ID_USUARIO)
        REFERENCES TB_SOULMOVE_USUARIO (ID_USUARIO),
    CONSTRAINT FK_VIAGEM_TRANSPORTE FOREIGN KEY (ID_TRANSPORTE)
        REFERENCES TB_SOULMOVE_TRANSPORTE (ID_TRANSPORTE),
    CONSTRAINT CK_SOULMOVE_VIAGEM_VALIDADA CHECK (ST_VALIDADA IN (0, 1))
);

-- ------------------------------------------------------------
-- Sequences para geracao das chaves primarias
-- ------------------------------------------------------------
CREATE SEQUENCE SQ_SOULMOVE_USUARIO    START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_SOULMOVE_TRANSPORTE START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_SOULMOVE_VIAGEM     START WITH 1 INCREMENT BY 1 NOCACHE;


-- ------------------------------------------------------------
-- Dados de exemplo (CPFs ficticios com digito verificador valido)
-- ------------------------------------------------------------
INSERT INTO TB_SOULMOVE_USUARIO (ID_USUARIO, NM_USUARIO, NR_CPF, DS_EMAIL, NR_BILHETE, ST_ATIVO, NR_SALDO_PONTOS)
VALUES (SQ_SOULMOVE_USUARIO.NEXTVAL, 'Ana Ribeiro', '529.982.247-25', 'ana.ribeiro@exemplo.com', 'BU-1000000001', 1, 0);
INSERT INTO TB_SOULMOVE_USUARIO (ID_USUARIO, NM_USUARIO, NR_CPF, DS_EMAIL, NR_BILHETE, ST_ATIVO, NR_SALDO_PONTOS)
VALUES (SQ_SOULMOVE_USUARIO.NEXTVAL, 'Bruno Carvalho', '111.444.777-35', 'bruno.carvalho@exemplo.com', 'BU-1000000138', 1, 0);

INSERT INTO TB_SOULMOVE_TRANSPORTE (ID_TRANSPORTE, TP_MODAL, DS_IDENTIFICACAO, DS_COMBUSTIVEL, VL_FATOR_EMISSAO, VL_CONSUMO, ST_PARTICIPA)
VALUES (SQ_SOULMOVE_TRANSPORTE.NEXTVAL, 'METRO', 'Linha 2 - Verde', 'Energia eletrica', 0.055, 0.09, 1);
INSERT INTO TB_SOULMOVE_TRANSPORTE (ID_TRANSPORTE, TP_MODAL, DS_IDENTIFICACAO, DS_COMBUSTIVEL, VL_FATOR_EMISSAO, VL_CONSUMO, ST_PARTICIPA)
VALUES (SQ_SOULMOVE_TRANSPORTE.NEXTVAL, 'ONIBUS', '875H-10 Terminal Lapa', 'Diesel S10', 2.68, 2.5, 1);
INSERT INTO TB_SOULMOVE_TRANSPORTE (ID_TRANSPORTE, TP_MODAL, DS_IDENTIFICACAO, DS_COMBUSTIVEL, VL_FATOR_EMISSAO, VL_CONSUMO, ST_PARTICIPA)
VALUES (SQ_SOULMOVE_TRANSPORTE.NEXTVAL, 'ONIBUS', '1178-10 Terminal Tucuruvi', 'Diesel', 2.68, 2.3, 0);

COMMIT;
