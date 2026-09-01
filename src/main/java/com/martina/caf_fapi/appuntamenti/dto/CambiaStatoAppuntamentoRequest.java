package com.martina.caf_fapi.appuntamenti.dto;

import com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambiaStatoAppuntamentoRequest(

        @NotNull(
                message =
                        "Lo stato è obbligatorio"
        )
        StatoAppuntamento stato,

        @Size(
                max = 1000,
                message =
                        "Il motivo non può superare 1000 caratteri"
        )
        String motivoAnnullamento

) {
}