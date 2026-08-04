package com.martina.caf_fapi.clienti.controller;

import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import com.martina.caf_fapi.clienti.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
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
}