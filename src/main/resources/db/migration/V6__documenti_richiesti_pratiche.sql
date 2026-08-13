-- ============================================================
-- V6 - Gestione documenti richiesti per pratica
-- ============================================================

-- Uniformiamo lo stato al formato usato dagli enum Java.
ALTER TABLE documenti_richiesti_pratica
    ALTER COLUMN stato SET DEFAULT 'MANCANTE';

UPDATE documenti_richiesti_pratica
SET stato = UPPER(stato);


-- ============================================================
-- Checklist iniziale - Modello 730
-- ============================================================
--
-- Checklist tecnica iniziale.
-- Verrà ampliata/validata con il CAF prima di essere considerata
-- definitiva.
-- ============================================================

INSERT INTO documenti_richiesti_servizio (
    servizio_id,
    etichetta,
    suggerimento,
    obbligatorio,
    ordine_visualizzazione
)
SELECT
    s.id,
    dati.etichetta,
    dati.suggerimento,
    dati.obbligatorio,
    dati.ordine_visualizzazione
FROM servizi s
         CROSS JOIN (
    VALUES
        (
            'Documento di identità',
            'Documento di identità in corso di validità.',
            true,
            1
        ),
        (
            'Codice fiscale o tessera sanitaria',
            'Codice fiscale o tessera sanitaria del contribuente.',
            true,
            2
        ),
        (
            'Certificazione Unica',
            'Certificazione Unica relativa ai redditi percepiti.',
            true,
            3
        ),
        (
            'Dichiarazione dei redditi precedente',
            'Copia dell''ultima dichiarazione dei redditi disponibile.',
            false,
            4
        ),
        (
            'Documentazione spese detraibili e deducibili',
            'Ricevute e documentazione relativa alle spese da portare in detrazione o deduzione.',
            false,
            5
        )
) AS dati(
          etichetta,
          suggerimento,
          obbligatorio,
          ordine_visualizzazione
    )
WHERE s.slug = 'modello-730'
  AND NOT EXISTS (
    SELECT 1
    FROM documenti_richiesti_servizio drs
    WHERE drs.servizio_id = s.id
      AND drs.etichetta = dati.etichetta
);