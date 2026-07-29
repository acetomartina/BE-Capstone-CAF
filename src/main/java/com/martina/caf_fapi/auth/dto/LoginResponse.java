package com.martina.caf_fapi.auth.dto;

import com.martina.caf_fapi.utenti.entity.Ruolo;
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

    private Long id;

    private String nome;

    private String cognome;

    private String email;

    private Ruolo ruolo;

    private boolean attivo;

    private String urlImmagineProfilo;

}