-- ============================================================
-- V12 - Checklist documentali servizi extra CAF
-- ============================================================
--
-- Completa le checklist dei servizi introdotti con V11 che hanno
-- richiede_documenti = TRUE.
--
-- PRINCIPI:
-- - OBBLIGATORIO: normalmente necessario per avviare la pratica.
-- - CONDIZIONALE: richiesto solo in base al caso, operatore, prodotto
--   o condizioni contrattuali.
-- - FACOLTATIVO: utile all'istruttoria ma non sempre necessario.
--
-- Le checklist restano configurabili dal gestionale.
-- I requisiti specifici del singolo fornitore/gestore prevalgono
-- sempre sulla configurazione iniziale qui definita.
-- ============================================================


-- ============================================================
-- ENERGIA E GAS
-- ============================================================

-- 1. Cambio fornitore luce e gas
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità dell’intestatario della fornitura.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria dell’intestatario.', 'OBBLIGATORIO', 2),
                         ('Ultima bolletta disponibile', 'Bolletta utile a recuperare i dati della fornitura e verificare l’attuale contratto.', 'OBBLIGATORIO', 3),
                         ('Codice POD o PDR', 'Codice identificativo della fornitura elettrica POD o della fornitura gas PDR.', 'OBBLIGATORIO', 4),
                         ('IBAN', 'Coordinate bancarie se il nuovo contratto prevede la domiciliazione dei pagamenti.', 'CONDIZIONALE', 5),
                         ('Partita IVA e dati aziendali', 'Documentazione identificativa dell’attività in caso di utenza business.', 'CONDIZIONALE', 6),
                         ('Delega', 'Delega e documento del delegante se la richiesta viene gestita da un soggetto diverso dall’intestatario.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'cambio-fornitore-luce-gas'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 2. Nuova attivazione luce e gas
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità del nuovo intestatario.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del nuovo intestatario.', 'OBBLIGATORIO', 2),
                         ('Codice POD o PDR', 'Codice del punto di fornitura, se già disponibile.', 'CONDIZIONALE', 3),
                         ('Dati dell’immobile e indirizzo di fornitura', 'Indirizzo completo e informazioni necessarie a identificare il punto da attivare.', 'OBBLIGATORIO', 4),
                         ('Documentazione titolo di occupazione', 'Contratto di locazione, atto o altra documentazione richiesta dal fornitore per attestare la disponibilità dell’immobile.', 'CONDIZIONALE', 5),
                         ('IBAN', 'Coordinate bancarie se viene scelta la domiciliazione.', 'CONDIZIONALE', 6),
                         ('Partita IVA e dati aziendali', 'Dati dell’impresa in caso di fornitura business.', 'CONDIZIONALE', 7),
                         ('Documentazione tecnica della fornitura', 'Documentazione tecnica o dati del contatore quando richiesti per l’attivazione.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'nuova-attivazione-luce-gas'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 3. Analisi bolletta e consulenza
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Ultima bolletta disponibile', 'Bolletta completa da analizzare per consumi, condizioni economiche e dati della fornitura.', 'OBBLIGATORIO', 1),
                         ('Bolletta precedente', 'Una o più bollette precedenti possono essere utili per confrontare consumi e costi nel tempo.', 'FACOLTATIVO', 2),
                         ('Documento di identità', 'Documento dell’intestatario se dalla consulenza si procede con una nuova pratica contrattuale.', 'CONDIZIONALE', 3),
                         ('Codice fiscale', 'Codice fiscale dell’intestatario se dalla consulenza si procede con una nuova pratica.', 'CONDIZIONALE', 4)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'analisi-bolletta-energia-gas'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 4. Assistenza e reclami luce e gas
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità dell’intestatario della fornitura.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale', 'Codice fiscale dell’intestatario.', 'OBBLIGATORIO', 2),
                         ('Bolletta interessata', 'Bolletta o documento relativo alla problematica segnalata.', 'OBBLIGATORIO', 3),
                         ('Comunicazioni del fornitore', 'Email, lettere, solleciti, reclami o risposte già ricevute.', 'CONDIZIONALE', 4),
                         ('Ricevute di pagamento', 'Ricevute utili in caso di contestazioni relative a pagamenti o morosità.', 'CONDIZIONALE', 5),
                         ('Fotografie o letture del contatore', 'Materiale utile in caso di contestazioni relative a letture o consumi.', 'CONDIZIONALE', 6),
                         ('Delega', 'Delega se la pratica viene gestita per conto dell’intestatario.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'assistenza-reclami-luce-gas'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- TELEFONIA E INTERNET
-- ============================================================

-- 5. Fibra e internet casa
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità dell’intestatario.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria dell’intestatario.', 'OBBLIGATORIO', 2),
                         ('Indirizzo di attivazione', 'Indirizzo completo presso cui verificare copertura e attivare il servizio.', 'OBBLIGATORIO', 3),
                         ('Ultima fattura del precedente operatore', 'Utile in caso di migrazione da una linea già attiva.', 'CONDIZIONALE', 4),
                         ('Codice di migrazione', 'Codice necessario quando previsto per il trasferimento di una linea esistente.', 'CONDIZIONALE', 5),
                         ('IBAN o metodo di pagamento', 'Dati necessari se l’offerta scelta prevede addebito automatico.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'fibra-internet-casa'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 6. Linea fissa
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità dell’intestatario della linea.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale', 'Codice fiscale dell’intestatario.', 'OBBLIGATORIO', 2),
                         ('Indirizzo di attivazione', 'Indirizzo completo dell’utenza.', 'OBBLIGATORIO', 3),
                         ('Numero telefonico da mantenere', 'Numero della linea esistente se viene richiesta la portabilità.', 'CONDIZIONALE', 4),
                         ('Codice di migrazione', 'Codice di trasferimento/migrazione della linea quando previsto.', 'CONDIZIONALE', 5),
                         ('Ultima fattura del precedente operatore', 'Utile per recuperare i dati della linea esistente.', 'CONDIZIONALE', 6),
                         ('IBAN o metodo di pagamento', 'Dati richiesti dall’offerta per l’addebito automatico.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'linea-fissa'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 7. SIM e offerte mobile
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità dell’intestatario della SIM.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria dell’intestatario.', 'OBBLIGATORIO', 2),
                         ('SIM del precedente operatore', 'SIM associata al numero da trasferire, quando richiesta dalla procedura.', 'CONDIZIONALE', 3),
                         ('Numero da portare', 'Numero di telefono da trasferire al nuovo operatore.', 'CONDIZIONALE', 4),
                         ('Codice seriale ICCID', 'Codice identificativo della SIM richiesto per alcune procedure di portabilità.', 'CONDIZIONALE', 5),
                         ('IBAN o metodo di pagamento', 'Dati necessari per offerte con addebito automatico.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'sim-offerte-mobile'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 8. Cambio operatore e portabilità
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità dell’intestatario dell’utenza.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale', 'Codice fiscale dell’intestatario.', 'OBBLIGATORIO', 2),
                         ('Ultima fattura del precedente operatore', 'Utile per verificare intestazione, numero e dati di trasferimento.', 'CONDIZIONALE', 3),
                         ('Codice di migrazione', 'Codice di trasferimento della linea fissa quando previsto.', 'CONDIZIONALE', 4),
                         ('Numero telefonico da trasferire', 'Numero oggetto della portabilità.', 'OBBLIGATORIO', 5),
                         ('ICCID della SIM', 'Codice seriale della SIM in caso di portabilità mobile, quando richiesto.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'cambio-operatore-portabilita'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- FINANZIAMENTI
-- ============================================================

-- 9. Mutui
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità del richiedente e degli eventuali cointestatari.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale del richiedente e degli eventuali cointestatari.', 'OBBLIGATORIO', 2),
                         ('Documentazione reddituale', 'Buste paga, CU, dichiarazione dei redditi o altra documentazione richiesta in base alla posizione lavorativa.', 'OBBLIGATORIO', 3),
                         ('Documentazione rapporto di lavoro', 'Contratto di lavoro, attestazione di servizio o documentazione equivalente quando richiesta.', 'CONDIZIONALE', 4),
                         ('Estratti conto', 'Movimenti o estratti conto richiesti dall’intermediario per l’istruttoria.', 'CONDIZIONALE', 5),
                         ('Documentazione immobile', 'Proposta, preliminare, atto di provenienza, planimetria o altra documentazione relativa all’immobile.', 'CONDIZIONALE', 6),
                         ('Documentazione altri finanziamenti', 'Contratti o conteggi relativi a finanziamenti già in corso.', 'CONDIZIONALE', 7),
                         ('Documentazione stato civile o familiare', 'Documentazione richiesta dall’intermediario in relazione alla situazione personale o patrimoniale.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'mutui'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 10. Prestiti personali
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
                         ('Documentazione reddituale', 'Buste paga, cedolino pensione, CU, dichiarazione dei redditi o documentazione equivalente.', 'OBBLIGATORIO', 3),
                         ('IBAN', 'Coordinate del conto corrente quando richieste per accredito o rimborso.', 'CONDIZIONALE', 4),
                         ('Documentazione rapporto di lavoro', 'Contratto o altra documentazione lavorativa quando richiesta.', 'CONDIZIONALE', 5),
                         ('Documentazione finanziamenti in corso', 'Documentazione relativa ad altri impegni finanziari quando necessaria all’istruttoria.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'prestiti-personali'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 11. Cessione del quinto
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
                         ('Ultime buste paga o cedolini pensione', 'Documentazione reddituale recente in base alla posizione del richiedente.', 'OBBLIGATORIO', 3),
                         ('Certificazione Unica', 'Ultima Certificazione Unica disponibile quando richiesta.', 'CONDIZIONALE', 4),
                         ('Certificato di stipendio', 'Certificato rilasciato dal datore di lavoro quando previsto per lavoratori dipendenti.', 'CONDIZIONALE', 5),
                         ('Quota cedibile', 'Comunicazione della quota cedibile in caso di pensionato, quando prevista.', 'CONDIZIONALE', 6),
                         ('Conteggio estintivo', 'Conteggio relativo a una precedente cessione o finanziamento da estinguere/rinnovare.', 'CONDIZIONALE', 7),
                         ('IBAN', 'Coordinate bancarie per l’accredito quando richieste.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'cessione-del-quinto'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 12. Finanziamenti imprese
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità del richiedente o legale rappresentante', 'Documento di identità del soggetto che presenta la richiesta.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale e Partita IVA', 'Dati fiscali del richiedente e dell’attività.', 'OBBLIGATORIO', 2),
                         ('Visura camerale', 'Visura camerale aggiornata quando applicabile.', 'CONDIZIONALE', 3),
                         ('Bilanci o dichiarazioni fiscali', 'Documentazione economico-reddituale dell’attività richiesta per l’istruttoria.', 'OBBLIGATORIO', 4),
                         ('Estratti conto aziendali', 'Documentazione bancaria recente quando richiesta.', 'CONDIZIONALE', 5),
                         ('Business plan o piano dell’investimento', 'Descrizione dell’investimento e delle esigenze finanziarie quando pertinente.', 'CONDIZIONALE', 6),
                         ('Documentazione finanziamenti in corso', 'Contratti, piani di ammortamento o altri impegni finanziari dell’attività.', 'CONDIZIONALE', 7),
                         ('Documentazione societaria', 'Atto costitutivo, statuto o altra documentazione societaria quando richiesta.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'finanziamenti-imprese'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- MOBILITA
-- ============================================================

-- 13. Noleggio auto a lungo termine
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità del richiedente o legale rappresentante.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale', 'Codice fiscale del richiedente.', 'OBBLIGATORIO', 2),
                         ('Patente di guida', 'Patente in corso di validità quando richiesta dall’operatore di noleggio.', 'CONDIZIONALE', 3),
                         ('Documentazione reddituale', 'Buste paga, CU, dichiarazione dei redditi o altra documentazione richiesta per la valutazione.', 'CONDIZIONALE', 4),
                         ('IBAN', 'Coordinate bancarie per l’addebito dei canoni quando previste.', 'CONDIZIONALE', 5),
                         ('Partita IVA e visura camerale', 'Documentazione dell’attività per professionisti e imprese.', 'CONDIZIONALE', 6),
                         ('Bilanci o dichiarazioni fiscali dell’attività', 'Documentazione economica richiesta per clienti business.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'noleggio-auto-lungo-termine'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- SERVIZI DIGITALI
-- ============================================================

-- 14. SPID
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di riconoscimento italiano', 'Carta di identità, patente o passaporto italiano in corso di validità.', 'OBBLIGATORIO', 1),
                         ('Tessera sanitaria o codice fiscale', 'Tessera sanitaria, tesserino del codice fiscale o certificato di attribuzione.', 'OBBLIGATORIO', 2),
                         ('Indirizzo email personale', 'Indirizzo email utilizzato per la registrazione e le comunicazioni relative all’identità digitale.', 'OBBLIGATORIO', 3),
                         ('Numero di cellulare personale', 'Numero di cellulare utilizzato nella procedura di registrazione e sicurezza.', 'OBBLIGATORIO', 4),
                         ('CIE, CNS o firma digitale', 'Può essere utilizzata come modalità di riconoscimento quando supportata dal gestore scelto.', 'CONDIZIONALE', 5)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'spid'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 15. PEC
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità dell’intestatario.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale dell’intestatario della casella.', 'OBBLIGATORIO', 2),
                         ('Indirizzo email ordinario', 'Email utilizzata per comunicazioni, recupero e attivazione quando prevista dal gestore.', 'CONDIZIONALE', 3),
                         ('Numero di cellulare', 'Numero utilizzato per comunicazioni o verifiche di sicurezza quando previsto.', 'CONDIZIONALE', 4),
                         ('Partita IVA e dati dell’organizzazione', 'Dati dell’impresa o professionista in caso di casella business o intestazione organizzativa.', 'CONDIZIONALE', 5),
                         ('Documentazione del legale rappresentante', 'Documentazione necessaria quando la PEC viene richiesta per una persona giuridica.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'pec'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);

-- 16. Firma digitale
INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, TRUE, TRUE
FROM servizi s
         CROSS JOIN (VALUES
                         ('Documento di identità', 'Documento di identità in corso di validità del titolare del certificato.', 'OBBLIGATORIO', 1),
                         ('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del titolare.', 'OBBLIGATORIO', 2),
                         ('Indirizzo email', 'Email necessaria per comunicazioni e attivazione secondo la procedura del certificatore.', 'CONDIZIONALE', 3),
                         ('Numero di cellulare', 'Numero utilizzato per OTP o altre verifiche previste dal certificatore.', 'CONDIZIONALE', 4),
                         ('Documentazione professionale o aziendale', 'Documentazione aggiuntiva in caso di certificati contenenti qualifiche, ruoli o dati organizzativi.', 'CONDIZIONALE', 5),
                         ('Dispositivo di firma precedente', 'Smart Card o Token precedente quando la pratica riguarda rinnovo o sostituzione e il certificatore lo richiede.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'firma-digitale'
  AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- CONTROLLO LOGICO
-- ============================================================
--
-- I seguenti servizi V11 NON ricevono checklist perché sono
-- configurati come richiede_documenti = FALSE:
--
-- amazon-hub-ritiro-resi
-- spedizioni-prontopacco
-- indabox-ritiro-pacchi
-- indabox-spedizione-pacchi
-- pagamento-bollo-auto
-- visure-certificati
-- pagamenti-pagopa-mav-rav
-- pagamento-bollettini
-- ricariche-servizi-digitali
--
-- ============================================================