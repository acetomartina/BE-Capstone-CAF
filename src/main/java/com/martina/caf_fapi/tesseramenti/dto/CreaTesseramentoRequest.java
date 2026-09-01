package com.martina.caf_fapi.tesseramenti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreaTesseramentoRequest(

        @NotNull(
                message = "La data di tesseramento è obbligatoria."
        )
        @PastOrPresent(
                message = "La data di tesseramento non può essere futura."
        )
        LocalDate dataTesseramento,

        @Size(
                max = 500,
                message = "Le note non possono superare 500 caratteri."
        )
        String note
) {
}