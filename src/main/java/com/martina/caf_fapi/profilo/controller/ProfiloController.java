package com.martina.caf_fapi.profilo.controller;

import com.martina.caf_fapi.auth.dto.MessaggioResponse;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.profilo.dto.AggiornaProfiloRequest;
import com.martina.caf_fapi.profilo.dto.CambiaPasswordProfiloRequest;
import com.martina.caf_fapi.profilo.service.ProfiloService;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profilo")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ProfiloController {

    private final ProfiloService profiloService;

    @GetMapping
    public ResponseEntity<UtenteResponse>
    trovaProfilo(
            @AuthenticationPrincipal
            UtenteDetails dettagli
    ) {
        return ResponseEntity.ok(
                profiloService.trovaProfilo(
                        dettagli.getUsername()
                )
        );
    }

    @PutMapping
    public ResponseEntity<UtenteResponse>
    aggiornaProfilo(
            @AuthenticationPrincipal
            UtenteDetails dettagli,

            @Valid
            @RequestBody
            AggiornaProfiloRequest request
    ) {
        return ResponseEntity.ok(
                profiloService.aggiornaProfilo(
                        dettagli.getUsername(),
                        request
                )
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<MessaggioResponse>
    cambiaPassword(
            @AuthenticationPrincipal
            UtenteDetails dettagli,

            @Valid
            @RequestBody
            CambiaPasswordProfiloRequest request
    ) {
        profiloService.cambiaPassword(
                dettagli.getUsername(),
                request
        );

        return ResponseEntity.ok(
                MessaggioResponse.builder()
                        .messaggio(
                                "Password aggiornata correttamente."
                        )
                        .build()
        );
    }
}