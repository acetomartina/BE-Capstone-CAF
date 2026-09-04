package com.martina.caf_fapi.auth.controller;

import com.martina.caf_fapi.auth.dto.AttivaAccountRequest;
import com.martina.caf_fapi.auth.dto.LoginRequest;
import com.martina.caf_fapi.auth.dto.LoginResponse;
import com.martina.caf_fapi.auth.dto.MessaggioResponse;
import com.martina.caf_fapi.auth.dto.RecuperoPasswordRequest;
import com.martina.caf_fapi.auth.dto.ResetPasswordRequest;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.auth.service.AccountActivationService;
import com.martina.caf_fapi.auth.service.AuthService;
import com.martina.caf_fapi.auth.service.PasswordResetService;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String MESSAGGIO_RECUPERO =
            "Se l'indirizzo è associato a un account, riceverai a breve "
                    + "una mail con le istruzioni per reimpostare la password.";

    private static final String MESSAGGIO_RESET =
            "Password aggiornata.";

    private static final String MESSAGGIO_ATTIVAZIONE =
            "Account attivato correttamente.";

    private final AuthService authService;

    private final PasswordResetService passwordResetService;

    private final AccountActivationService accountActivationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UtenteResponse> utenteCorrente(
            @AuthenticationPrincipal UtenteDetails dettagli
    ) {
        return ResponseEntity.ok(
                authService.utenteCorrente(
                        dettagli.getUsername()
                )
        );
    }

    @PostMapping("/recupera-password")
    public ResponseEntity<MessaggioResponse> recuperaPassword(
            @Valid @RequestBody RecuperoPasswordRequest request
    ) {
        passwordResetService.richiediRecupero(request);

        return ResponseEntity.ok(
                MessaggioResponse.builder()
                        .messaggio(
                                MESSAGGIO_RECUPERO
                        )
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessaggioResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.reimpostaPassword(
                request
        );

        return ResponseEntity.ok(
                MessaggioResponse.builder()
                        .messaggio(
                                MESSAGGIO_RESET
                        )
                        .build()
        );
    }

    @PostMapping("/attiva-account")
    public ResponseEntity<MessaggioResponse> attivaAccount(
            @Valid @RequestBody AttivaAccountRequest request
    ) {
        accountActivationService.attivaAccount(
                request.getToken(),
                request.getNuovaPassword()
        );

        return ResponseEntity.ok(
                MessaggioResponse.builder()
                        .messaggio(
                                MESSAGGIO_ATTIVAZIONE
                        )
                        .build()
        );
    }
}