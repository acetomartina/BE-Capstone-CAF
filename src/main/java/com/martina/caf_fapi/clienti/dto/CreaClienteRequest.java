package com.martina.caf_fapi.clienti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreaClienteRequest(

        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 80, message = "Il nome non può superare 80 caratteri")
        String nome,

        @NotBlank(message = "Il cognome è obbligatorio")
        @Size(max = 80, message = "Il cognome non può superare 80 caratteri")
        String cognome,

        @NotBlank(message = "Il codice fiscale è obbligatorio")
        @Pattern(
                regexp = "^[A-Za-z0-9]{16}$",
                message = "Il codice fiscale deve contenere 16 caratteri alfanumerici"
        )
        String codiceFiscale,

        LocalDate dataNascita,

        @Size(max = 100, message = "Il luogo di nascita non può superare 100 caratteri")
        String luogoNascita,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Inserire un indirizzo email valido")
        @Size(max = 150, message = "L'email non può superare 150 caratteri")
        String email,

        @Size(max = 20, message = "Il telefono non può superare 20 caratteri")
        String telefono,

        @Size(max = 150, message = "L'indirizzo non può superare 150 caratteri")
        String indirizzo,

        @Size(max = 100, message = "Il comune non può superare 100 caratteri")
        String comune,

        @Pattern(
                regexp = "^[A-Za-z]{2}$",
                message = "La provincia deve contenere 2 lettere"
        )
        String provincia,

        @Pattern(
                regexp = "^\\d{5}$",
                message = "Il CAP deve contenere 5 cifre"
        )
        String cap,

        @NotBlank(message = "La password è obbligatoria")
        @Size(
                min = 8,
                max = 72,
                message = "La password deve contenere tra 8 e 72 caratteri"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La password deve contenere almeno una maiuscola, una minuscola, un numero e un carattere speciale"
        )
        String password
) {
}