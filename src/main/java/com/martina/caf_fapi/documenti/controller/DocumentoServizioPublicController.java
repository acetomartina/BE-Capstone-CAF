package com.martina.caf_fapi.documenti.controller;

import com.martina.caf_fapi.documenti.dto.DocumentoServizioResponse;
import com.martina.caf_fapi.documenti.service.DocumentoServizioService;
import com.martina.caf_fapi.servizi.dto.ServizioResponse;
import com.martina.caf_fapi.servizi.service.ServizioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class DocumentoServizioPublicController {

    private final DocumentoServizioService documentoServizioService;
    private final ServizioService servizioService;

    @GetMapping("/servizi/{slug}/documenti")
    public ResponseEntity<List<DocumentoServizioResponse>>
    trovaDocumentiPubbliciPerServizio(
            @PathVariable String slug
    ) {

        ServizioResponse servizio =
                servizioService.trovaServizioPerSlug(slug);

        return ResponseEntity.ok(
                documentoServizioService
                        .trovaDocumentiPubbliciPerServizio(
                                servizio.id()
                        )
        );
    }
}