package com.martina.caf_fapi.tesseramenti.controller;

import com.martina.caf_fapi.tesseramenti.dto.CreaTesseramentoRequest;
import com.martina.caf_fapi.tesseramenti.dto.TesseramentoResponse;
import com.martina.caf_fapi.tesseramenti.service.TesseramentoService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clienti/{clienteId}/tesseramenti")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
public class TesseramentoController {

    private final TesseramentoService
            tesseramentoService;

    @PostMapping
    public ResponseEntity<TesseramentoResponse>
    crea(
            @PathVariable Long clienteId,
            @Valid @RequestBody
            CreaTesseramentoRequest request
    ) {
        return ResponseEntity
                .status(201)
                .body(
                        tesseramentoService.crea(
                                clienteId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<TesseramentoResponse>>
    trovaStorico(
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(
                tesseramentoService
                        .trovaStoricoCliente(
                                clienteId
                        )
        );
    }

    @GetMapping("/corrente")
    public ResponseEntity<TesseramentoResponse>
    trovaCorrente(
            @PathVariable Long clienteId
    ) {
        return tesseramentoService
                .trovaCorrenteCliente(clienteId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent()
                                .build()
                );
    }
}