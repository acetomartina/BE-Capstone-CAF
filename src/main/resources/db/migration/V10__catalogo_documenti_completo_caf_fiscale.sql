-- ============================================================
-- V10 - Catalogo documentale completo macroarea CAF e fiscale
-- ============================================================
--
-- Completa le checklist iniziali dei servizi presenti nella V5.
-- Le checklist sono configurazioni iniziali e potranno essere
-- modificate dagli amministratori dal gestionale.
--
-- NOTA:
-- - i prezzi restano NULL finché non viene inserito il tariffario reale;
-- - i documenti CONDIZIONALI vanno richiesti solo se applicabili;
-- - gli INSERT sono idempotenti rispetto a servizio + etichetta.
-- ============================================================


-- ============================================================
-- NORMALIZZAZIONE CHECKLIST 730 CREATA IN V6
-- ============================================================

UPDATE documenti_richiesti_servizio drs
SET etichetta = 'Certificazioni dei redditi',
    suggerimento = 'Certificazioni relative ai redditi percepiti, come la Certificazione Unica.',
    tipo_obbligatorieta = 'OBBLIGATORIO'
FROM servizi s
WHERE drs.servizio_id = s.id
  AND s.slug = 'modello-730'
  AND drs.etichetta = 'Certificazione Unica';

UPDATE documenti_richiesti_servizio drs
SET attivo = false
FROM servizi s
WHERE drs.servizio_id = s.id
  AND s.slug = 'modello-730'
  AND drs.etichetta = 'Documentazione spese detraibili e deducibili';


-- ============================================================
-- FUNZIONE LOGICA DEGLI INSERT
-- Ogni blocco usa:
-- servizio + etichetta come chiave logica idempotente.
-- ============================================================


