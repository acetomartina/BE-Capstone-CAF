package com.martina.caf_fapi.appuntamenti.dto;

import com.martina.caf_fapi.appuntamenti.enums.ModalitaAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.TipologiaAppuntamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreaAppuntamentoRequest(

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

        @Size(
                max = 1000,
                message =
                        "La descrizione non può superare 1000 caratteri"
        )
        String descrizione,

        @NotNull(
                message =
                        "La tipologia è obbligatoria"
        )
        TipologiaAppuntamento tipologia,

        @NotNull(
                message =
                        "La modalità è obbligatoria"
        )
        ModalitaAppuntamento modalita,

        @NotNull(
                message =
                        "La data di inizio è obbligatoria"
        )
        LocalDateTime inizio,

        @NotNull(
                message =
                        "La data di fine è obbligatoria"
        )
        LocalDateTime fine,

        @Size(
                max = 200,
                message =
                        "Il luogo non può superare 200 caratteri"
        )
        String luogo,

        @Size(
                max = 500,
                message =
                        "Il link non può superare 500 caratteri"
        )
        String linkOnline,

        @Size(
                max = 1000,
                message =
                        "Le note non possono superare 1000 caratteri"
        )
        String note

) {
}