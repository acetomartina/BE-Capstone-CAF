CREATE TABLE tesseramenti
(
    id                  BIGSERIAL PRIMARY KEY,

    cliente_id          BIGINT         NOT NULL,

    data_tesseramento   DATE           NOT NULL,
    data_scadenza       DATE           NOT NULL,

    quota               NUMERIC(10, 2) NOT NULL,
    note                VARCHAR(500),

    annullato           BOOLEAN        NOT NULL DEFAULT FALSE,
    annullato_il        TIMESTAMP,

    creato_il           TIMESTAMP      NOT NULL,
    aggiornato_il       TIMESTAMP      NOT NULL,
    creato_da           BIGINT,
    aggiornato_da       BIGINT,

    eliminato           BOOLEAN        NOT NULL DEFAULT FALSE,
    eliminato_il        TIMESTAMP,
    eliminato_da        BIGINT,

    CONSTRAINT fk_tesseramenti_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES utenti (id),

    CONSTRAINT chk_tesseramenti_quota_non_negativa
        CHECK (quota >= 0),

    CONSTRAINT chk_tesseramenti_scadenza_successiva
        CHECK (data_scadenza > data_tesseramento)
);

CREATE INDEX idx_tesseramenti_cliente
    ON tesseramenti (cliente_id);

CREATE INDEX idx_tesseramenti_scadenza
    ON tesseramenti (data_scadenza);

CREATE INDEX idx_tesseramenti_attivi
    ON tesseramenti (cliente_id, annullato, data_scadenza);