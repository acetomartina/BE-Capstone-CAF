package com.martina.caf_fapi.appuntamenti.dto;

import com.martina.caf_fapi.appuntamenti.enums.ModalitaAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.TipologiaAppuntamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AggiornaAppuntamentoRequest(

        @NotNull(
                message =
                        "Il cliente è obbligatorio"
        )
        Long clienteId,

        Long praticaId,

        Long responsabileId,

        @NotBlank(
                message =
                        "Il titolo è obbligatorio"
        )
        @Size(
                max = 120,
                message =
                        "Il titolo non può superare 120 caratteri"
        )
        String titolo,

        @Size(max = 1000)
        String descrizione,

        @NotNull
        TipologiaAppuntamento tipologia,

        @NotNull
        ModalitaAppuntamento modalita,

        @NotNull
        LocalDateTime inizio,

        @NotNull
        LocalDateTime fine,

        @Size(max = 200)
        String luogo,

        @Size(max = 500)
        String linkOnline,

        @Size(max = 1000)
        String note

) {
}