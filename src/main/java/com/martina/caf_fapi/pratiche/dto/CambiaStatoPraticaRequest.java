package com.martina.caf_fapi.pratiche.dto;

import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import jakarta.validation.constraints.NotNull;

public record CambiaStatoPraticaRequest(

        @NotNull(message = "Lo stato della pratica è obbligatorio")
        StatoPratica stato

) {
}