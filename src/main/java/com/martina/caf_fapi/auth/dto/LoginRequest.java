package com.martina.caf_fapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "L'email è obbligatoria.")
    @Email(message = "Formato email non valido.")
    @Size(max = 150, message = "L'email non può superare i 150 caratteri.")
    private String email;

    @NotBlank(message = "La password è obbligatoria.")
    @Size(max = 100, message = "La password non può superare i 100 caratteri.")
    private String password;
}