package com.martina.caf_fapi.auth.controller;

import com.martina.caf_fapi.auth.dto.LoginRequest;
import com.martina.caf_fapi.auth.dto.LoginResponse;
import com.martina.caf_fapi.auth.dto.MessaggioResponse;
import com.martina.caf_fapi.auth.dto.RecuperoPasswordRequest;
import com.martina.caf_fapi.auth.dto.ResetPasswordRequest;
import com.martina.caf_fapi.auth.service.AuthService;
import com.martina.caf_fapi.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Risponde sempre 200 con lo stesso messaggio, che l'account esista
     * o no: è la difesa contro l'enumerazione degli indirizzi.
     */
    @PostMapping("/recupera-password")
    public ResponseEntity<MessaggioResponse> recuperaPassword(
            @Valid @RequestBody RecuperoPasswordRequest request
    ) {
        passwordResetService.richiediRecupero(request);

        return ResponseEntity.ok(
                MessaggioResponse.builder()
                        .messaggio(MESSAGGIO_RECUPERO)
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessaggioResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.reimpostaPassword(request);

        return ResponseEntity.ok(
                MessaggioResponse.builder()
                        .messaggio(MESSAGGIO_RESET)
                        .build()
        );
    }
}