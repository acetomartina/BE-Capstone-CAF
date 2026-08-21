-- ============================================================
-- V8 - Estensione configurazione servizi e documenti
-- ============================================================
--
-- Aggiunge:
--   1. Prezzo numerico ai servizi
--   2. Stato attivo ai documenti richiesti del servizio
--   3. Visibilità del documento verso il cliente
--
-- I campi permettono agli amministratori di configurare
-- catalogo e checklist direttamente dal gestionale.
-- ============================================================


-- ============================================================
-- 1. PREZZO SERVIZI
-- ============================================================

ALTER TABLE servizi
    ADD COLUMN prezzo NUMERIC(10, 2);

ALTER TABLE servizi
    ADD CONSTRAINT chk_servizi_prezzo_non_negativo
        CHECK (prezzo IS NULL OR prezzo >= 0);


-- ============================================================
-- 2. CONFIGURAZIONE DOCUMENTI RICHIESTI
-- ============================================================

ALTER TABLE documenti_richiesti_servizio
    ADD COLUMN attivo BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE documenti_richiesti_servizio
    ADD COLUMN visibile_al_cliente BOOLEAN NOT NULL DEFAULT TRUE;


-- ============================================================
-- 3. COMMENTI DATABASE
-- ============================================================

COMMENT ON COLUMN servizi.prezzo IS
    'Prezzo numerico base del servizio. Può essere NULL per servizi con prezzo variabile o su preventivo.';

COMMENT ON COLUMN documenti_richiesti_servizio.attivo IS
    'Indica se il documento deve essere incluso nelle checklist delle nuove pratiche.';

COMMENT ON COLUMN documenti_richiesti_servizio.visibile_al_cliente IS
    'Indica se il requisito documentale può essere mostrato al cliente e nelle sezioni pubbliche.';