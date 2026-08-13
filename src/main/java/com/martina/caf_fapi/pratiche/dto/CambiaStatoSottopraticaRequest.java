package com.martina.caf_fapi.pratiche.dto;

import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import jakarta.validation.constraints.NotNull;

public record CambiaStatoSottopraticaRequest(

        @NotNull(message = "Lo stato della sottopratica è obbligatorio")
        StatoPratica stato

) {
}