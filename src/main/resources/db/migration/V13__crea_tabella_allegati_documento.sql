CREATE TABLE allegati_documento
(
    id                  BIGSERIAL PRIMARY KEY,

    documento_pratica_id BIGINT       NOT NULL,
    nome_originale       VARCHAR(255) NOT NULL,
    nome_storage         VARCHAR(255) NOT NULL,
    mime_type            VARCHAR(100) NOT NULL,
    dimensione           BIGINT       NOT NULL,
    caricato_da_id       BIGINT       NOT NULL,
    caricato_il          TIMESTAMP    NOT NULL,

    CONSTRAINT uk_allegati_documento_nome_storage
        UNIQUE (nome_storage),

    CONSTRAINT fk_allegati_documento_documento_pratica
        FOREIGN KEY (documento_pratica_id)
            REFERENCES documenti_richiesti_pratica (id),

    CONSTRAINT fk_allegati_documento_caricato_da
        FOREIGN KEY (caricato_da_id)
            REFERENCES utenti (id)
);

CREATE INDEX idx_allegati_documento_pratica
    ON allegati_documento (documento_pratica_id);

CREATE INDEX idx_allegati_documento_caricato_da
    ON allegati_documento (caricato_da_id);