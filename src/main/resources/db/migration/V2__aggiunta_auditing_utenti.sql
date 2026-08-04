ALTER TABLE utenti
    ADD COLUMN creato_da BIGINT,
    ADD COLUMN aggiornato_da BIGINT;

ALTER TABLE utenti
    ADD CONSTRAINT fk_utenti_creato_da
        FOREIGN KEY (creato_da)
        REFERENCES utenti (id)
        ON DELETE SET NULL;

ALTER TABLE utenti
    ADD CONSTRAINT fk_utenti_aggiornato_da
        FOREIGN KEY (aggiornato_da)
        REFERENCES utenti (id)
        ON DELETE SET NULL;

CREATE INDEX idx_utenti_creato_da
    ON utenti (creato_da);

CREATE INDEX idx_utenti_aggiornato_da
    ON utenti (aggiornato_da);
