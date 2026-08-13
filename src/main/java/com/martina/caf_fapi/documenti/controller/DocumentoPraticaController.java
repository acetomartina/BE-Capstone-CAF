package com.martina.caf_fapi.documenti.controller;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.service.DocumentoPraticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')"
)
public class DocumentoPraticaController {

    private final DocumentoPraticaService
            documentoPraticaService;

    @GetMapping(
            "/api/pratiche/{praticaId}/documenti"
    )
    public ResponseEntity<List<DocumentoPraticaResponse>>
    trovaPerPratica(
            @PathVariable Long praticaId
    ) {
        return ResponseEntity.ok(
                documentoPraticaService
                        .trovaPerPratica(praticaId)
        );
    }

    @GetMapping(
            "/api/documenti-pratica/{id}"
    )
    public ResponseEntity<DocumentoPraticaResponse>
    trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                documentoPraticaService.trovaPerId(id)
        );
    }

    @PatchMapping(
            "/api/documenti-pratica/{id}/stato"
    )
    public ResponseEntity<DocumentoPraticaResponse>
    cambiaStato(
            @PathVariable Long id,
            @Valid @RequestBody
            CambiaStatoDocumentoRequest request
    ) {
        return ResponseEntity.ok(
                documentoPraticaService.cambiaStato(
                        id,
                        request
                )
        );
    }
}