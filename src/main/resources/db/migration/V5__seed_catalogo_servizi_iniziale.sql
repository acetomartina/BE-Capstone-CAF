-- ============================================================
-- V5 - Seed iniziale catalogo servizi CAF FAPI
-- ============================================================
--
-- Popola:
--   1. Macro-aree mostrate nel frontend
--   2. Partner della sede
--   3. Servizi CAF FAPI e Patronato ENAC
--
-- Gli INSERT sono idempotenti tramite ON CONFLICT(slug):
-- la migration può essere applicata anche al database locale
-- dove alcuni record sono stati inseriti manualmente.
-- ============================================================


-- ============================================================
-- 1. MACRO AREE
-- ============================================================

INSERT INTO macro_aree (
    nome,
    slug,
    descrizione_breve,
    chiave_icona,
    chiave_colore,
    ordine_visualizzazione,
    attiva
)
VALUES
    (
        'CAF e fiscale',
        'caf-e-fiscale',
        'Assistenza fiscale, previdenziale e sociale per cittadini e famiglie.',
        'document',
        'green',
        1,
        true
    ),
    (
        'Energia e gas',
        'energia-e-gas',
        'Supporto nella gestione delle utenze domestiche e aziendali.',
        'energy',
        'orange',
        2,
        true
    ),
    (
        'Telefonia e internet',
        'telefonia-e-internet',
        'Soluzioni mobile, fibra e connettività per casa e attività.',
        'phone',
        'blue',
        3,
        true
    ),
    (
        'Finanziamenti',
        'finanziamenti',
        'Consulenza per prestiti, mutui e soluzioni di credito.',
        'credit-card',
        'pink',
        4,
        true
    ),
    (
        'Mobilità e logistica',
        'mobilita-e-logistica',
        'Servizi per auto, noleggio, spedizioni e punti di ritiro.',
        'truck',
        'purple',
        5,
        true
    ),
    (
        'Servizi digitali',
        'servizi-digitali',
        'Identità digitale, strumenti online e servizi digitali.',
        'monitor',
        'teal',
        6,
        true
    )
    ON CONFLICT (slug)
DO UPDATE SET
    nome = EXCLUDED.nome,
           descrizione_breve = EXCLUDED.descrizione_breve,
           chiave_icona = EXCLUDED.chiave_icona,
           chiave_colore = EXCLUDED.chiave_colore,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attiva = EXCLUDED.attiva,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 2. PARTNER
-- ============================================================

INSERT INTO partner (
    nome,
    slug,
    descrizione,
    ordine_visualizzazione,
    attivo
)
VALUES
    (
        'CAF FAPI',
        'caf-fapi',
        'Centro di assistenza fiscale per dichiarazioni dei redditi, IMU e TASI, contratti di locazione, successioni, volture catastali e assistenza nei rapporti con l''Agenzia delle Entrate.',
        1,
        true
    ),
    (
        'Patronato ENAC',
        'patronato-enac',
        'Patronato per pensioni, invalidità civile, accompagnamento, NASpI, Assegno Unico Universale e assistenza nelle problematiche con INPS.',
        2,
        true
    ),
    (
        'APS Senza Nodi',
        'aps-senza-nodi',
        'Associazione dedicata al supporto sociale, ai centri di recupero e anti-violenza e ad altri servizi di assistenza alla persona.',
        3,
        true
    ),
    (
        'Punto Semplice',
        'punto-semplice',
        'Servizi di identità e innovazione digitale: SPID, PEC e Firma Digitale.',
        4,
        true
    ),
    (
        'Credipass',
        'credipass',
        'Mediazione creditizia per mutui, prestiti personali, cessione del quinto e soluzioni finanziarie per imprese.',
        5,
        true
    ),
    (
        'RentalSi',
        'rentalsi',
        'Servizi di noleggio a lungo termine e leasing per privati, professionisti e imprese.',
        6,
        true
    ),
    (
        'Amazon Hub',
        'amazon-hub',
        'Punto di ritiro e reso degli acquisti Amazon. La sede è identificata su Amazon come Fnt Facciamo Tutto Noi.',
        7,
        true
    ),
    (
        'ProntoPacco',
        'prontopacco',
        'Servizi di spedizione e fermo deposito tramite corrieri, tra cui GLS e FedEx.',
        8,
        true
    ),
    (
        'Indabox',
        'indabox',
        'Punto fisico sicuro per la ricezione e il ritiro di acquisti effettuati online.',
        9,
        true
    ),
    (
        'Semplicert',
        'semplicert',
        'Servizi di pagamento per bollettini postali, MAV, RAV, PagoPA e bollo auto.',
        10,
        true
    ),
    (
        'Promoterbet',
        'promoterbet',
        'Servizi digitali, ricariche, gift card, telefonia, gaming, streaming e prodotti a PIN.',
        11,
        true
    ),
    (
        'CAF Energia e Gas',
        'caf-energia-gas',
        'Analisi delle bollette e attivazione di contratti luce e gas sul mercato libero.',
        12,
        true
    )
    ON CONFLICT (slug)
