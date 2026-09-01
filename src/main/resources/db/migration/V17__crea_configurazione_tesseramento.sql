CREATE TABLE configurazione_tesseramento
(
    id                  BIGINT PRIMARY KEY,

    quota_annuale       NUMERIC(10, 2),

    creato_il           TIMESTAMP NOT NULL,
    aggiornato_il       TIMESTAMP NOT NULL,
    creato_da           BIGINT,
    aggiornato_da       BIGINT,

    eliminato           BOOLEAN   NOT NULL DEFAULT FALSE,
    eliminato_il        TIMESTAMP,
    eliminato_da        BIGINT,

    CONSTRAINT chk_configurazione_tesseramento_id
        CHECK (id = 1),

    CONSTRAINT chk_configurazione_tesseramento_quota
        CHECK (
            quota_annuale IS NULL
                OR quota_annuale >= 0
            )
);

INSERT INTO configurazione_tesseramento (
    id,
    creato_il,
    aggiornato_il,
    eliminato
)
VALUES (
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP,
           FALSE
       );