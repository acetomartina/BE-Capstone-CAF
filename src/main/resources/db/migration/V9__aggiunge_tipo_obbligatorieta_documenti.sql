-- ============================================================
-- V9 - Tipo di obbligatorieta dei documenti
-- ============================================================
--
-- Sostituisce il flag booleano "obbligatorio" con una
-- classificazione più espressiva:
--
--   OBBLIGATORIO
--   CONDIZIONALE
--   FACOLTATIVO
--
-- La conversione mantiene i dati esistenti:
--   true  -> OBBLIGATORIO
--   false -> FACOLTATIVO
-- ============================================================


-- ============================================================
-- 1. DOCUMENTI RICHIESTI PER SERVIZIO
-- ============================================================

ALTER TABLE documenti_richiesti_servizio
    ADD COLUMN tipo_obbligatorieta VARCHAR(30);

UPDATE documenti_richiesti_servizio
SET tipo_obbligatorieta =
        CASE
            WHEN obbligatorio = true
                THEN 'OBBLIGATORIO'
            ELSE 'FACOLTATIVO'
            END;

ALTER TABLE documenti_richiesti_servizio
    ALTER COLUMN tipo_obbligatorieta
        SET DEFAULT 'OBBLIGATORIO';

ALTER TABLE documenti_richiesti_servizio
    ALTER COLUMN tipo_obbligatorieta
        SET NOT NULL;

ALTER TABLE documenti_richiesti_servizio
    ADD CONSTRAINT chk_documenti_servizio_tipo_obbligatorieta
        CHECK (
            tipo_obbligatorieta IN (
                                    'OBBLIGATORIO',
                                    'CONDIZIONALE',
                                    'FACOLTATIVO'
                )
            );

ALTER TABLE documenti_richiesti_servizio
DROP COLUMN obbligatorio;


-- ============================================================
-- 2. DOCUMENTI RICHIESTI PER PRATICA
-- ============================================================

ALTER TABLE documenti_richiesti_pratica
    ADD COLUMN tipo_obbligatorieta VARCHAR(30);

UPDATE documenti_richiesti_pratica
SET tipo_obbligatorieta =
        CASE
            WHEN obbligatorio = true
                THEN 'OBBLIGATORIO'
            ELSE 'FACOLTATIVO'
            END;

ALTER TABLE documenti_richiesti_pratica
    ALTER COLUMN tipo_obbligatorieta
        SET DEFAULT 'OBBLIGATORIO';

ALTER TABLE documenti_richiesti_pratica
    ALTER COLUMN tipo_obbligatorieta
        SET NOT NULL;

ALTER TABLE documenti_richiesti_pratica
    ADD CONSTRAINT chk_documenti_pratica_tipo_obbligatorieta
        CHECK (
            tipo_obbligatorieta IN (
                                    'OBBLIGATORIO',
                                    'CONDIZIONALE',
                                    'FACOLTATIVO'
                )
            );

ALTER TABLE documenti_richiesti_pratica
DROP COLUMN obbligatorio;


-- ============================================================
-- 3. COMMENTI DATABASE
-- ============================================================

COMMENT ON COLUMN documenti_richiesti_servizio.tipo_obbligatorieta IS
    'Classificazione del requisito documentale: OBBLIGATORIO, CONDIZIONALE o FACOLTATIVO.';

COMMENT ON COLUMN documenti_richiesti_pratica.tipo_obbligatorieta IS
    'Snapshot della classificazione del requisito documentale al momento della creazione della pratica.';