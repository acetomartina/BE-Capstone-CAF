package com.martina.caf_fapi.documenti.controller;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoAdminResponse;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiAdminResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiResponse;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import com.martina.caf_fapi.documenti.service.DocumentoPraticaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/api/documenti-pratica")
    public ResponseEntity<Page<DocumentoAdminResponse>>
    trovaTutti(
            @RequestParam(required = false)
            String termine,

            @RequestParam(required = false)
            StatoDocumentoPratica stato,

            @RequestParam(required = false)
            TipoObbligatorietaDocumento tipoObbligatorieta,

            @PageableDefault(
                    size = 20,
                    sort = "aggiornatoIl",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                documentoPraticaService.trovaTutti(
                        termine,
                        stato,
                        tipoObbligatorieta,
                        pageable
                )
        );
    }

    @GetMapping(
            "/api/documenti-pratica/riepilogo"
    )
    public ResponseEntity<RiepilogoDocumentiAdminResponse>
    riepilogoAdmin() {
        return ResponseEntity.ok(
                documentoPraticaService.riepilogoAdmin()
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
            "/api/pratiche/{praticaId}/documenti/riepilogo"
    )
    public ResponseEntity<RiepilogoDocumentiResponse>
    riepilogo(
            @PathVariable Long praticaId
    ) {
        return ResponseEntity.ok(
                documentoPraticaService
                        .riepilogo(praticaId)
        );
    }
}