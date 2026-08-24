-- ============================================================
-- V15 - Destinatari pubblici dei servizi
-- ============================================================
--
-- Obiettivi:
--   1. Popolare il campo destinatari per tutti i servizi censiti.
--   2. Rendere significativa la sezione pubblica "A chi è rivolto".
--   3. Fornire testi iniziali modificabili successivamente dal gestionale.
--
-- NOTE:
-- - La migration aggiorna solo i servizi già esistenti.
-- - Lo slug viene usato come chiave logica.
-- - I testi sono editoriali iniziali e possono essere modificati
--   dall'amministratore senza nuove migration.
-- ============================================================


UPDATE servizi AS s
SET
    destinatari = v.destinatari,
    aggiornato_il = CURRENT_TIMESTAMP
    FROM (
VALUES
    ('modello-730', 'Sei un lavoratore dipendente, un pensionato o un contribuente che può utilizzare il Modello 730 per presentare la dichiarazione dei redditi.'),
    ('isee', 'Fai parte di un nucleo familiare che deve richiedere bonus, agevolazioni o prestazioni per cui è necessario presentare l’ISEE.'),
    ('modello-red', 'Sei titolare di una prestazione previdenziale collegata al reddito e devi comunicare i redditi richiesti dall’INPS.'),
    ('modello-redditi-persone-fisiche', 'Devi presentare la dichiarazione dei redditi e il Modello 730 non è utilizzabile o non è adatto alla tua situazione fiscale.'),
    ('calcolo-imu-tasi', 'Possiedi immobili o terreni e devi verificare o calcolare i tributi locali dovuti.'),
    ('contratti-locazione', 'Devi registrare o gestire un contratto di locazione, una proroga, una cessione, una risoluzione o un subentro.'),
    ('successioni', 'Devi gestire gli adempimenti fiscali e patrimoniali conseguenti al decesso di una persona.'),
    ('volture-catastali', 'Devi aggiornare l’intestazione catastale di uno o più immobili a seguito di successione o altro trasferimento di diritti.'),
    ('assistenza-agenzia-entrate', 'Hai ricevuto una comunicazione dall’Agenzia delle Entrate o devi gestire una pratica, un chiarimento o un adempimento fiscale.'),
    ('pensione-vecchiaia', 'Hai raggiunto o stai per raggiungere i requisiti previsti per la pensione di vecchiaia.'),
    ('pensione-anticipata', 'Vuoi verificare se puoi accedere alla pensione prima dell’età ordinaria prevista per la vecchiaia.'),
    ('pensione-reversibilita', 'Sei un familiare superstite e vuoi verificare o richiedere la pensione ai superstiti spettante.'),
    ('invalidita-civile', 'Vuoi avviare o seguire una pratica per il riconoscimento dell’invalidità civile.'),
    ('indennita-accompagnamento', 'Vuoi verificare o richiedere l’indennità di accompagnamento in presenza dei requisiti previsti.'),
    ('naspi', 'Hai perso involontariamente il lavoro e vuoi verificare o richiedere l’indennità di disoccupazione NASpI.'),
    ('assegno-unico-universale', 'Hai figli a carico e vuoi richiedere o gestire l’Assegno Unico Universale.'),
    ('assistenza-inps', 'Hai una pratica, una comunicazione o una problematica da gestire con l’INPS.'),
    ('cambio-fornitore-luce-gas', 'Vuoi cambiare fornitore o offerta di luce e gas e preferisci essere assistito nella scelta e nel passaggio.'),
    ('nuova-attivazione-luce-gas', 'Devi attivare una nuova fornitura di luce o gas per un’abitazione o un’attività.'),
    ('analisi-bolletta-energia-gas', 'Vuoi capire meglio costi, consumi e condizioni della tua bolletta e valutare eventuali alternative.'),
    ('assistenza-reclami-luce-gas', 'Hai riscontrato anomalie, addebiti, disservizi o altre problematiche con una fornitura di luce o gas.'),
    ('fibra-internet-casa', 'Vuoi attivare o cambiare una connessione internet o fibra per la tua abitazione.'),
    ('linea-fissa', 'Hai bisogno di attivare, trasferire o gestire una linea telefonica fissa.'),
    ('sim-offerte-mobile', 'Vuoi attivare una nuova SIM o scegliere un’offerta mobile più adatta alle tue esigenze.'),
    ('cambio-operatore-portabilita', 'Vuoi cambiare operatore mantenendo, quando possibile, il tuo numero di telefono.'),
    ('mutui', 'Vuoi acquistare, ristrutturare o rifinanziare un immobile e hai bisogno di valutare una soluzione di mutuo.'),
    ('prestiti-personali', 'Hai bisogno di liquidità per esigenze personali e vuoi valutare una soluzione di finanziamento.'),
    ('cessione-del-quinto', 'Sei un lavoratore dipendente o un pensionato e vuoi valutare un finanziamento con trattenuta su stipendio o pensione.'),
    ('finanziamenti-imprese', 'Gestisci un’impresa o un’attività professionale e vuoi valutare soluzioni di credito per investimenti o liquidità.'),
    ('noleggio-auto-lungo-termine', 'Vuoi utilizzare un’auto senza acquistarla e stai valutando una soluzione di noleggio a lungo termine.'),
    ('amazon-hub-ritiro-resi', 'Vuoi ritirare o restituire un ordine Amazon presso un punto fisico abilitato.'),
    ('spedizioni-prontopacco', 'Devi spedire un pacco e vuoi affidarti a un punto fisico che gestisce la spedizione tramite corrieri convenzionati.'),
    ('indabox-ritiro-pacchi', 'Vuoi ricevere i tuoi acquisti online presso un punto di ritiro invece che a casa.'),
    ('indabox-spedizione-pacchi', 'Devi spedire un pacco tramite i servizi disponibili presso il punto Indabox.'),
    ('pagamento-bollo-auto', 'Devi effettuare il pagamento del bollo auto presso un punto abilitato.'),
    ('spid', 'Hai bisogno di un’identità digitale SPID per accedere ai servizi online che la richiedono.'),
    ('pec', 'Hai bisogno di una casella PEC per inviare e ricevere comunicazioni elettroniche certificate.'),
    ('firma-digitale', 'Devi firmare documenti elettronici con una firma digitale dotata di valore giuridico.'),
    ('visure-certificati', 'Hai bisogno di ottenere visure, certificati o altra documentazione tramite i canali disponibili.'),
    ('pagamenti-pagopa-mav-rav', 'Devi effettuare un pagamento tramite avviso PagoPA, MAV o RAV.'),
    ('pagamento-bollettini', 'Devi pagare un bollettino e preferisci effettuare l’operazione presso la sede.'),
    ('ricariche-servizi-digitali', 'Hai bisogno di ricariche, gift card, codici digitali o altri servizi elettronici disponibili presso la sede.')
) AS v(slug, destinatari)
WHERE s.slug = v.slug;


-- ============================================================
-- VERIFICA FACOLTATIVA
-- ============================================================
--
-- SELECT
--     nome,
--     slug,
--     destinatari
-- FROM servizi
-- ORDER BY macro_area_id, ordine_visualizzazione;
--
-- ============================================================