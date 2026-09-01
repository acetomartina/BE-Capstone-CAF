ALTER TABLE utenti
    ADD COLUMN telefono_secondario VARCHAR(20),
    ADD COLUMN domicilio_diverso_dalla_residenza BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN domicilio_indirizzo VARCHAR(150),
    ADD COLUMN domicilio_comune VARCHAR(100),
    ADD COLUMN domicilio_provincia VARCHAR(2),
    ADD COLUMN domicilio_cap VARCHAR(5);

ALTER TABLE utenti
    ADD CONSTRAINT chk_utenti_domicilio_provincia
        CHECK (
            domicilio_provincia IS NULL
                OR domicilio_provincia ~ '^[A-Za-z]{2}$'
    ),
    ADD CONSTRAINT chk_utenti_domicilio_cap
    CHECK (
    domicilio_cap IS NULL
    OR domicilio_cap ~ '^[0-9]{5}$'
    );