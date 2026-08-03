package com.martina.caf_fapi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = "Il token è obbligatorio.")
    @ToString.Exclude
    private String token;

    /*
     * Stessi vincoli di CreaUtenteRequest: il reset imposta una password
     * nuova, quindi deve valere la regola della creazione utente e non
     * quella piu' larga del login. Il frontend valida con la stessa regola.
     */
    @NotBlank(message = "La password è obbligatoria.")
    @Size(
            min = 8,
            max = 72,
            message = "La password deve contenere tra 8 e 72 caratteri"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "La password deve contenere almeno una maiuscola, una minuscola, un numero e un carattere speciale"
    )
    @ToString.Exclude
    private String nuovaPassword;
}
