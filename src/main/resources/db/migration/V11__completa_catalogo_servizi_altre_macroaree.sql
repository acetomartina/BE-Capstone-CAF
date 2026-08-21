-- ============================================================
-- V11 - Catalogo completo servizi delle altre macroaree
-- ============================================================
--
-- Obiettivi:
--   1. Estendere il modello Servizio con:
--        - genera_pratica
--        - richiede_documenti
--   2. Configurare correttamente i servizi CAF/Patronato esistenti
--   3. Popolare le altre 5 macroaree:
--        - Energia e gas
--        - Telefonia e internet
--        - Finanziamenti
--        - Mobilità e logistica
--        - Servizi digitali
--   4. Associare ogni servizio al partner corretto
--
-- I prezzi restano NULL finché non viene inserito il tariffario reale.
-- ============================================================


-- ============================================================
-- 1. ESTENSIONE MODELLO SERVIZI
-- ============================================================

ALTER TABLE servizi
    ADD COLUMN genera_pratica BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE servizi
    ADD COLUMN richiede_documenti BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN servizi.genera_pratica IS
    'Indica se il servizio deve generare una pratica nel gestionale.';

COMMENT ON COLUMN servizi.richiede_documenti IS
    'Indica se il servizio prevede una checklist documentale configurabile.';


-- ============================================================
-- 2. CONFIGURAZIONE SERVIZI CAF FAPI / PATRONATO ENAC ESISTENTI
-- ============================================================
--
-- Tutti i 17 servizi già censiti nella macroarea CAF e fiscale
-- generano una pratica e prevedono documentazione.
-- ============================================================

UPDATE servizi
SET genera_pratica = TRUE,
    richiede_documenti = TRUE
WHERE slug IN (
               'modello-730',
               'isee',
               'modello-red',
               'modello-redditi-persone-fisiche',
               'calcolo-imu-tasi',
               'contratti-locazione',
               'successioni',
               'volture-catastali',
               'assistenza-agenzia-entrate',
               'pensione-vecchiaia',
               'pensione-anticipata',
               'pensione-reversibilita',
               'invalidita-civile',
               'indennita-accompagnamento',
               'naspi',
               'assegno-unico-universale',
               'assistenza-inps'
    );


-- ============================================================
-- 3. ENERGIA E GAS
-- Partner principale: CAF Energia e Gas
-- ============================================================

INSERT INTO servizi (
    macro_area_id,
    partner_id,
    nome,
    slug,
    descrizione_breve,
    prenotabile,
    richiedibile_online,
    in_evidenza,
    ordine_visualizzazione,
    attivo,
    genera_pratica,
    richiede_documenti
)
VALUES
    (
        (SELECT id FROM macro_aree WHERE slug = 'energia-e-gas'),
        (SELECT id FROM partner WHERE slug = 'caf-energia-gas'),
        'Cambio fornitore luce e gas',
        'cambio-fornitore-luce-gas',
        'Consulenza e assistenza per il passaggio a una nuova offerta luce, gas o dual fuel.',
        TRUE,
        TRUE,
        TRUE,
        1,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'energia-e-gas'),
        (SELECT id FROM partner WHERE slug = 'caf-energia-gas'),
        'Nuova attivazione luce e gas',
        'nuova-attivazione-luce-gas',
        'Assistenza per l’attivazione di una nuova fornitura luce o gas.',
        TRUE,
        TRUE,
        TRUE,
        2,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'energia-e-gas'),
        (SELECT id FROM partner WHERE slug = 'caf-energia-gas'),
        'Analisi bolletta e consulenza',
        'analisi-bolletta-energia-gas',
        'Analisi della bolletta e supporto nella scelta della soluzione più adatta.',
        TRUE,
        TRUE,
        FALSE,
        3,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'energia-e-gas'),
        (SELECT id FROM partner WHERE slug = 'caf-energia-gas'),
        'Assistenza e reclami luce e gas',
        'assistenza-reclami-luce-gas',
        'Supporto nella gestione di richieste, anomalie e reclami relativi alle forniture.',
        TRUE,
        TRUE,
        FALSE,
        4,
        TRUE,
        TRUE,
        TRUE
    )
    ON CONFLICT (slug)
