package com.martina.caf_fapi.ricerca.controller;

import com.martina.caf_fapi.ricerca.dto.RicercaGlobaleResponse;
import com.martina.caf_fapi.ricerca.service.RicercaGlobaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ricerca-globale")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
public class RicercaGlobaleController {

    private final RicercaGlobaleService ricercaGlobaleService;

    @GetMapping
    public ResponseEntity<RicercaGlobaleResponse> cerca(
            @RequestParam String q
    ) {
        return ResponseEntity.ok(
                ricercaGlobaleService.cerca(q)
        );
    }
}