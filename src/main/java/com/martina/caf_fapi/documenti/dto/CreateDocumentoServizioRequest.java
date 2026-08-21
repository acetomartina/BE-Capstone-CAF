package com.martina.caf_fapi.documenti.dto;

import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDocumentoServizioRequest(

        @NotBlank
        @Size(max = 150)
        String etichetta,

        String suggerimento,

        @NotNull
        TipoObbligatorietaDocumento tipoObbligatorieta,

        Boolean visibileAlCliente,

        @NotNull
        @Min(0)
        Integer ordineVisualizzazione
) {
}