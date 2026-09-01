package com.martina.caf_fapi.clienti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AggiornaClienteRequest(

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

        @Size(
                max = 100,
                message = "Il luogo di nascita non può superare 100 caratteri"
        )
        String luogoNascita,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Inserire un indirizzo email valido")
        @Size(max = 150, message = "L'email non può superare 150 caratteri")
        String email,

        @Pattern(
                regexp = "^$|^\\+?[0-9\\s-]{8,20}$",
                message = "Numero di telefono non valido"
        )
        String telefono,

        @Pattern(
                regexp = "^$|^\\+?[0-9\\s-]{8,20}$",
                message = "Numero di telefono secondario non valido"
        )
        String telefonoSecondario,

        @Size(max = 150, message = "L'indirizzo non può superare 150 caratteri")
        String indirizzo,

        @Size(max = 100, message = "Il comune non può superare 100 caratteri")
        String comune,

        @Pattern(
                regexp = "^$|^[A-Za-z]{2}$",
                message = "La provincia deve contenere 2 lettere"
        )
        String provincia,

        @Pattern(
                regexp = "^$|^\\d{5}$",
                message = "Il CAP deve contenere 5 cifre"
        )
        String cap,

        boolean domicilioDiversoDallaResidenza,

        @Size(
                max = 150,
                message = "L'indirizzo di domicilio non può superare 150 caratteri"
        )
        String domicilioIndirizzo,

        @Size(
                max = 100,
                message = "Il comune di domicilio non può superare 100 caratteri"
        )
        String domicilioComune,

        @Pattern(
                regexp = "^$|^[A-Za-z]{2}$",
                message = "La provincia di domicilio deve contenere 2 lettere"
        )
        String domicilioProvincia,

        @Pattern(
                regexp = "^$|^\\d{5}$",
                message = "Il CAP di domicilio deve contenere 5 cifre"
        )
        String domicilioCap

) {
}