DO UPDATE SET
    macro_area_id = EXCLUDED.macro_area_id,
           partner_id = EXCLUDED.partner_id,
           nome = EXCLUDED.nome,
           descrizione_breve = EXCLUDED.descrizione_breve,
           prenotabile = EXCLUDED.prenotabile,
           richiedibile_online = EXCLUDED.richiedibile_online,
           in_evidenza = EXCLUDED.in_evidenza,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attivo = EXCLUDED.attivo,
           genera_pratica = EXCLUDED.genera_pratica,
           richiede_documenti = EXCLUDED.richiede_documenti,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 4. TELEFONIA E INTERNET
-- Partner principale: Punto Semplice
-- ============================================================

INSERT INTO servizi (
    macro_area_id,
    partner_id,
    nome,
    slug,
    descrizione_breve,
    prenotabile,
    richiedibile_online,
    in_evidenza,
    ordine_visualizzazione,
    attivo,
    genera_pratica,
    richiede_documenti
)
VALUES
    (
        (SELECT id FROM macro_aree WHERE slug = 'telefonia-e-internet'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'Fibra e internet casa',
        'fibra-internet-casa',
        'Assistenza nella scelta e attivazione di offerte internet e fibra per la casa.',
        TRUE,
        TRUE,
        TRUE,
        1,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'telefonia-e-internet'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'Linea fissa',
        'linea-fissa',
        'Assistenza per offerte e attivazioni di telefonia fissa.',
        TRUE,
        TRUE,
        FALSE,
        2,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'telefonia-e-internet'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'SIM e offerte mobile',
        'sim-offerte-mobile',
        'Supporto nella scelta e attivazione di SIM e offerte di telefonia mobile.',
        TRUE,
        TRUE,
        TRUE,
        3,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'telefonia-e-internet'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'Cambio operatore e portabilità',
        'cambio-operatore-portabilita',
        'Assistenza per cambio operatore, portabilità del numero e passaggio a nuove offerte.',
        TRUE,
        TRUE,
        FALSE,
        4,
        TRUE,
        TRUE,
        TRUE
    )
    ON CONFLICT (slug)
DO UPDATE SET
    macro_area_id = EXCLUDED.macro_area_id,
           partner_id = EXCLUDED.partner_id,
           nome = EXCLUDED.nome,
           descrizione_breve = EXCLUDED.descrizione_breve,
           prenotabile = EXCLUDED.prenotabile,
           richiedibile_online = EXCLUDED.richiedibile_online,
           in_evidenza = EXCLUDED.in_evidenza,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attivo = EXCLUDED.attivo,
           genera_pratica = EXCLUDED.genera_pratica,
           richiede_documenti = EXCLUDED.richiede_documenti,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 5. FINANZIAMENTI
-- Partner principale: Credipass
-- ============================================================

INSERT INTO servizi (
    macro_area_id,
    partner_id,
    nome,
    slug,
    descrizione_breve,
    prenotabile,
    richiedibile_online,
    in_evidenza,
    ordine_visualizzazione,
    attivo,
    genera_pratica,
    richiede_documenti
)
VALUES
    (
        (SELECT id FROM macro_aree WHERE slug = 'finanziamenti'),
        (SELECT id FROM partner WHERE slug = 'credipass'),
        'Mutui',
        'mutui',
        'Consulenza creditizia e confronto di soluzioni di mutuo.',
        TRUE,
        TRUE,
        TRUE,
        1,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'finanziamenti'),
        (SELECT id FROM partner WHERE slug = 'credipass'),
        'Prestiti personali',
        'prestiti-personali',
        'Consulenza per prestiti personali e soluzioni di liquidità.',
        TRUE,
        TRUE,
        TRUE,
        2,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'finanziamenti'),
        (SELECT id FROM partner WHERE slug = 'credipass'),
        'Cessione del quinto',
        'cessione-del-quinto',
        'Consulenza per finanziamenti tramite cessione del quinto dello stipendio o della pensione.',
        TRUE,
        TRUE,
        TRUE,
        3,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'finanziamenti'),
        (SELECT id FROM partner WHERE slug = 'credipass'),
        'Soluzioni finanziarie per imprese',
        'finanziamenti-imprese',
        'Consulenza per esigenze finanziarie di imprese e attività professionali.',
        TRUE,
        TRUE,
        FALSE,
        4,
        TRUE,
        TRUE,
        TRUE
    )
    ON CONFLICT (slug)
