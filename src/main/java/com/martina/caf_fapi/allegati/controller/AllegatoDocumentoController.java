package com.martina.caf_fapi.allegati.controller;

import com.martina.caf_fapi.allegati.dto.AllegatoDocumentoResponse;
import com.martina.caf_fapi.allegati.service.AllegatoDocumentoService;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Anche un CLIENTE deve poter caricare i propri documenti, quindi qui non
 * si filtra per ruolo: e' il service a verificare, per ogni allegato, che
 * appartenga a una pratica di chi sta chiamando.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AllegatoDocumentoController {

    private final AllegatoDocumentoService
            allegatoDocumentoService;

    @PostMapping(
            value = "/documenti-pratica/{documentoId}/allegati",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AllegatoDocumentoResponse>
    carica(
            @PathVariable Long documentoId,
            @RequestPart("file")
            MultipartFile file,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        AllegatoDocumentoResponse response =
                allegatoDocumentoService.carica(
                        documentoId,
                        file,
                        utenteDetails
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            "/documenti-pratica/{documentoId}/allegati"
    )
    public ResponseEntity<List<AllegatoDocumentoResponse>>
    trovaPerDocumento(
            @PathVariable Long documentoId,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        return ResponseEntity.ok(
                allegatoDocumentoService
                        .trovaPerDocumento(
                                documentoId,
                                utenteDetails
                        )
        );
    }

    @GetMapping(
            "/allegati/{allegatoId}/download"
    )
    public ResponseEntity<Resource>
    scarica(
            @PathVariable Long allegatoId,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        AllegatoDocumentoService.DownloadAllegato download =
                allegatoDocumentoService.scarica(
                        allegatoId,
                        utenteDetails
                );

        ContentDisposition contentDisposition =
                ContentDisposition
                        .attachment()
                        .filename(
                                download.nomeOriginale(),
                                StandardCharsets.UTF_8
                        )
                        .build();

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                download.mimeType()
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .body(
                        download.resource()
                );
    }

    @DeleteMapping(
            "/allegati/{allegatoId}"
    )
    public ResponseEntity<Void>
    elimina(
            @PathVariable Long allegatoId,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        allegatoDocumentoService.elimina(
                allegatoId,
                utenteDetails
        );

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping(
            "/pratiche/{praticaId}/allegati"
    )
    public ResponseEntity<List<AllegatoDocumentoResponse>>
    trovaPerPratica(
            @PathVariable Long praticaId,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        return ResponseEntity.ok(
                allegatoDocumentoService
                        .trovaPerPratica(
                                praticaId,
                                utenteDetails
                        )
        );
    }
}
