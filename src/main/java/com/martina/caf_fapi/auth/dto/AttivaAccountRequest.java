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
public class AttivaAccountRequest {

    @NotBlank(message = "Il token è obbligatorio.")
    @ToString.Exclude
    private String token;

    /*
     * L'attivazione account rappresenta la prima impostazione
     * della password da parte del cliente.
     *
     * Manteniamo quindi gli stessi requisiti utilizzati
     * in CreaUtenteRequest e ResetPasswordRequest.
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