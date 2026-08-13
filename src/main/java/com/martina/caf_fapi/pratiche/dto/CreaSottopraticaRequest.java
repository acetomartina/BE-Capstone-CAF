package com.martina.caf_fapi.pratiche.dto;

import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreaSottopraticaRequest(

        @NotBlank(message = "Il titolo della sottopratica è obbligatorio")
        @Size(
                max = 150,
                message = "Il titolo non può superare 150 caratteri"
        )
        String titolo,

        @Size(
                max = 1000,
                message = "La descrizione non può superare 1000 caratteri"
        )
        String descrizione,

        Long operatoreId,

        PrioritaPratica priorita,

        LocalDate dataScadenza,

        @Size(
                max = 2000,
                message = "Le note non possono superare 2000 caratteri"
        )
        String note
) {
}