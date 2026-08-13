package com.martina.caf_fapi.pratiche.controller;

import com.martina.caf_fapi.pratiche.dto.AggiornaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.CambiaStatoPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.CreaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.pratiche.service.PraticaService;
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
@RequestMapping("/api/pratiche")
@RequiredArgsConstructor
@PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')"
)
public class PraticaController {

    private final PraticaService praticaService;

    @GetMapping
    public ResponseEntity<Page<PraticaResponse>>
    trovaTutte(
            @RequestParam(
                    required = false
            )
            String q,

            @RequestParam(
                    required = false
            )
            StatoPratica stato,

            @RequestParam(
                    required = false
            )
            Long servizioId,

            @RequestParam(
                    required = false
            )
            Long responsabileId,

            @ParameterObject
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                praticaService.cerca(
                        q,
                        stato,
                        servizioId,
                        responsabileId,
                        pageable
                )
        );
    }

    @GetMapping(
            "/cliente/{clienteId}"
    )
    public ResponseEntity<Page<PraticaResponse>>
    trovaPerCliente(
            @PathVariable
            Long clienteId,

            @ParameterObject
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                praticaService
                        .trovaPerCliente(
                                clienteId,
                                pageable
                        )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PraticaResponse>
    trovaPerId(
            @PathVariable
            Long id
    ) {
        return ResponseEntity.ok(
                praticaService
                        .trovaPerId(id)
        );
    }

    @PostMapping
    public ResponseEntity<PraticaResponse>
    creaPratica(
            @Valid
            @RequestBody
            CreaPraticaRequest request
    ) {
        PraticaResponse praticaCreata =
                praticaService
                        .creaPratica(
                                request
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        praticaCreata
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PraticaResponse>
    aggiornaPratica(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            AggiornaPraticaRequest request
    ) {
        return ResponseEntity.ok(
                praticaService
                        .aggiornaPratica(
                                id,
                                request
                        )
        );
    }

    @PatchMapping(
            "/{id}/stato"
    )
    public ResponseEntity<PraticaResponse>
    cambiaStato(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            CambiaStatoPraticaRequest request
    ) {
        return ResponseEntity.ok(
                praticaService
                        .cambiaStato(
                                id,
                                request
                        )
        );
    }
}