ALTER TABLE appuntamenti
    ADD COLUMN titolo VARCHAR(120) NOT NULL,

    ADD COLUMN descrizione VARCHAR(1000),

    ADD COLUMN tipologia VARCHAR(40)
        NOT NULL
        DEFAULT 'APPUNTAMENTO_CAF',

    ADD COLUMN modalita VARCHAR(30)
        NOT NULL
        DEFAULT 'IN_SEDE',

    ADD COLUMN link_online VARCHAR(500),

    ADD COLUMN creato_da BIGINT,

    ADD COLUMN aggiornato_da BIGINT,

    ADD COLUMN eliminato BOOLEAN
        NOT NULL
        DEFAULT FALSE,

    ADD COLUMN eliminato_il TIMESTAMP,

    ADD COLUMN eliminato_da BIGINT;

ALTER TABLE appuntamenti
    ALTER COLUMN stato
        SET DEFAULT 'PROGRAMMATO',

ALTER COLUMN sede
        DROP NOT NULL;

ALTER TABLE appuntamenti
    ADD CONSTRAINT chk_appuntamenti_fine_successiva
        CHECK (fine_il > inizio_il),

    ADD CONSTRAINT chk_appuntamenti_tipologia
        CHECK (
            tipologia IN (
                'APPUNTAMENTO_CAF',
                'CONSEGNA_DOCUMENTI',
                'CONSULENZA',
                'TELEFONATA',
                'ALTRO'
            )
        ),

    ADD CONSTRAINT chk_appuntamenti_modalita
        CHECK (
            modalita IN (
                'IN_SEDE',
                'TELEFONICO',
                'ONLINE'
            )
        ),

    ADD CONSTRAINT chk_appuntamenti_stato
        CHECK (
            stato IN (
                'PROGRAMMATO',
                'CONFERMATO',
                'COMPLETATO',
                'ANNULLATO'
            )
        );

CREATE INDEX idx_appuntamenti_pratica
    ON appuntamenti (pratica_id);

CREATE INDEX idx_appuntamenti_servizio
    ON appuntamenti (servizio_id);

CREATE INDEX idx_appuntamenti_stato_inizio
    ON appuntamenti (
                     stato,
                     inizio_il
        );

CREATE INDEX idx_appuntamenti_attivi_inizio
    ON appuntamenti (
                     eliminato,
                     inizio_il
        );