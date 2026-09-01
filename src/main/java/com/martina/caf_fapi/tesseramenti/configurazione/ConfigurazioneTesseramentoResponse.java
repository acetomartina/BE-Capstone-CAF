package com.martina.caf_fapi.tesseramenti.configurazione;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfigurazioneTesseramentoResponse(
        BigDecimal quotaAnnuale,
        LocalDateTime aggiornatoIl
) {
}