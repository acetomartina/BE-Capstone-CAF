package com.martina.caf_fapi.documenti.dto;

import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentoAdminResponse(
        Long id,

        Long praticaId,
        String numeroPratica,
        String oggettoPratica,
        LocalDate dataScadenza,

        Long clienteId,
        String clienteNome,
        String clienteCognome,
        String clienteCodiceFiscale,

        Long servizioId,
        String servizioNome,

        String etichetta,
        String suggerimento,
        TipoObbligatorietaDocumento tipoObbligatorieta,
        StatoDocumentoPratica stato,

        LocalDateTime creatoIl,
        LocalDateTime aggiornatoIl
) {
}