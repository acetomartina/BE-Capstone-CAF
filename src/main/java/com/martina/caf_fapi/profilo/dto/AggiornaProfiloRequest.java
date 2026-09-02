package com.martina.caf_fapi.profilo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AggiornaProfiloRequest(

        @NotBlank(message = "Il nome è obbligatorio.")
        @Size(
                max = 80,
                message = "Il nome non può superare 80 caratteri."
        )
        String nome,

        @NotBlank(message = "Il cognome è obbligatorio.")
        @Size(
                max = 80,
                message = "Il cognome non può superare 80 caratteri."
        )
        String cognome,

        LocalDate dataNascita,

        @Size(
                max = 100,
                message = "Il luogo di nascita non può superare 100 caratteri."
        )
        String luogoNascita,

        @Pattern(
                regexp = "^$|^\\+?[0-9\\s-]{8,20}$",
                message = "Numero di telefono non valido."
        )
        String telefono,

        @Size(
                max = 150,
                message = "L'indirizzo non può superare 150 caratteri."
        )
        String indirizzo,

        @Size(
                max = 100,
                message = "Il comune non può superare 100 caratteri."
        )
        String comune,

        @Pattern(
                regexp = "^$|^[A-Za-z]{2}$",
                message = "La provincia deve contenere due lettere."
        )
        String provincia,

        @Pattern(
                regexp = "^$|^\\d{5}$",
                message = "Il CAP deve contenere 5 cifre."
        )
        String cap,

        @Size(
                max = 100,
                message = "La mansione non può superare 100 caratteri."
        )
        String mansione,

        @Size(
                max = 500,
                message = "L'URL dell'immagine non può superare 500 caratteri."
        )
        String urlImmagineProfilo

) {
}