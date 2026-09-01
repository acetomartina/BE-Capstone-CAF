package com.martina.caf_fapi.appuntamenti.controller;

import com.martina.caf_fapi.appuntamenti.dto.AggiornaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.dto.AppuntamentoResponse;
import com.martina.caf_fapi.appuntamenti.dto.CambiaStatoAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.dto.CreaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento;
import com.martina.caf_fapi.appuntamenti.service.AppuntamentoService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appuntamenti")
@RequiredArgsConstructor
@PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')"
)
public class AppuntamentoController {

    private final AppuntamentoService
            appuntamentoService;

    @GetMapping
    public ResponseEntity<
            List<AppuntamentoResponse>
            > trovaTutti(
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso =
                            DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime dal,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso =
                            DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime al,

            @RequestParam(required = false)
            Long clienteId,

            @RequestParam(required = false)
            Long responsabileId,

            @RequestParam(required = false)
            StatoAppuntamento stato
    ) {
        return ResponseEntity.ok(
                appuntamentoService
                        .trovaTutti(
                                dal,
                                al,
                                clienteId,
                                responsabileId,
                                stato
                        )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppuntamentoResponse>
    trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                appuntamentoService
                        .trovaPerId(id)
        );
    }

    @PostMapping
    public ResponseEntity<AppuntamentoResponse>
    crea(
            @Valid
            @RequestBody
            CreaAppuntamentoRequest request
    ) {
        AppuntamentoResponse creato =
                appuntamentoService
                        .crea(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppuntamentoResponse>
    aggiorna(
            @PathVariable Long id,

            @Valid
            @RequestBody
            AggiornaAppuntamentoRequest request
    ) {
        return ResponseEntity.ok(
                appuntamentoService
                        .aggiorna(
                                id,
                                request
                        )
        );
    }

    @PatchMapping("/{id}/stato")
    public ResponseEntity<AppuntamentoResponse>
    cambiaStato(
            @PathVariable Long id,

            @Valid
            @RequestBody
            CambiaStatoAppuntamentoRequest request
    ) {
        return ResponseEntity.ok(
                appuntamentoService
                        .cambiaStato(
                                id,
                                request
                        )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(
            @PathVariable Long id
    ) {
        appuntamentoService.elimina(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}