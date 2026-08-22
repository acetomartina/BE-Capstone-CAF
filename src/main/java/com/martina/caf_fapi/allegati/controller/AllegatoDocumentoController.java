package com.martina.caf_fapi.allegati.controller;

import com.martina.caf_fapi.allegati.dto.AllegatoDocumentoResponse;
import com.martina.caf_fapi.allegati.service.AllegatoDocumentoService;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
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
                        utenteDetails.getId()
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
            @PathVariable Long documentoId
    ) {
        return ResponseEntity.ok(
                allegatoDocumentoService
                        .trovaPerDocumento(
                                documentoId
                        )
        );
    }

    @GetMapping(
            "/allegati/{allegatoId}/download"
    )
    public ResponseEntity<Resource>
    scarica(
            @PathVariable Long allegatoId
    ) {
        AllegatoDocumentoService.DownloadAllegato download =
                allegatoDocumentoService.scarica(
                        allegatoId
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
            @PathVariable Long allegatoId
    ) {
        allegatoDocumentoService.elimina(
                allegatoId
        );

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping(
            "/pratiche/{praticaId}/allegati"
    )
    public ResponseEntity<List<AllegatoDocumentoResponse>>
    trovaPerPratica(
            @PathVariable Long praticaId
    ) {
        return ResponseEntity.ok(
                allegatoDocumentoService
                        .trovaPerPratica(
                                praticaId
                        )
        );
    }
}