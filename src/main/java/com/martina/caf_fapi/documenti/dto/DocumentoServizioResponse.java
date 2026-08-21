package com.martina.caf_fapi.documenti.dto;

import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;

public record DocumentoServizioResponse(

        Long id,

        Long servizioId,

        String etichetta,

        String suggerimento,

        boolean attivo,

        boolean visibileAlCliente,

        TipoObbligatorietaDocumento tipoObbligatorieta,

        Integer ordineVisualizzazione
) {
}