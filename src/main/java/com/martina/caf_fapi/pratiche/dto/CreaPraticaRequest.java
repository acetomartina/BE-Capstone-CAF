package com.martina.caf_fapi.pratiche.dto;

import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreaPraticaRequest(

        @NotNull(message = "Il cliente è obbligatorio")
        Long clienteId,

        @NotNull(message = "Il servizio è obbligatorio")
        Long servizioId,

        @NotBlank(message = "L'oggetto della pratica è obbligatorio")
        @Size(
                max = 200,
                message = "L'oggetto non può superare 200 caratteri"
        )
        String oggetto,

        String descrizione,

        PrioritaPratica priorita,

        LocalDate dataScadenza,

        @Size(
                max = 2000,
                message = "Le note non possono superare 2000 caratteri"
        )
        String note
) {
}