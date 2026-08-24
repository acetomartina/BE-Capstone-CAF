-- ============================================================
-- V14 - Contenuti informativi pubblici dei servizi
-- ============================================================
--
-- Obiettivi:
--   1. Aggiungere ai servizi i campi editoriali:
--        - cos_e
--        - a_cosa_serve
--   2. Popolare i contenuti iniziali di tutti i servizi censiti
--      nelle migration V5 e V11.
--   3. Rendere i testi modificabili in seguito dal gestionale admin.
--
-- NOTE:
-- - ADD COLUMN IF NOT EXISTS rende la migration compatibile anche
--   con database locali in cui le colonne siano state create a mano.
-- - I testi rappresentano contenuti editoriali iniziali: potranno
--   essere aggiornati dall'amministratore senza nuove migration.
-- ============================================================


-- ============================================================
-- 1. ESTENSIONE MODELLO SERVIZI
-- ============================================================

ALTER TABLE servizi
    ADD COLUMN IF NOT EXISTS cos_e TEXT;

ALTER TABLE servizi
    ADD COLUMN IF NOT EXISTS a_cosa_serve TEXT;

COMMENT ON COLUMN servizi.cos_e IS
    'Breve spiegazione pubblica di che cosa è il servizio. Modificabile dal gestionale.';

COMMENT ON COLUMN servizi.a_cosa_serve IS
    'Breve spiegazione pubblica dell’utilità del servizio. Modificabile dal gestionale.';


-- ============================================================
-- 2. POPOLAMENTO CONTENUTI INFORMATIVI
-- ============================================================
--
-- Usiamo slug come chiave logica, coerentemente con il catalogo.
-- Un unico UPDATE FROM VALUES mantiene la migration leggibile
-- e rende semplice aggiungere o correggere testi in futuro.
-- ============================================================