DO UPDATE SET
    macro_area_id = EXCLUDED.macro_area_id,
           partner_id = EXCLUDED.partner_id,
           nome = EXCLUDED.nome,
           descrizione_breve = EXCLUDED.descrizione_breve,
           prenotabile = EXCLUDED.prenotabile,
           richiedibile_online = EXCLUDED.richiedibile_online,
           in_evidenza = EXCLUDED.in_evidenza,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attivo = EXCLUDED.attivo,
           genera_pratica = EXCLUDED.genera_pratica,
           richiede_documenti = EXCLUDED.richiede_documenti,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 6. MOBILITA E LOGISTICA
-- ============================================================

INSERT INTO servizi (
    macro_area_id,
    partner_id,
    nome,
    slug,
    descrizione_breve,
    prenotabile,
    richiedibile_online,
    in_evidenza,
    ordine_visualizzazione,
    attivo,
    genera_pratica,
    richiede_documenti
)
VALUES
    (
        (SELECT id FROM macro_aree WHERE slug = 'mobilita-e-logistica'),
        (SELECT id FROM partner WHERE slug = 'rentalsi'),
        'Noleggio auto a lungo termine',
        'noleggio-auto-lungo-termine',
        'Consulenza e preventivi per il noleggio a lungo termine per privati, professionisti e imprese.',
        TRUE,
        TRUE,
        TRUE,
        1,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'mobilita-e-logistica'),
        (SELECT id FROM partner WHERE slug = 'amazon-hub'),
        'Amazon Hub - ritiro e resi',
        'amazon-hub-ritiro-resi',
        'Punto fisico per il ritiro e la gestione dei resi degli acquisti Amazon.',
        FALSE,
        FALSE,
        TRUE,
        2,
        TRUE,
        FALSE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'mobilita-e-logistica'),
        (SELECT id FROM partner WHERE slug = 'prontopacco'),
        'Spedizioni ProntoPacco',
        'spedizioni-prontopacco',
        'Servizio di spedizione e fermo deposito tramite corrieri convenzionati.',
        FALSE,
        FALSE,
        FALSE,
        3,
        TRUE,
        FALSE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'mobilita-e-logistica'),
        (SELECT id FROM partner WHERE slug = 'indabox'),
        'Indabox - ritiro pacchi',
        'indabox-ritiro-pacchi',
        'Punto di ritiro per acquisti online consegnati da diversi corrieri.',
        FALSE,
        FALSE,
        FALSE,
        4,
        TRUE,
        FALSE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'mobilita-e-logistica'),
        (SELECT id FROM partner WHERE slug = 'indabox'),
        'Indabox - spedizione pacchi',
        'indabox-spedizione-pacchi',
        'Servizio per la spedizione di pacchi tramite il punto Indabox.',
        FALSE,
        FALSE,
        FALSE,
        5,
        TRUE,
        FALSE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'mobilita-e-logistica'),
        (SELECT id FROM partner WHERE slug = 'semplicert'),
        'Pagamento bollo auto',
        'pagamento-bollo-auto',
        'Servizio di pagamento del bollo auto presso la sede.',
        FALSE,
        FALSE,
        FALSE,
        6,
        TRUE,
        FALSE,
        FALSE
    )
    ON CONFLICT (slug)