DO UPDATE SET
    nome = EXCLUDED.nome,
           descrizione = EXCLUDED.descrizione,
           ordine_visualizzazione = EXCLUDED.ordine_visualizzazione,
           attivo = EXCLUDED.attivo,
           aggiornato_il = CURRENT_TIMESTAMP;


-- ============================================================
-- 3. SERVIZI - CAF FAPI
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
    attivo
)
VALUES

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Modello 730',
        'modello-730',
        'Assistenza per la compilazione e presentazione del Modello 730.',
        true,
        true,
        true,
        1,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'ISEE',
        'isee',
        'Calcolo e rilascio dell''attestazione ISEE.',
        true,
        true,
        true,
        2,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Modello RED',
        'modello-red',
        'Assistenza per la compilazione e trasmissione del Modello RED.',
        true,
        true,
        false,
        3,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Modello Redditi Persone Fisiche',
        'modello-redditi-persone-fisiche',
        'Assistenza per la dichiarazione dei redditi tramite Modello Redditi PF.',
        true,
        true,
        false,
        4,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Calcolo IMU e TASI',
        'calcolo-imu-tasi',
        'Calcolo e assistenza per IMU e altri tributi immobiliari.',
        true,
        true,
        false,
        5,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Contratti di locazione',
        'contratti-locazione',
        'Assistenza nella gestione degli adempimenti relativi ai contratti di locazione.',
        true,
        true,
        false,
        6,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Successioni',
        'successioni',
        'Gestione delle pratiche di successione.',
        true,
        true,
        true,
        7,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Volture catastali',
        'volture-catastali',
        'Gestione delle volture catastali collegate alle pratiche patrimoniali.',
        true,
        true,
        false,
        8,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'caf-fapi'),
        'Assistenza Agenzia delle Entrate',
        'assistenza-agenzia-entrate',
        'Assistenza nella gestione di problematiche e contenziosi con l''Agenzia delle Entrate.',
        true,
        true,
        false,
        9,
        true
    ),


-- ============================================================
-- 4. SERVIZI - PATRONATO ENAC
-- ============================================================

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Pensione di vecchiaia',
        'pensione-vecchiaia',
        'Assistenza per la presentazione della domanda di pensione di vecchiaia.',
        true,
        true,
        true,
        10,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Pensione anticipata',
        'pensione-anticipata',
        'Assistenza per le pratiche di pensionamento anticipato.',
        true,
        true,
        false,
        11,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Pensione di reversibilità',
        'pensione-reversibilita',
        'Assistenza per le pratiche di pensione ai superstiti e reversibilità.',
        true,
        true,
        false,
        12,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Invalidità civile',
        'invalidita-civile',
        'Assistenza per le domande di invalidità civile.',
        true,
        true,
        true,
        13,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Indennità di accompagnamento',
        'indennita-accompagnamento',
        'Assistenza per le domande di indennità di accompagnamento.',
        true,
        true,
        false,
        14,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'NASpI',
        'naspi',
        'Assistenza per la domanda di indennità di disoccupazione NASpI.',
        true,
        true,
        true,
        15,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Assegno Unico Universale',
        'assegno-unico-universale',
        'Assistenza per la domanda e la gestione dell''Assegno Unico Universale.',
        true,
        true,
        false,
        16,
        true
    ),

    (
        (SELECT id FROM macro_aree WHERE slug = 'caf-e-fiscale'),
        (SELECT id FROM partner WHERE slug = 'patronato-enac'),
        'Assistenza INPS',
        'assistenza-inps',
        'Consulenza e assistenza per problematiche e pratiche con l''INPS.',
        true,
        true,
        false,
        17,
        true
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
           aggiornato_il = CURRENT_TIMESTAMP;