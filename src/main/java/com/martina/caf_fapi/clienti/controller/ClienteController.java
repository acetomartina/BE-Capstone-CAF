package com.martina.caf_fapi.clienti.controller;

import com.martina.caf_fapi.clienti.dto.AggiornaClienteRequest;
import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import com.martina.caf_fapi.clienti.service.ClienteService;
import com.martina.caf_fapi.clienti.dto.CreaClienteRequest;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> trovaTutti(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                clienteService.trovaTutti(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> trovaPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                clienteService.trovaPerId(id)
        );
    }

    @GetMapping("/ricerca/cognome")
    public ResponseEntity<Page<ClienteResponse>> cercaPerCognome(
            @RequestParam String cognome,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                clienteService.cercaPerCognome(
                        cognome,
                        pageable
                )
        );
    }

    @GetMapping("/ricerca/codice-fiscale")
    public ResponseEntity<Page<ClienteResponse>> cercaPerCodiceFiscale(
            @RequestParam String codiceFiscale,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                clienteService.cercaPerCodiceFiscale(
                        codiceFiscale,
                        pageable
                )
        );
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> creaCliente(
            @Valid @RequestBody CreaClienteRequest request
    ) {
        ClienteResponse clienteCreato =
                clienteService.creaCliente(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteCreato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> aggiornaCliente(
            @PathVariable Long id,
            @Valid @RequestBody AggiornaClienteRequest request
    ) {
        return ResponseEntity.ok(
                clienteService.aggiornaCliente(id, request)
        );
    }
}