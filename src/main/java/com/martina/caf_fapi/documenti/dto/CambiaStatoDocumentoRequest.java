package com.martina.caf_fapi.documenti.dto;

import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import jakarta.validation.constraints.NotNull;

public record CambiaStatoDocumentoRequest(

        @NotNull(message = "Lo stato del documento è obbligatorio")
        StatoDocumentoPratica stato

) {
}