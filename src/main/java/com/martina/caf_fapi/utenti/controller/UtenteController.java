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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

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

    @GetMapping("/{id}")
    public ResponseEntity<UtenteResponse> trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                utenteService.trovaPerId(id)
        );
    }

    @GetMapping
    public ResponseEntity<Page<UtenteResponse>> trovaTutti(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                utenteService.trovaTutti(pageable)
        );
    }

    @GetMapping("/ruolo/{ruolo}")
    public ResponseEntity<Page<UtenteResponse>> trovaPerRuolo(
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

    @PutMapping("/{id}")
    public ResponseEntity<UtenteResponse> aggiornaUtente(
            @PathVariable Long id,
            @Valid @RequestBody UtenteUpdateRequest request
    ) {
        return ResponseEntity.ok(
                utenteService.aggiornaUtente(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/ruolo")
    public ResponseEntity<UtenteResponse> cambiaRuolo(
            @PathVariable Long id,
            @RequestParam("ruolo") Ruolo nuovoRuolo
    ) {
        return ResponseEntity.ok(
                utenteService.cambiaRuolo(
                        id,
                        nuovoRuolo
                )
        );
    }

    @PatchMapping("/{id}/attiva")
    public ResponseEntity<UtenteResponse> attivaUtente(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                utenteService.attivaUtente(id)
        );
    }

    @PatchMapping("/{id}/disattiva")
    public ResponseEntity<UtenteResponse> disattivaUtente(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                utenteService.disattivaUtente(id)
        );
    }
}