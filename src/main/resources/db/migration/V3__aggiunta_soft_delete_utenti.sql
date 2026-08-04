ALTER TABLE utenti
    ADD COLUMN eliminato BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN eliminato_il TIMESTAMP,
    ADD COLUMN eliminato_da BIGINT;

ALTER TABLE utenti
    ADD CONSTRAINT fk_utenti_eliminato_da
        FOREIGN KEY (eliminato_da)
            REFERENCES utenti(id)
            ON DELETE SET NULL;

CREATE INDEX idx_utenti_eliminato
    ON utenti(eliminato);

CREATE INDEX idx_utenti_eliminato_da
    ON utenti(eliminato_da);