DO UPDATE SET
    macro_area_id = EXCLUDED.macro_area_id,
           partner_id = EXCLUDED.partner_id,
           nome = EXCLUDED.nome,
           descrizione_breve = EXCLUDED.descrizione_breve,
           prenotabile = EXCLUDED.prenotabile,
           richiedibile_online = EXCLUDED.richiedibile_online,
           in_evidenza = EXCLUDED.in_evidenza,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attivo = EXCLUDED.attivo,
           genera_pratica = EXCLUDED.genera_pratica,
           richiede_documenti = EXCLUDED.richiede_documenti,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 7. SERVIZI DIGITALI
-- ============================================================

INSERT INTO servizi (
    macro_area_id,
    partner_id,
    nome,
    slug,
    descrizione_breve,
    prenotabile,
    richiedibile_online,
    in_evidenza,
    ordine_visualizzazione,
    attivo,
    genera_pratica,
    richiede_documenti
)
VALUES
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'SPID',
        'spid',
        'Assistenza per il rilascio e l’attivazione dell’identità digitale SPID.',
        TRUE,
        FALSE,
        TRUE,
        1,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'PEC',
        'pec',
        'Assistenza per l’attivazione di una casella di Posta Elettronica Certificata.',
        TRUE,
        TRUE,
        TRUE,
        2,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'Firma digitale',
        'firma-digitale',
        'Assistenza per il rilascio di firma digitale, Smart Card o Token USB.',
        TRUE,
        FALSE,
        TRUE,
        3,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'punto-semplice'),
        'Visure e certificati',
        'visure-certificati',
        'Richiesta assistita di visure e certificati tramite i servizi disponibili in sede.',
        TRUE,
        TRUE,
        FALSE,
        4,
        TRUE,
        TRUE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'semplicert'),
        'Pagamenti PagoPA, MAV e RAV',
        'pagamenti-pagopa-mav-rav',
        'Servizio di pagamento per PagoPA, MAV, RAV e altri avvisi supportati.',
        FALSE,
        FALSE,
        FALSE,
        5,
        TRUE,
        FALSE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'semplicert'),
        'Pagamento bollettini',
        'pagamento-bollettini',
        'Servizio di pagamento di bollettini postali e altri avvisi supportati.',
        FALSE,
        FALSE,
        FALSE,
        6,
        TRUE,
        FALSE,
        FALSE
    ),
    (
        (SELECT id FROM macro_aree WHERE slug = 'servizi-digitali'),
        (SELECT id FROM partner WHERE slug = 'promoterbet'),
        'Ricariche e servizi digitali',
        'ricariche-servizi-digitali',
        'Ricariche telefoniche, prodotti digitali e altri servizi disponibili presso la sede.',
        FALSE,
        FALSE,
        FALSE,
        7,
        TRUE,
        FALSE,
        FALSE
    )
    ON CONFLICT (slug)
DO UPDATE SET
    macro_area_id = EXCLUDED.macro_area_id,
           partner_id = EXCLUDED.partner_id,
           nome = EXCLUDED.nome,
           descrizione_breve = EXCLUDED.descrizione_breve,
           prenotabile = EXCLUDED.prenotabile,
           richiedibile_online = EXCLUDED.richiedibile_online,
           in_evidenza = EXCLUDED.in_evidenza,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attivo = EXCLUDED.attivo,
           genera_pratica = EXCLUDED.genera_pratica,
           richiede_documenti = EXCLUDED.richiede_documenti,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 8. PREZZI
-- ============================================================
--
-- Nessun prezzo viene impostato in questa migration.
-- Il campo servizi.prezzo resta NULL finché non viene inserito
-- il tariffario reale della sede.
-- ============================================================