package com.martina.caf_fapi.pratiche.controller;

import com.martina.caf_fapi.pratiche.dto.CreaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.service.PraticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pratiche")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
public class PraticaController {

    private final PraticaService praticaService;

    @GetMapping
    public ResponseEntity<Page<PraticaResponse>> trovaTutte(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                praticaService.trovaTutte(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PraticaResponse> trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                praticaService.trovaPerId(id)
        );
    }

    @PostMapping
    public ResponseEntity<PraticaResponse> creaPratica(
            @Valid @RequestBody CreaPraticaRequest request
    ) {
        PraticaResponse praticaCreata =
                praticaService.creaPratica(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(praticaCreata);
    }
}