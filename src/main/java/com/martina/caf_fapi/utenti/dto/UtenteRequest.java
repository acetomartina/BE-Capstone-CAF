package com.martina.caf_fapi.utenti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 80, message = "Il nome non può superare gli 80 caratteri")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 80, message = "Il cognome non può superare gli 80 caratteri")
    private String cognome;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    @Pattern(
            regexp = "^[A-Z0-9]{16}$",
            message = "Il codice fiscale deve contenere 16 caratteri alfanumerici"
    )
    private String codiceFiscale;

    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate dataNascita;

    @Size(
            max = 100,
            message = "Il luogo di nascita non può superare i 100 caratteri"
    )
    private String luogoNascita;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Inserisci un indirizzo email valido")
    @Size(max = 150, message = "L'email non può superare i 150 caratteri")
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9 ]{8,20}$",
            message = "Il numero di telefono non è valido"
    )
    private String telefono;

    @Size(
            max = 150,
            message = "L'indirizzo non può superare i 150 caratteri"
    )
    private String indirizzo;

    @Size(
            max = 100,
            message = "Il comune non può superare i 100 caratteri"
    )
    private String comune;

    @Size(
            min = 2,
            max = 2,
            message = "La provincia deve contenere 2 caratteri"
    )
    private String provincia;

    @Pattern(
            regexp = "^\\d{5}$",
            message = "Il CAP deve contenere 5 cifre"
    )
    private String cap;

    @NotBlank(message = "La password è obbligatoria")
    @Size(
            min = 8,
            max = 100,
            message = "La password deve contenere tra 8 e 100 caratteri"
    )
    private String password;

    @Size(
            max = 100,
            message = "La mansione non può superare i 100 caratteri"
    )
    private String mansione;

    @Size(
            max = 50,
            message = "Il numero di matricola non può superare i 50 caratteri"
    )
    private String numeroMatricola;

    @Size(
            max = 500,
            message = "L'URL dell'immagine non può superare i 500 caratteri"
    )
    private String urlImmagineProfilo;
}