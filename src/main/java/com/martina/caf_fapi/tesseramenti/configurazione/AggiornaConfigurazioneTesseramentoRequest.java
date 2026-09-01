package com.martina.caf_fapi.tesseramenti.configurazione;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AggiornaConfigurazioneTesseramentoRequest(

        @NotNull(
                message = "La quota annuale è obbligatoria."
        )
        @DecimalMin(
                value = "0.00",
                message = "La quota annuale non può essere negativa."
        )
        @Digits(
                integer = 8,
                fraction = 2,
                message = "La quota può contenere al massimo due decimali."
        )
        BigDecimal quotaAnnuale
) {
}