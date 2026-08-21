package com.martina.caf_fapi.servizi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ServizioResponse(

        Long id,

        Long macroAreaId,

        String macroAreaNome,

        Long partnerId,

        String nome,

        String slug,

        String descrizioneBreve,

        String descrizione,

        String destinatari,

        String requisiti,

        String comeFunziona,

        BigDecimal prezzo,

        String prezzoTesto,

        String notaPrezzo,

        Integer durataMinuti,

        boolean prenotabile,

        boolean richiedibileOnline,

        boolean inEvidenza,

        boolean generaPratica,

        boolean richiedeDocumenti,

        Integer ordineVisualizzazione,

        boolean attivo,

        LocalDate validoFinoAl
) {
}