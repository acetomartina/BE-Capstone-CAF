package com.martina.caf_fapi.documenti.dto;

import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import com.martina.caf_fapi.pratiche.dto.UtentePraticaResponse;

import java.time.LocalDateTime;

public record DocumentoPraticaResponse(

        Long id,

        Long praticaId,

        String numeroPratica,

        String etichetta,

        String suggerimento,

        TipoObbligatorietaDocumento tipoObbligatorieta,

        StatoDocumentoPratica stato,

        UtentePraticaResponse richiestoDa,

        LocalDateTime creatoIl,

        LocalDateTime aggiornatoIl

) {
}