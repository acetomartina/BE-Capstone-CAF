package com.martina.caf_fapi.servizi.controller;

import com.martina.caf_fapi.servizi.dto.MacroAreaResponse;
import com.martina.caf_fapi.servizi.dto.ServizioResponse;
import com.martina.caf_fapi.servizi.service.ServizioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class ServizioPublicController {

    private final ServizioService servizioService;

    @GetMapping("/macro-aree")
    public ResponseEntity<List<MacroAreaResponse>>
    trovaMacroAreeAttive() {
        return ResponseEntity.ok(
                servizioService.trovaMacroAreeAttive()
        );
    }

    @GetMapping("/servizi")
    public ResponseEntity<List<ServizioResponse>>
    trovaServiziAttivi() {
        return ResponseEntity.ok(
                servizioService.trovaServiziAttivi()
        );
    }

    @GetMapping(
            "/macro-aree/{macroAreaId}/servizi"
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
}