-- ============================================================
-- 1. MODELLO 730
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del contribuente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del contribuente.', 'OBBLIGATORIO', 2),
('Certificazioni dei redditi', 'Certificazioni relative ai redditi percepiti, come la Certificazione Unica.', 'OBBLIGATORIO', 3),
('Dichiarazione dei redditi precedente', 'Copia dell''ultima dichiarazione dei redditi disponibile.', 'CONDIZIONALE', 4),
('Codici fiscali dei familiari', 'Codici fiscali dei familiari rilevanti ai fini della dichiarazione.', 'CONDIZIONALE', 5),
('Documentazione terreni e fabbricati', 'Documentazione relativa a immobili e terreni posseduti o oggetto di variazioni.', 'CONDIZIONALE', 6),
('Contratti di locazione', 'Contratti e documentazione relativa a immobili concessi o detenuti in locazione.', 'CONDIZIONALE', 7),
('Interessi passivi mutuo', 'Certificazione degli interessi passivi e documentazione relativa al mutuo.', 'CONDIZIONALE', 8),
('Spese sanitarie', 'Fatture, ricevute e altra documentazione delle spese sanitarie rilevanti.', 'CONDIZIONALE', 9),
('Spese di istruzione', 'Documentazione delle spese scolastiche o universitarie rilevanti.', 'CONDIZIONALE', 10),
('Spese per attività sportive', 'Documentazione delle spese sportive detraibili.', 'CONDIZIONALE', 11),
('Spese funebri', 'Documentazione delle eventuali spese funebri detraibili.', 'CONDIZIONALE', 12),
('Spese veterinarie', 'Documentazione delle eventuali spese veterinarie detraibili.', 'CONDIZIONALE', 13),
('Premi assicurativi', 'Documentazione relativa a premi assicurativi fiscalmente rilevanti.', 'CONDIZIONALE', 14),
('Contributi previdenziali e assistenziali', 'Documentazione relativa ai contributi deducibili.', 'CONDIZIONALE', 15),
('Previdenza complementare', 'Certificazioni relative ai contributi versati a forme di previdenza complementare.', 'CONDIZIONALE', 16),
('Spese per interventi sugli immobili', 'Documentazione relativa a interventi edilizi agevolabili.', 'CONDIZIONALE', 17),
('Spese per risparmio energetico', 'Documentazione relativa agli interventi di riqualificazione energetica agevolabili.', 'CONDIZIONALE', 18),
('Erogazioni liberali', 'Ricevute o attestazioni delle erogazioni liberali fiscalmente rilevanti.', 'CONDIZIONALE', 19),
('Assegni periodici corrisposti', 'Documentazione relativa agli assegni periodici fiscalmente rilevanti.', 'CONDIZIONALE', 20),
('Redditi esteri e imposte pagate all''estero', 'Documentazione relativa a redditi prodotti all''estero e imposte definitive pagate all''estero.', 'CONDIZIONALE', 21),
('Altri redditi', 'Documentazione relativa ad altri redditi da indicare nella dichiarazione.', 'CONDIZIONALE', 22),
('Crediti d''imposta', 'Documentazione relativa a eventuali crediti d''imposta spettanti.', 'CONDIZIONALE', 23),
('Acconti e versamenti effettuati', 'Modelli e ricevute relativi ad acconti o altri versamenti fiscali rilevanti.', 'CONDIZIONALE', 24)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'modello-730'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 2. ISEE
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità del dichiarante', 'Documento di identità in corso di validità del soggetto che presenta la DSU.', 'OBBLIGATORIO', 1),
('Codici fiscali dei componenti del nucleo', 'Codici fiscali o tessere sanitarie dei componenti del nucleo familiare.', 'OBBLIGATORIO', 2),
('Documentazione reddituale di riferimento', 'Documentazione utile alla verifica dei redditi dell''anno di riferimento della DSU.', 'OBBLIGATORIO', 3),
('Saldo e giacenza media dei rapporti finanziari', 'Documentazione relativa ai rapporti finanziari dell''anno patrimoniale rilevante.', 'CONDIZIONALE', 4),
('Altre forme di patrimonio mobiliare', 'Documentazione relativa a titoli, fondi, depositi e altri rapporti finanziari.', 'CONDIZIONALE', 5),
('Patrimonio immobiliare', 'Documentazione relativa a immobili e terreni posseduti in Italia o all''estero.', 'CONDIZIONALE', 6),
('Capitale residuo del mutuo', 'Documentazione del capitale residuo dei mutui relativi agli immobili dichiarati.', 'CONDIZIONALE', 7),
('Contratto di locazione registrato', 'Contratto e relativi estremi di registrazione se il nucleo vive in affitto.', 'CONDIZIONALE', 8),
('Assegni periodici per coniuge o figli', 'Documentazione relativa agli assegni periodici percepiti o corrisposti.', 'CONDIZIONALE', 9),
('Autoveicoli e motoveicoli', 'Targa o estremi identificativi dei veicoli rilevanti intestati ai componenti del nucleo.', 'CONDIZIONALE', 10),
('Navi e imbarcazioni da diporto', 'Estremi di registrazione di eventuali navi o imbarcazioni da diporto.', 'CONDIZIONALE', 11),
('Documentazione relativa a disabilità', 'Documentazione utile in presenza di componenti con disabilità o non autosufficienza.', 'CONDIZIONALE', 12),
('Provvedimenti relativi al nucleo familiare', 'Eventuali provvedimenti giudiziari o documentazione su separazione, divorzio o condizioni particolari.', 'CONDIZIONALE', 13)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'isee'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 3. MODELLO RED
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del pensionato o dichiarante.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del pensionato.', 'OBBLIGATORIO', 2),
('Dati della pensione o comunicazione INPS', 'Documentazione o riferimenti utili a identificare la prestazione collegata al reddito.', 'OBBLIGATORIO', 3),
('Documentazione dei redditi del pensionato', 'Documentazione relativa ai redditi rilevanti per la campagna RED.', 'CONDIZIONALE', 4),
('Documentazione dei redditi del coniuge', 'Documentazione reddituale del coniuge quando richiesta dalla prestazione.', 'CONDIZIONALE', 5),
('Documentazione dei redditi dei familiari', 'Documentazione reddituale di altri componenti rilevanti quando prevista.', 'CONDIZIONALE', 6),
('Documentazione redditi esteri', 'Certificazioni o documentazione relativa a redditi percepiti all''estero.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'modello-red'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 4. MODELLO REDDITI PERSONE FISICHE
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del contribuente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del contribuente.', 'OBBLIGATORIO', 2),
('Dichiarazione dei redditi precedente', 'Copia dell''ultima dichiarazione presentata.', 'CONDIZIONALE', 3),
('Certificazioni dei redditi', 'Certificazioni relative ai redditi percepiti nel periodo d''imposta.', 'CONDIZIONALE', 4),
('Documentazione attività autonoma o impresa', 'Documentazione contabile e fiscale relativa ad attività autonoma o d''impresa.', 'CONDIZIONALE', 5),
('Documentazione terreni e fabbricati', 'Documentazione relativa a immobili e terreni.', 'CONDIZIONALE', 6),
('Documentazione altri redditi', 'Documentazione relativa a redditi diversi, di capitale o altre categorie reddituali.', 'CONDIZIONALE', 7),
('Documentazione oneri deducibili e detraibili', 'Documentazione relativa a spese e oneri fiscalmente rilevanti.', 'CONDIZIONALE', 8),
('Documentazione investimenti e attività estere', 'Documentazione relativa ad attività finanziarie o patrimoniali detenute all''estero.', 'CONDIZIONALE', 9),
('Imposte pagate all''estero', 'Certificazioni delle imposte definitive pagate all''estero.', 'CONDIZIONALE', 10),
('Modelli F24 e acconti versati', 'Ricevute dei versamenti e degli acconti rilevanti per la dichiarazione.', 'CONDIZIONALE', 11)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'modello-redditi-persone-fisiche'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 5. CALCOLO IMU E TASI
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità del contribuente.', 'OBBLIGATORIO', 1),
('Codice fiscale', 'Codice fiscale del contribuente.', 'OBBLIGATORIO', 2),
('Visure o dati catastali degli immobili', 'Rendita catastale, categoria, quota e altri dati necessari al calcolo.', 'OBBLIGATORIO', 3),
('Atti di acquisto, vendita o successione', 'Documentazione relativa a variazioni della titolarità intervenute nel periodo.', 'CONDIZIONALE', 4),
('Documentazione variazioni catastali', 'Documentazione relativa a nuove rendite, fusioni, frazionamenti o altre variazioni.', 'CONDIZIONALE', 5),
('Documentazione agevolazioni o esenzioni', 'Documentazione utile a verificare eventuali agevolazioni, riduzioni o esenzioni.', 'CONDIZIONALE', 6),
('Versamenti precedenti', 'Modelli F24 o ricevute di versamenti già effettuati.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'calcolo-imu-tasi'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 6. CONTRATTI DI LOCAZIONE
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documenti di identità delle parti', 'Documenti di identità dei soggetti coinvolti nel contratto.', 'OBBLIGATORIO', 1),
('Codici fiscali delle parti', 'Codici fiscali di locatore e conduttore e degli eventuali ulteriori soggetti.', 'OBBLIGATORIO', 2),
('Contratto di locazione', 'Testo del contratto da registrare o documentazione del contratto già registrato.', 'OBBLIGATORIO', 3),
('Dati catastali dell''immobile', 'Dati catastali dell''unità immobiliare e delle eventuali pertinenze.', 'OBBLIGATORIO', 4),
('Documentazione adempimento successivo', 'Estremi della registrazione precedente in caso di proroga, cessione, risoluzione o subentro.', 'CONDIZIONALE', 5),
('Documentazione cedolare secca', 'Informazioni necessarie per l''opzione o revoca della cedolare secca.', 'CONDIZIONALE', 6),
('Attestato di prestazione energetica', 'Documentazione energetica dell''immobile quando prevista o utile al contratto.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'contratti-locazione'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 7. SUCCESSIONI
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità del dichiarante', 'Documento di identità del soggetto che presenta la dichiarazione.', 'OBBLIGATORIO', 1),
('Codice fiscale del dichiarante', 'Codice fiscale del soggetto che presenta la dichiarazione.', 'OBBLIGATORIO', 2),
('Dati anagrafici e codice fiscale del defunto', 'Informazioni anagrafiche e codice fiscale della persona deceduta.', 'OBBLIGATORIO', 3),
('Documentazione relativa al decesso', 'Documentazione utile a verificare la data di apertura della successione.', 'OBBLIGATORIO', 4),
('Dati degli eredi e beneficiari', 'Dati anagrafici e codici fiscali degli eredi, legatari o altri beneficiari.', 'OBBLIGATORIO', 5),
('Documentazione attestante la qualità di erede', 'Documentazione o dichiarazioni relative al titolo successorio.', 'CONDIZIONALE', 6),
('Testamento', 'Copia o estremi del testamento, se presente.', 'CONDIZIONALE', 7),
('Documentazione immobili e terreni', 'Dati catastali e documentazione dei beni immobili compresi nell''asse ereditario.', 'CONDIZIONALE', 8),
('Documentazione rapporti bancari e finanziari', 'Documentazione di conti, depositi, titoli e altri rapporti intestati al defunto.', 'CONDIZIONALE', 9),
('Documentazione partecipazioni societarie', 'Documentazione relativa ad aziende, quote o partecipazioni societarie.', 'CONDIZIONALE', 10),
('Documentazione crediti del defunto', 'Documentazione relativa a eventuali crediti compresi nell''attivo ereditario.', 'CONDIZIONALE', 11),
('Documentazione debiti e passività', 'Documentazione relativa a debiti o passività rilevanti.', 'CONDIZIONALE', 12),
('Spese mediche e funerarie rilevanti', 'Documentazione delle spese ammesse tra le passività della successione.', 'CONDIZIONALE', 13),
('Donazioni effettuate in vita', 'Informazioni e documentazione sulle eventuali donazioni rilevanti.', 'CONDIZIONALE', 14),
('Documentazione beni situati all''estero', 'Documentazione relativa a beni o rapporti patrimoniali situati all''estero.', 'CONDIZIONALE', 15),
('Documentazione agevolazione prima casa', 'Documentazione utile se vengono richieste agevolazioni prima casa.', 'CONDIZIONALE', 16),
('Documentazione per volture catastali', 'Documentazione aggiuntiva necessaria per le volture catastali collegate.', 'CONDIZIONALE', 17)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'successioni'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 8. VOLTURE CATASTALI
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità del richiedente', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale del richiedente', 'Codice fiscale del richiedente.', 'OBBLIGATORIO', 2),
('Atto o titolo che determina la voltura', 'Successione, atto notarile, provvedimento o altro titolo che determina il trasferimento.', 'OBBLIGATORIO', 3),
('Dati catastali degli immobili', 'Identificativi catastali degli immobili interessati.', 'OBBLIGATORIO', 4),
('Dati dei soggetti interessati', 'Dati anagrafici e codici fiscali dei soggetti coinvolti nella variazione.', 'OBBLIGATORIO', 5),
('Dichiarazione di successione', 'Copia o riferimenti della dichiarazione di successione se la voltura deriva da successione.', 'CONDIZIONALE', 6),
('Documentazione integrativa catastale', 'Eventuale documentazione necessaria in presenza di incongruenze o situazioni particolari.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'volture-catastali'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 9. ASSISTENZA AGENZIA DELLE ENTRATE
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità del contribuente.', 'OBBLIGATORIO', 1),
('Codice fiscale', 'Codice fiscale del contribuente.', 'OBBLIGATORIO', 2),
('Atto o comunicazione da esaminare', 'Avviso, cartella, comunicazione, ricevuta o altro documento oggetto della richiesta.', 'OBBLIGATORIO', 3),
('Delega o incarico', 'Delega o incarico quando necessario per operare per conto del contribuente.', 'CONDIZIONALE', 4),
('Dichiarazioni fiscali interessate', 'Copie delle dichiarazioni fiscali pertinenti alla problematica.', 'CONDIZIONALE', 5),
('Ricevute e versamenti', 'Ricevute telematiche, modelli F24 e altra documentazione di pagamento pertinente.', 'CONDIZIONALE', 6),
('Documentazione integrativa', 'Ulteriori documenti utili a ricostruire la posizione fiscale.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'assistenza-agenzia-entrate'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 10. PENSIONE DI VECCHIAIA
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
('Estratto contributivo o documentazione carriera lavorativa', 'Documentazione utile a verificare la posizione assicurativa e contributiva.', 'CONDIZIONALE', 3),
('Documentazione cessazione rapporto di lavoro dipendente', 'Documentazione utile a verificare la cessazione del rapporto quando richiesta.', 'CONDIZIONALE', 4),
('Documentazione contributi esteri o altre gestioni', 'Documentazione relativa a contribuzione presso altre gestioni o all''estero.', 'CONDIZIONALE', 5),
('IBAN', 'Coordinate per l''accredito della prestazione, quando utilizzate.', 'CONDIZIONALE', 6)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'pensione-vecchiaia'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 11. PENSIONE ANTICIPATA
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
('Estratto contributivo o documentazione carriera lavorativa', 'Documentazione utile alla verifica dell''anzianità contributiva.', 'OBBLIGATORIO', 3),
('Documentazione cessazione rapporto di lavoro dipendente', 'Documentazione relativa alla cessazione del rapporto quando necessaria.', 'CONDIZIONALE', 4),
('Documentazione requisiti particolari', 'Documentazione relativa a condizioni specifiche, lavori gravosi, precoci o altre fattispecie.', 'CONDIZIONALE', 5),
('Documentazione contributi esteri o altre gestioni', 'Documentazione relativa a periodi contributivi esteri o presso altre gestioni.', 'CONDIZIONALE', 6),
('IBAN', 'Coordinate per l''accredito della prestazione, quando utilizzate.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'pensione-anticipata'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 12. PENSIONE DI REVERSIBILITA / INDIRETTA
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità del richiedente', 'Documento di identità del familiare superstite richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale del richiedente', 'Codice fiscale del familiare superstite.', 'OBBLIGATORIO', 2),
('Dati e codice fiscale del defunto', 'Dati identificativi della persona deceduta.', 'OBBLIGATORIO', 3),
('Documentazione del rapporto familiare', 'Documentazione utile a verificare il rapporto con il defunto quando necessaria.', 'CONDIZIONALE', 4),
('Dati della pensione del defunto', 'Riferimenti della pensione in caso di reversibilità.', 'CONDIZIONALE', 5),
('Documentazione posizione contributiva del defunto', 'Documentazione contributiva in caso di pensione indiretta.', 'CONDIZIONALE', 6),
('Documentazione figli studenti o inabili', 'Documentazione utile in presenza di figli maggiorenni studenti o inabili.', 'CONDIZIONALE', 7),
('Documentazione reddituale del beneficiario', 'Documentazione reddituale quando necessaria per la determinazione della prestazione.', 'CONDIZIONALE', 8),
('IBAN', 'Coordinate per l''accredito della prestazione, quando utilizzate.', 'CONDIZIONALE', 9)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'pensione-reversibilita'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 13. INVALIDITA CIVILE
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
('Certificato medico introduttivo o ricevuta', 'Certificato medico introduttivo e/o ricevuta con codice univoco quando previsti dalla procedura applicabile.', 'OBBLIGATORIO', 3),
('Documentazione sanitaria', 'Referti, certificazioni e documentazione sanitaria utile all''accertamento.', 'CONDIZIONALE', 4),
('Permesso o titolo di soggiorno', 'Documentazione relativa al soggiorno per cittadini stranieri quando necessaria.', 'CONDIZIONALE', 5),
('Documentazione reddituale', 'Documentazione reddituale necessaria in caso di richiesta di prestazioni economiche collegate.', 'CONDIZIONALE', 6),
('IBAN', 'Coordinate per l''accredito di eventuali prestazioni economiche.', 'CONDIZIONALE', 7),
('Documentazione tutela o rappresentanza', 'Provvedimento di tutela, amministrazione di sostegno o delega se opera un rappresentante.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'invalidita-civile'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 14. INDENNITA DI ACCOMPAGNAMENTO
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
('Certificato medico introduttivo o ricevuta', 'Certificato medico introduttivo e/o ricevuta quando previsti dalla procedura applicabile.', 'OBBLIGATORIO', 3),
('Documentazione sanitaria', 'Referti e certificazioni sanitarie utili all''accertamento della condizione.', 'CONDIZIONALE', 4),
('Documentazione invalidità già riconosciuta', 'Verbali o provvedimenti già disponibili relativi all''invalidità.', 'CONDIZIONALE', 5),
('IBAN', 'Coordinate per l''accredito dell''indennità.', 'CONDIZIONALE', 6),
('Documentazione tutela o rappresentanza', 'Provvedimento di tutela, amministrazione di sostegno o delega se opera un rappresentante.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'indennita-accompagnamento'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 15. NASPI
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità in corso di validità del richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del richiedente.', 'OBBLIGATORIO', 2),
('Documentazione ultimo rapporto di lavoro', 'Documentazione relativa all''ultimo rapporto e alla cessazione.', 'OBBLIGATORIO', 3),
('IBAN', 'IBAN intestato o cointestato al richiedente se viene scelto l''accredito su conto.', 'CONDIZIONALE', 4),
('Documentazione attività lavorative ulteriori', 'Documentazione relativa a eventuali altri rapporti o attività in corso.', 'CONDIZIONALE', 5),
('Reddito presunto da attività autonoma o professionale', 'Informazioni utili a dichiarare il reddito presunto quando richiesto.', 'CONDIZIONALE', 6),
('Documentazione malattia, maternità o infortunio', 'Documentazione utile in presenza di eventi rilevanti alla cessazione del rapporto.', 'CONDIZIONALE', 7)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'naspi'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 16. ASSEGNO UNICO UNIVERSALE
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità del richiedente', 'Documento di identità in corso di validità del genitore o soggetto richiedente.', 'OBBLIGATORIO', 1),
('Codice fiscale del richiedente', 'Codice fiscale del soggetto richiedente.', 'OBBLIGATORIO', 2),
('Codici fiscali dei figli', 'Codici fiscali dei figli per i quali viene richiesta o gestita la prestazione.', 'OBBLIGATORIO', 3),
('IBAN o modalità di pagamento', 'Coordinate o informazioni relative alla modalità di pagamento scelta.', 'CONDIZIONALE', 4),
('ISEE', 'Attestazione ISEE valida per la determinazione dell''importo spettante, se disponibile.', 'CONDIZIONALE', 5),
('Documentazione disabilità del figlio', 'Documentazione utile in presenza di figli con disabilità.', 'CONDIZIONALE', 6),
('Provvedimenti di affidamento o responsabilità genitoriale', 'Documentazione relativa ad affidamento esclusivo, ripartizione o altre situazioni particolari.', 'CONDIZIONALE', 7),
('Documentazione tutela o affido', 'Provvedimenti relativi a tutore, affidatario o minore in affido.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'assegno-unico-universale'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- 17. ASSISTENZA INPS
-- ============================================================

INSERT INTO documenti_richiesti_servizio
(servizio_id, etichetta, suggerimento, tipo_obbligatorieta,
 ordine_visualizzazione, attivo, visibile_al_cliente)
SELECT s.id, d.etichetta, d.suggerimento, d.tipo, d.ordine, true, true
FROM servizi s
CROSS JOIN (VALUES
('Documento di identità', 'Documento di identità del cittadino interessato.', 'OBBLIGATORIO', 1),
('Codice fiscale o tessera sanitaria', 'Codice fiscale o tessera sanitaria del cittadino.', 'OBBLIGATORIO', 2),
('Comunicazione o pratica INPS da esaminare', 'Comunicazione, domanda, provvedimento, ricevuta o altro documento oggetto dell''assistenza.', 'OBBLIGATORIO', 3),
('Delega o documentazione di rappresentanza', 'Delega o documentazione necessaria quando la pratica viene gestita per conto di un altro soggetto.', 'CONDIZIONALE', 4),
('Documentazione reddituale', 'Documentazione reddituale quando pertinente alla prestazione o problematica.', 'CONDIZIONALE', 5),
('Documentazione contributiva', 'Estratti, certificazioni o altra documentazione contributiva pertinente.', 'CONDIZIONALE', 6),
('Documentazione sanitaria', 'Documentazione sanitaria quando pertinente alla prestazione o richiesta.', 'CONDIZIONALE', 7),
('Ricevute e protocolli precedenti', 'Ricevute, protocolli o comunicazioni relativi a precedenti invii o richieste.', 'CONDIZIONALE', 8)
) AS d(etichetta, suggerimento, tipo, ordine)
WHERE s.slug = 'assistenza-inps'
AND NOT EXISTS (
    SELECT 1 FROM documenti_richiesti_servizio x
    WHERE x.servizio_id = s.id AND x.etichetta = d.etichetta
);


-- ============================================================
-- PREZZI
-- ============================================================
--
-- Nessun prezzo viene impostato in questa migration.
-- I prezzi resteranno NULL fino all'inserimento del tariffario
-- reale del CAF attraverso una migration dedicata o il gestionale.
-- ============================================================
