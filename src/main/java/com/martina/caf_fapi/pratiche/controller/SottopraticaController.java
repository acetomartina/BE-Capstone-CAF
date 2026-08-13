package com.martina.caf_fapi.pratiche.controller;

import com.martina.caf_fapi.pratiche.dto.*;
import com.martina.caf_fapi.pratiche.service.SottopraticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')"
)
public class SottopraticaController {

    private final SottopraticaService sottopraticaService;

    @GetMapping(
            "/api/pratiche/{praticaId}/sottopratiche"
    )
    public ResponseEntity<Page<SottopraticaResponse>>
    trovaPerPratica(
            @PathVariable Long praticaId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                sottopraticaService.trovaPerPratica(
                        praticaId,
                        pageable
                )
        );
    }

    @PostMapping(
            "/api/pratiche/{praticaId}/sottopratiche"
    )
    public ResponseEntity<SottopraticaResponse> crea(
            @PathVariable Long praticaId,
            @Valid @RequestBody
            CreaSottopraticaRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        sottopraticaService.crea(
                                praticaId,
                                request
                        )
                );
    }

    @GetMapping("/api/sottopratiche/{id}")
    public ResponseEntity<SottopraticaResponse>
    trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                sottopraticaService.trovaPerId(id)
        );
    }

    @PutMapping("/api/sottopratiche/{id}")
    public ResponseEntity<SottopraticaResponse> aggiorna(
            @PathVariable Long id,
            @Valid @RequestBody
            AggiornaSottopraticaRequest request
    ) {
        return ResponseEntity.ok(
                sottopraticaService.aggiorna(
                        id,
                        request
                )
        );
    }

    @PatchMapping(
            "/api/sottopratiche/{id}/stato"
    )
    public ResponseEntity<SottopraticaResponse>
    cambiaStato(
            @PathVariable Long id,
            @Valid @RequestBody
            CambiaStatoSottopraticaRequest request
    ) {
        return ResponseEntity.ok(
                sottopraticaService.cambiaStato(
                        id,
                        request
                )
        );
    }
}