# CAF FAPI Pianopoli — Backend

API REST per la gestione di un Centro di Assistenza Fiscale: catalogo
servizi pubblico, anagrafica clienti, pratiche con checklist documentali,
tesseramenti e agenda appuntamenti.

Capstone EPICODE — progetto full-stack diviso in due repository.

| | Repository |
|---|---|
| **Backend** (questo) | https://github.com/acetomartina/BE-Capstone-CAF |
| **Frontend** | https://github.com/acetomartina/FE-Capstone-CAF |

Per far girare l'applicazione servono entrambi. Le istruzioni complete
sono qui sotto e comprendono anche il frontend.

---

## Stack

| | |
|---|---|
| Linguaggio | Java 25 |
| Framework | Spring Boot 4.1.0 (Web MVC, Data JPA, Security, Validation, Mail) |
| Database | PostgreSQL 18 |
| Migrazioni | Flyway — 19 migrazioni versionate |
| Autenticazione | JWT (jjwt 0.12.7), password con BCrypt |
| Documentazione API | springdoc-openapi (Swagger UI) |
| Build | Maven (wrapper incluso) |
| Test | JUnit 5, Mockito, Spring Boot Test — 69 test |

---

## Requisiti

- **JDK 25** o superiore
- **PostgreSQL 16+** in esecuzione
- **Node.js 20+** (per il frontend)

Maven non serve installarlo: il progetto include `mvnw`.

---

## Avvio rapido

### 1. Database

```bash
createdb caf_fapi
```

Lo schema **non** va creato a mano: al primo avvio Flyway applica le 19
migrazioni, che creano le tabelle e caricano il catalogo iniziale di
macro-aree, servizi e checklist documentali.

### 2. Configurazione

```bash
cp env.properties.example env.properties
```

Compila `env.properties`. Le variabili obbligatorie sono database,
`JWT_SECRET` e il blocco `SUPER_ADMIN_*` — senza queste ultime
l'applicazione non parte, perche' non hanno valori predefiniti.

Per generare il segreto JWT:

```bash
openssl rand -base64 32
```

Il file `env.properties` e' escluso dal versionamento e non contiene
mai segreti reali nel repository.

### 3. Avvio

```bash
./mvnw spring-boot:run
```

L'API risponde su `http://localhost:8080`.

Al primo avvio viene creato l'utente `SUPER_ADMIN` con le credenziali
indicate in `env.properties`: sono quelle da usare per accedere
all'area amministrativa.

### 4. Frontend

```bash
git clone https://github.com/acetomartina/FE-Capstone-CAF.git
cd FE-Capstone-CAF
cp .env.example .env
npm install
npm run dev
```

Il sito risponde su `http://localhost:5173`. `.env` punta gia' a
`http://localhost:8080`, e il backend accetta quell'origine di default.

---

## Documentazione dell'API

Con il backend avviato:

- Swagger UI — http://localhost:8080/swagger-ui.html
- Specifica OpenAPI — http://localhost:8080/v3/api-docs

Entrambe sono disponibili nel profilo di sviluppo e disattivate in
produzione.

---

## Test

```bash
./mvnw test
```

69 test: unitari con Mockito su servizi e regole di autorizzazione,
slice di Spring MVC sui controller.

> I test si appoggiano al PostgreSQL configurato in `env.properties`.
> Serve quindi un database raggiungibile anche solo per eseguirli.

---

## Architettura

Organizzazione **package-by-feature**: ogni dominio contiene il proprio
controller, service, repository, entity, DTO e mapper.

```
com.martina.caf_fapi
├── allegati        file caricati sui documenti di pratica
├── appuntamenti    agenda della sede
├── auth            login, JWT, recupero e attivazione credenziali
├── clienti         anagrafica assistiti
├── documenti       checklist documentali di servizi e pratiche
├── pratiche        pratiche e sottopratiche
├── profilo         dati e password dell'utente autenticato
├── ricerca         ricerca globale
├── servizi         catalogo macro-aree e servizi
├── tesseramenti    tessere annuali
└── utenti          operatori e ruoli
```

Scelte trasversali:

- **I DTO non espongono mai le entity.** Ogni risposta passa da un
  record dedicato, costruito dentro la transazione.
- **Lo schema e' di Flyway, non di Hibernate.** `ddl-auto=validate`:
  all'avvio Hibernate verifica la corrispondenza e fallisce se lo
  schema e' disallineato.
- **Cancellazione logica** su clienti, pratiche e appuntamenti:
  `eliminato` con data e autore, cosi' lo storico resta consultabile.
- **Auditing automatico** tramite `BaseEntity`: chi ha creato e
  modificato ogni riga, e quando.

### Ruoli

| Ruolo | Puo' fare |
|---|---|
| `SUPER_ADMIN` | tutto, incluse la gestione degli operatori |
| `ADMIN` | area amministrativa, configurazione servizi e tesseramenti |
| `USER` | operativita' di sede: clienti, pratiche, documenti, agenda |
| `CLIENTE` | anagrafica dell'assistito (area riservata non attiva) |

---

## Sicurezza

Le scelte non ovvie, con il motivo:

- **Il cambio password invalida i token gia' emessi.** Ogni JWT porta
  l'istante dell'ultimo cambio password; se non coincide con quello
  dell'utente il token viene rifiutato. Un reset chiude quindi anche
  le sessioni aperte altrove.
- **Il recupero password non rivela se un indirizzo esiste.** La
  risposta e' sempre la stessa e i guasti SMTP vengono assorbiti:
  rispondere 200 alle mail inesistenti e 500 a quelle vere sarebbe
  esattamente l'enumerazione che si vuole evitare.
- **Gli allegati sono filtrati per titolare.** Contengono documenti
  fiscali e d'identita': ogni accesso verifica che la pratica
  appartenga a chi la richiede. "Non esiste" e "non e' tuo" danno lo
  stesso errore, per non permettere di scoprire quali id esistono.
- **La gerarchia dei ruoli vale anche sul bersaglio.** Si puo'
  intervenire solo su utenti il cui ruolo si sarebbe potuti assegnare:
  un ADMIN non modifica un altro ADMIN ne' il SUPER_ADMIN. Senza
  questo vincolo bastava cambiare l'email del SUPER_ADMIN e chiederne
  il recupero password per impossessarsene.
- **I token di reset sono conservati come hash** SHA-256, monouso e
  con scadenza.

---

## Profili

| Profilo | Quando | Effetto |
|---|---|---|
| `dev` | predefinito | query e parametri nei log, Swagger attivo |
| `prod` | `SPRING_PROFILES_ACTIVE=prod` | log ridotti, Swagger disattivato, nessun dettaglio tecnico nelle risposte d'errore |

Il profilo di sviluppo registra i parametri delle query, che
contengono codici fiscali e indirizzi: per questo in produzione
vengono spenti.

---

## Stato del progetto

Sono attivi il **sito pubblico** e l'**area amministrativa**.

L'area riservata ai clienti e la relativa attivazione dell'account
sono sviluppate ma sospese lato interfaccia, in attesa di
completamento. Gli endpoint restano nel codice.
