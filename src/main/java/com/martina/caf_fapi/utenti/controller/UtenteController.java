package com.martina.caf_fapi.utenti.controller;

import com.martina.caf_fapi.utenti.dto.CreaUtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @PostMapping
    public ResponseEntity<UtenteResponse> creaUtente(
            @Valid @RequestBody CreaUtenteRequest request
    ) {
        UtenteResponse response =
                utenteService.creaUtente(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<UtenteResponse> trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                utenteService.trovaPerId(id)
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @GetMapping
    public ResponseEntity<Page<UtenteResponse>>
    trovaTutti(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                utenteService.trovaTutti(
                        pageable
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @GetMapping("/ruolo/{ruolo}")
    public ResponseEntity<Page<UtenteResponse>>
    trovaPerRuolo(
            @PathVariable Ruolo ruolo,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                utenteService.trovaPerRuolo(
                        ruolo,
                        pageable
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')"
    )
    @GetMapping("/operatori")
    public ResponseEntity<Page<UtenteResponse>>
    trovaOperatoriAttivi(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                utenteService.trovaOperatoriAttivi(
                        pageable
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<UtenteResponse> aggiornaUtente(
            @PathVariable Long id,
            @Valid @RequestBody
            UtenteUpdateRequest request
    ) {
        return ResponseEntity.ok(
                utenteService.aggiornaUtente(
                        id,
                        request
                )
        );
    }

    @PreAuthorize(
            "hasRole('SUPER_ADMIN')"
    )
    @PatchMapping("/{id}/ruolo")
    public ResponseEntity<UtenteResponse> cambiaRuolo(
            @PathVariable Long id,
            @RequestParam("ruolo")
            Ruolo nuovoRuolo
    ) {
        return ResponseEntity.ok(
                utenteService.cambiaRuolo(
                        id,
                        nuovoRuolo
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @PatchMapping("/{id}/attiva")
    public ResponseEntity<UtenteResponse> attivaUtente(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                utenteService.attivaUtente(id)
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'SUPER_ADMIN')"
    )
    @PatchMapping("/{id}/disattiva")
    public ResponseEntity<UtenteResponse>
    disattivaUtente(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                utenteService.disattivaUtente(id)
        );
    }
}