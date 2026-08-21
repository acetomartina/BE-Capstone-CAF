package com.martina.caf_fapi.documenti.controller;

import com.martina.caf_fapi.documenti.dto.CreateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoServizioResponse;
import com.martina.caf_fapi.documenti.dto.RiordinaDocumentiServizioRequest;
import com.martina.caf_fapi.documenti.dto.UpdateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.service.DocumentoServizioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
)
public class DocumentoServizioController {

    private final DocumentoServizioService documentoServizioService;

    @GetMapping("/servizi/{servizioId}/documenti")
    public ResponseEntity<List<DocumentoServizioResponse>>
    trovaDocumentiPerServizio(
            @PathVariable Long servizioId
    ) {
        return ResponseEntity.ok(
                documentoServizioService
                        .trovaDocumentiPerServizio(servizioId)
        );
    }

    @PostMapping("/servizi/{servizioId}/documenti")
    public ResponseEntity<DocumentoServizioResponse>
    creaDocumento(
            @PathVariable Long servizioId,
            @Valid
            @RequestBody
            CreateDocumentoServizioRequest request
    ) {
        DocumentoServizioResponse response =
                documentoServizioService.creaDocumento(
                        servizioId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/servizi/{servizioId}/documenti/ordine")
    public ResponseEntity<List<DocumentoServizioResponse>>
    riordinaDocumenti(
            @PathVariable Long servizioId,
            @Valid
            @RequestBody
            RiordinaDocumentiServizioRequest request
    ) {
        return ResponseEntity.ok(
                documentoServizioService.riordinaDocumenti(
                        servizioId,
                        request
                )
        );
    }

    @PatchMapping("/documenti-servizio/{documentoId}")
    public ResponseEntity<DocumentoServizioResponse>
    aggiornaDocumento(
            @PathVariable Long documentoId,
            @Valid
            @RequestBody
            UpdateDocumentoServizioRequest request
    ) {
        return ResponseEntity.ok(
                documentoServizioService.aggiornaDocumento(
                        documentoId,
                        request
                )
        );
    }

    @DeleteMapping("/documenti-servizio/{documentoId}")
    public ResponseEntity<Void>
    disattivaDocumento(
            @PathVariable Long documentoId
    ) {
        documentoServizioService.disattivaDocumento(
                documentoId
        );

        return ResponseEntity.noContent().build();
    }
}