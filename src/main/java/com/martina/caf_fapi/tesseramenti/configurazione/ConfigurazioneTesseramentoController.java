package com.martina.caf_fapi.tesseramenti.configurazione;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/amministrazione/tesseramento")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class ConfigurazioneTesseramentoController {

    private final ConfigurazioneTesseramentoService
            configurazioneTesseramentoService;

    @GetMapping
    public ResponseEntity<
            ConfigurazioneTesseramentoResponse
            > trova() {
        return ResponseEntity.ok(
                configurazioneTesseramentoService
                        .trova()
        );
    }

    @PutMapping
    public ResponseEntity<
            ConfigurazioneTesseramentoResponse
            > aggiorna(
            @Valid @RequestBody
            AggiornaConfigurazioneTesseramentoRequest request
    ) {
        return ResponseEntity.ok(
                configurazioneTesseramentoService
                        .aggiorna(request)
        );
    }
}