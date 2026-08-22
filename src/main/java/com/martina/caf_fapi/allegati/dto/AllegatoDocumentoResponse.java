package com.martina.caf_fapi.allegati.dto;

import java.time.LocalDateTime;

public record AllegatoDocumentoResponse(
        Long id,
        Long documentoPraticaId,
        String nomeOriginale,
        String mimeType,
        Long dimensione,
        Long caricatoDaId,
        String caricatoDaNome,
        String caricatoDaCognome,
        LocalDateTime caricatoIl
) {
}