package com.martina.caf_fapi.utenti.dto;

import com.martina.caf_fapi.utenti.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private LocalDateTime expiresAt;

    private Long utenteId;

    private String nome;

    private String cognome;

    private String email;

    private Ruolo ruolo;

    private String urlImmagineProfilo;
}