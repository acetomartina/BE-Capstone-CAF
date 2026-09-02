package com.martina.caf_fapi.pratiche.controller;

import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiResponse;
import com.martina.caf_fapi.documenti.service.DocumentoPraticaService;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.service.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pratiche")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENTE')")
public class PraticaClienteController {

    private final PraticaService praticaService;

    private final DocumentoPraticaService
            documentoPraticaService;

    @GetMapping("/mie")
    public ResponseEntity<Page<PraticaResponse>> trovaMiePratiche(
            @AuthenticationPrincipal
            UtenteDetails utenteDetails,
            @ParameterObject
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                praticaService.trovaPerCliente(
                        utenteDetails.getId(),
                        pageable
                )
        );
    }

    @GetMapping("/mie/{id}")
    public ResponseEntity<PraticaResponse> trovaMiaPratica(
            @PathVariable
            Long id,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        return ResponseEntity.ok(
                praticaService.trovaPerIdDelCliente(
                        id,
                        utenteDetails.getId()
                )
        );
    }

    @GetMapping("/mie/{id}/documenti")
    public ResponseEntity<List<DocumentoPraticaResponse>>
    trovaDocumentiDellaMiaPratica(
            @PathVariable
            Long id,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        verificaAccessoAllaPratica(
                id,
                utenteDetails
        );

        return ResponseEntity.ok(
                documentoPraticaService
                        .trovaPerPratica(id)
        );
    }

    @GetMapping("/mie/{id}/documenti/riepilogo")
    public ResponseEntity<RiepilogoDocumentiResponse>
    riepilogoDocumentiDellaMiaPratica(
            @PathVariable
            Long id,
            @AuthenticationPrincipal
            UtenteDetails utenteDetails
    ) {
        verificaAccessoAllaPratica(
                id,
                utenteDetails
        );

        return ResponseEntity.ok(
                documentoPraticaService
                        .riepilogo(id)
        );
    }

    private void verificaAccessoAllaPratica(
            Long praticaId,
            UtenteDetails utenteDetails
    ) {
        /*
         * Il metodo cerca contemporaneamente la pratica
         * e il cliente autenticato. Se non coincidono,
         * restituisce una generica "Pratica non trovata".
         */
        praticaService.trovaPerIdDelCliente(
                praticaId,
                utenteDetails.getId()
        );
    }
}