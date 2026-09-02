package com.martina.caf_fapi.profilo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CambiaPasswordProfiloRequest(

        @NotBlank(
                message = "La password attuale è obbligatoria."
        )
        String passwordAttuale,

        @NotBlank(
                message = "La nuova password è obbligatoria."
        )
        @Size(
                min = 8,
                max = 72,
                message = "La password deve contenere tra 8 e 72 caratteri."
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La password deve contenere almeno una maiuscola, una minuscola, un numero e un carattere speciale."
        )
        String nuovaPassword

) {
}