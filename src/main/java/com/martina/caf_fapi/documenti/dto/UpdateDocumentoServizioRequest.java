package com.martina.caf_fapi.documenti.dto;

import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateDocumentoServizioRequest(

        @Size(max = 150)
        String etichetta,

        String suggerimento,

        TipoObbligatorietaDocumento tipoObbligatorieta,

        Boolean attivo,

        Boolean visibileAlCliente,

        @Min(0)
        Integer ordineVisualizzazione
) {
}