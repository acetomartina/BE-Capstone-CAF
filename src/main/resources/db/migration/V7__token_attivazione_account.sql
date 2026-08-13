CREATE TABLE token_attivazione_account (
                                           id BIGSERIAL PRIMARY KEY,

                                           token_hash VARCHAR(64) NOT NULL UNIQUE,

                                           utente_id BIGINT NOT NULL,

                                           scadenza TIMESTAMP NOT NULL,

                                           usato_il TIMESTAMP NULL,

                                           CONSTRAINT fk_token_attivazione_utente
                                               FOREIGN KEY (utente_id)
                                                   REFERENCES utenti(id)
                                                   ON DELETE CASCADE
);

CREATE INDEX idx_token_attivazione_utente
    ON token_attivazione_account(utente_id);