UPDATE servizi AS s
SET
    cos_e = v.cos_e,
    a_cosa_serve = v.a_cosa_serve,
    aggiornato_il = CURRENT_TIMESTAMP
    FROM (
VALUES
    ('modello-730',
     'Il Modello 730 è una dichiarazione dei redditi semplificata utilizzata soprattutto da lavoratori dipendenti e pensionati.',
     'Serve a dichiarare i redditi, calcolare imposte e detrazioni e ottenere eventuali rimborsi.'),
    ('isee',
     'L''ISEE è l''indicatore che rappresenta la situazione economica complessiva del nucleo familiare.',
     'Serve per accedere a bonus, agevolazioni e prestazioni sociali legate alla situazione economica.'),
    ('modello-red',
     'Il Modello RED è una dichiarazione reddituale richiesta in alcuni casi ai titolari di prestazioni previdenziali collegate al reddito.',
     'Serve a comunicare i redditi rilevanti per verificare il diritto e l''importo di alcune prestazioni.'),
    ('modello-redditi-persone-fisiche',
     'Il Modello Redditi Persone Fisiche è una dichiarazione fiscale utilizzata per comunicare redditi e altri dati fiscali.',
     'Serve a dichiarare i redditi quando il 730 non è utilizzabile o non è adatto alla propria situazione.'),
    ('calcolo-imu-tasi',
     'È il servizio di verifica e calcolo dei tributi dovuti sugli immobili in base ai dati disponibili.',
     'Serve a determinare gli importi da versare e a predisporre correttamente i relativi pagamenti.'),
    ('contratti-locazione',
     'È il servizio di assistenza per la registrazione e la gestione degli adempimenti legati ai contratti di locazione.',
     'Serve a gestire registrazioni, proroghe, cessioni, risoluzioni e altri adempimenti del contratto.'),
    ('successioni',
     'La dichiarazione di successione comunica all''Amministrazione finanziaria il trasferimento di beni e diritti dopo un decesso.',
     'Serve a regolarizzare gli adempimenti fiscali e patrimoniali collegati alla successione.'),
    ('volture-catastali',
     'La voltura catastale aggiorna gli intestatari degli immobili presenti negli archivi catastali.',
     'Serve ad allineare i dati catastali dopo successioni o altri trasferimenti di diritti sugli immobili.'),
    ('assistenza-agenzia-entrate',
     'È un servizio di supporto per comunicazioni, richieste e pratiche nei rapporti con l''Agenzia delle Entrate.',
     'Serve a comprendere le comunicazioni ricevute e a gestire gli adempimenti o le richieste collegate.'),
    ('pensione-vecchiaia',
     'La pensione di vecchiaia è una prestazione previdenziale riconosciuta al raggiungimento dei requisiti previsti.',
     'Serve a presentare e seguire la domanda di pensionamento quando sono maturati i requisiti.'),
    ('pensione-anticipata',
     'La pensione anticipata consente, nei casi previsti, di accedere al pensionamento prima dell''età ordinaria di vecchiaia.',
     'Serve a verificare la posizione e a presentare la domanda quando risultano soddisfatti i requisiti richiesti.'),
    ('pensione-reversibilita',
     'La pensione di reversibilità è una prestazione destinata, nei casi previsti, ai superstiti di un pensionato.',
     'Serve a richiedere la prestazione previdenziale spettante ai familiari aventi diritto.'),
    ('invalidita-civile',
     'L''invalidità civile riguarda il riconoscimento di condizioni che possono dare accesso a specifiche tutele e prestazioni.',
     'Serve ad avviare e seguire la pratica amministrativa per il riconoscimento dell''invalidità civile.'),
    ('indennita-accompagnamento',
     'L''indennità di accompagnamento è una prestazione economica riconosciuta in presenza dei requisiti previsti.',
     'Serve a presentare e seguire la richiesta della prestazione collegata a specifiche condizioni di non autosufficienza.'),
    ('naspi',
     'La NASpI è un''indennità mensile di disoccupazione prevista per i lavoratori che possiedono i requisiti richiesti.',
     'Serve a richiedere il sostegno economico in caso di perdita involontaria del lavoro, quando spettante.'),
    ('assegno-unico-universale',
     'L''Assegno Unico Universale è un sostegno economico destinato alle famiglie con figli secondo i requisiti previsti.',
     'Serve a richiedere e gestire il contributo economico riconosciuto per i figli a carico.'),
    ('assistenza-inps',
     'È un servizio di supporto per domande, comunicazioni e pratiche nei rapporti con l''INPS.',
     'Serve a comprendere la propria posizione e a gestire richieste o problematiche previdenziali e assistenziali.'),
    ('cambio-fornitore-luce-gas',
     'È il servizio di assistenza per passare a un nuovo fornitore o a una nuova offerta di luce e gas.',
     'Serve a valutare una proposta e gestire il cambio di fornitura senza interrompere il servizio.'),
    ('nuova-attivazione-luce-gas',
     'È il servizio di assistenza per attivare una fornitura di luce o gas su un punto da mettere in esercizio.',
     'Serve a predisporre i dati e la richiesta necessari per avviare una nuova fornitura.'),
    ('analisi-bolletta-energia-gas',
     'È una lettura guidata della bolletta per comprenderne consumi, costi e condizioni applicate.',
     'Serve a capire quanto e come si sta pagando e a valutare eventuali alternative più adatte.'),
    ('assistenza-reclami-luce-gas',
     'È il servizio di supporto per anomalie, contestazioni e comunicazioni relative alle forniture di luce e gas.',
     'Serve a ricostruire il problema e predisporre una richiesta o un reclamo verso il gestore.'),
    ('fibra-internet-casa',
     'È il servizio di assistenza per scegliere e attivare una connessione internet o fibra per la casa.',
     'Serve a verificare le esigenze di connettività e avviare l''offerta più adatta tra quelle disponibili.'),
    ('linea-fissa',
     'È il servizio di assistenza per attivare o gestire una linea telefonica fissa.',
     'Serve a scegliere un''offerta e completare i passaggi necessari per l''attivazione o il trasferimento della linea.'),
    ('sim-offerte-mobile',
     'È il servizio di supporto per scegliere e attivare SIM e offerte di telefonia mobile.',
     'Serve a individuare un piano adatto alle proprie esigenze e gestire l''attivazione della SIM.'),
    ('cambio-operatore-portabilita',
     'È il servizio di assistenza per cambiare operatore mantenendo, quando possibile, il proprio numero.',
     'Serve a gestire la portabilità e i dati necessari al passaggio verso il nuovo operatore.'),
    ('mutui',
     'Il mutuo è un finanziamento a medio-lungo termine, spesso utilizzato per l''acquisto o la gestione di un immobile.',
     'Serve a valutare soluzioni di finanziamento e avviare l''istruttoria con l''intermediario.'),
    ('prestiti-personali',
     'Il prestito personale è un finanziamento destinato a esigenze personali senza una finalità necessariamente vincolata.',
     'Serve a valutare importo, durata e sostenibilità di una soluzione di credito personale.'),
    ('cessione-del-quinto',
     'La cessione del quinto è una forma di finanziamento rimborsata tramite trattenuta su stipendio o pensione.',
     'Serve a valutare una soluzione di credito con rata trattenuta direttamente dalla retribuzione o dalla pensione.'),
    ('finanziamenti-imprese',
     'Sono soluzioni di credito dedicate alle esigenze finanziarie di imprese e attività professionali.',
     'Servono a valutare strumenti per investimenti, liquidità o altre necessità legate all''attività.'),
    ('noleggio-auto-lungo-termine',
     'Il noleggio a lungo termine consente di utilizzare un veicolo per un periodo stabilito pagando un canone periodico.',
     'Serve a valutare un''alternativa all''acquisto dell''auto con servizi inclusi secondo il contratto scelto.'),
    ('amazon-hub-ritiro-resi',
     'È un punto fisico dedicato al ritiro e alla gestione dei resi degli acquisti Amazon.',
     'Serve a ricevere o restituire ordini tramite la sede quando l''opzione è disponibile per la spedizione.'),
    ('spedizioni-prontopacco',
     'È un servizio per spedire pacchi tramite i corrieri convenzionati con il punto ProntoPacco.',
     'Serve a preparare e affidare una spedizione alla sede, scegliendo la soluzione disponibile più adatta.'),
    ('indabox-ritiro-pacchi',
     'È un punto di ritiro che permette di ricevere presso la sede acquisti effettuati online.',
     'Serve ad avere un luogo fisico dove far consegnare e ritirare i propri pacchi.'),
    ('indabox-spedizione-pacchi',
     'È un servizio di spedizione pacchi gestito attraverso il punto Indabox.',
     'Serve a consegnare il pacco in sede e avviare la spedizione tramite i servizi disponibili.'),
    ('pagamento-bollo-auto',
     'È il servizio che consente di effettuare presso la sede il pagamento del bollo auto.',
     'Serve a completare il pagamento dell''imposta automobilistica tramite il punto abilitato.'),
    ('spid',
     'SPID è il sistema di identità digitale che consente di accedere a numerosi servizi online della Pubblica Amministrazione e di altri soggetti aderenti.',
     'Serve ad autenticarsi online con un''identità digitale personale sui servizi che accettano SPID.'),
    ('pec',
     'La PEC è una casella di posta elettronica certificata che attribuisce valore legale all''invio e alla consegna dei messaggi nei casi previsti.',
     'Serve a inviare comunicazioni elettroniche con ricevute che attestano invio e consegna.'),
    ('firma-digitale',
     'La firma digitale è uno strumento informatico che consente di sottoscrivere documenti elettronici con valore giuridico.',
     'Serve a firmare digitalmente file e pratiche senza dover utilizzare una firma autografa su carta.'),
    ('visure-certificati',
     'È un servizio di assistenza per richiedere visure, certificati e documenti disponibili tramite i canali abilitati.',
     'Serve a ottenere documentazione informativa o certificativa necessaria per pratiche e verifiche.'),
    ('pagamenti-pagopa-mav-rav',
     'È il servizio di assistenza per effettuare pagamenti tramite avvisi PagoPA, MAV e RAV.',
     'Serve a pagare in sede gli avvisi compatibili con i circuiti e i servizi disponibili.'),
    ('pagamento-bollettini',
     'È il servizio per effettuare il pagamento di bollettini attraverso i canali abilitati presso la sede.',
     'Serve a completare pagamenti senza dover gestire autonomamente la procedura online.'),
    ('ricariche-servizi-digitali',
     'È un insieme di servizi per ricariche, codici digitali, gift card e altri prodotti elettronici disponibili in sede.',
     'Serve ad acquistare o attivare rapidamente servizi digitali e credito prepagato disponibili sul punto.')
) AS v(slug, cos_e, a_cosa_serve)
WHERE s.slug = v.slug;


-- ============================================================
-- 3. VERIFICA FACOLTATIVA
-- ============================================================
--
-- Query utile da eseguire manualmente in sviluppo:
--
-- SELECT
--     macro_area_id,
--     nome,
--     slug,
--     cos_e,
--     a_cosa_serve
-- FROM servizi
-- ORDER BY macro_area_id, ordine_visualizzazione;
--
-- ============================================================