package com.martina.caf_fapi.servizi.controller;

import com.martina.caf_fapi.servizi.dto.MacroAreaResponse;
import com.martina.caf_fapi.servizi.dto.ServizioResponse;
import com.martina.caf_fapi.servizi.service.ServizioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')"
)
public class ServizioController {

    private final ServizioService servizioService;

    @GetMapping("/api/macro-aree")
    public ResponseEntity<List<MacroAreaResponse>>
    trovaMacroAreeAttive() {
        return ResponseEntity.ok(
                servizioService.trovaMacroAreeAttive()
        );
    }

    @GetMapping("/api/servizi")
    public ResponseEntity<List<ServizioResponse>>
    trovaServiziAttivi() {
        return ResponseEntity.ok(
                servizioService.trovaServiziAttivi()
        );
    }

    @GetMapping(
            "/api/macro-aree/{macroAreaId}/servizi"
    )
    public ResponseEntity<List<ServizioResponse>>
    trovaServiziPerMacroArea(
            @PathVariable Long macroAreaId
    ) {
        return ResponseEntity.ok(
                servizioService.trovaServiziPerMacroArea(
                        macroAreaId
                )
        );
    }

    @GetMapping("/api/servizi/{id}")
    public ResponseEntity<ServizioResponse>
    trovaServizioPerId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                servizioService.trovaServizioPerId(id)
        );
    }
}