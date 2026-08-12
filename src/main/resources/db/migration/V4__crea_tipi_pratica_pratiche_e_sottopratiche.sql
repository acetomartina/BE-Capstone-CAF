-- =========================================================
-- CAF FAPI
-- V4 - Evoluzione dominio pratiche
-- PostgreSQL / Flyway
-- =========================================================

-- ---------------------------------------------------------
-- 1. Allineamento pratiche a BaseEntity
-- ---------------------------------------------------------

ALTER TABLE pratiche
    ADD COLUMN creato_da BIGINT,
    ADD COLUMN aggiornato_da BIGINT,
    ADD COLUMN eliminato BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN eliminato_il TIMESTAMP,
    ADD COLUMN eliminato_da BIGINT,
    ADD COLUMN note VARCHAR(2000);


-- ---------------------------------------------------------
-- 2. Auditing: riferimenti agli utenti
-- ---------------------------------------------------------

ALTER TABLE pratiche
    ADD CONSTRAINT fk_pratiche_creato_da
        FOREIGN KEY (creato_da)
            REFERENCES utenti(id)
            ON DELETE SET NULL,

    ADD CONSTRAINT fk_pratiche_aggiornato_da
        FOREIGN KEY (aggiornato_da)
        REFERENCES utenti(id)
        ON DELETE SET NULL,

    ADD CONSTRAINT fk_pratiche_eliminato_da
        FOREIGN KEY (eliminato_da)
        REFERENCES utenti(id)
        ON DELETE SET NULL;


-- ---------------------------------------------------------
-- 3. Priorità
--
-- La V1 utilizzava SMALLINT.
-- Il dominio Java utilizza ora EnumType.STRING.
--
-- Questa conversione è sicura se non esistono ancora
-- pratiche reali nel database.
-- ---------------------------------------------------------

ALTER TABLE pratiche
    ALTER COLUMN priorita DROP DEFAULT;

ALTER TABLE pratiche
ALTER COLUMN priorita TYPE VARCHAR(20)
    USING priorita::VARCHAR(20);

ALTER TABLE pratiche
    ALTER COLUMN priorita SET DEFAULT 'NORMALE';


-- ---------------------------------------------------------
-- 4. Stato
--
-- Rimuoviamo il vecchio default "nuova" della V1 e
-- utilizziamo il valore dell'enum Java.
-- ---------------------------------------------------------

ALTER TABLE pratiche
    ALTER COLUMN stato DROP DEFAULT;

ALTER TABLE pratiche
    ALTER COLUMN stato SET DEFAULT 'DA_AVVIARE';


-- ---------------------------------------------------------
-- 5. Indici aggiuntivi per il nuovo dominio
-- ---------------------------------------------------------

CREATE INDEX idx_pratiche_cliente
    ON pratiche(utente_id);

CREATE INDEX idx_pratiche_responsabile
    ON pratiche(assegnata_a_id);

CREATE INDEX idx_pratiche_servizio
    ON pratiche(servizio_id);

CREATE INDEX idx_pratiche_stato
    ON pratiche(stato);

CREATE INDEX idx_pratiche_scadenza
    ON pratiche(data_scadenza);

CREATE INDEX idx_pratiche_eliminato
    ON pratiche(eliminato);

CREATE INDEX idx_pratiche_creato_da
    ON pratiche(creato_da);

CREATE INDEX idx_pratiche_aggiornato_da
    ON pratiche(aggiornato_da);

CREATE INDEX idx_pratiche_eliminato_da
    ON pratiche(eliminato_da);


-- =========================================================
-- 6. Sottopratiche
-- =========================================================

CREATE TABLE sottopratiche (
                               id BIGSERIAL PRIMARY KEY,

                               pratica_id BIGINT NOT NULL,
                               operatore_id BIGINT,

                               titolo VARCHAR(150) NOT NULL,
                               descrizione VARCHAR(1000),

                               stato VARCHAR(40) NOT NULL DEFAULT 'DA_AVVIARE',
                               priorita VARCHAR(20) NOT NULL DEFAULT 'NORMALE',

                               data_scadenza DATE,
                               data_chiusura DATE,

                               note VARCHAR(2000),

    -- BaseEntity
                               creato_il TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               aggiornato_il TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               creato_da BIGINT,
                               aggiornato_da BIGINT,

                               eliminato BOOLEAN NOT NULL DEFAULT FALSE,
                               eliminato_il TIMESTAMP,
                               eliminato_da BIGINT,

                               CONSTRAINT fk_sottopratiche_pratica
                                   FOREIGN KEY (pratica_id)
                                       REFERENCES pratiche(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_sottopratiche_operatore
                                   FOREIGN KEY (operatore_id)
                                       REFERENCES utenti(id)
                                       ON DELETE SET NULL,

                               CONSTRAINT fk_sottopratiche_creato_da
                                   FOREIGN KEY (creato_da)
                                       REFERENCES utenti(id)
                                       ON DELETE SET NULL,

                               CONSTRAINT fk_sottopratiche_aggiornato_da
                                   FOREIGN KEY (aggiornato_da)
                                       REFERENCES utenti(id)
                                       ON DELETE SET NULL,

                               CONSTRAINT fk_sottopratiche_eliminato_da
                                   FOREIGN KEY (eliminato_da)
                                       REFERENCES utenti(id)
                                       ON DELETE SET NULL
);


-- ---------------------------------------------------------
-- 7. Indici sottopratiche
-- ---------------------------------------------------------

CREATE INDEX idx_sottopratiche_pratica
    ON sottopratiche(pratica_id);

CREATE INDEX idx_sottopratiche_operatore
    ON sottopratiche(operatore_id);

CREATE INDEX idx_sottopratiche_stato
    ON sottopratiche(stato);

CREATE INDEX idx_sottopratiche_scadenza
    ON sottopratiche(data_scadenza);

CREATE INDEX idx_sottopratiche_eliminato
    ON sottopratiche(eliminato);

CREATE INDEX idx_sottopratiche_creato_da
    ON sottopratiche(creato_da);

CREATE INDEX idx_sottopratiche_aggiornato_da
    ON sottopratiche(aggiornato_da);

CREATE INDEX idx_sottopratiche_eliminato_da
    ON sottopratiche(eliminato_da);