package com.martina.caf_fapi.servizi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateServizioRequest(

        @Size(max = 150)
        String nome,

        @Size(max = 255)
        String descrizioneBreve,

        String descrizione,

        String destinatari,

        String requisiti,

        String comeFunziona,

        @DecimalMin(value = "0.00")
        BigDecimal prezzo,

        @Size(max = 255)
        String prezzoTesto,

        String notaPrezzo,

        @Min(1)
        Integer durataMinuti,

        Boolean prenotabile,

        Boolean richiedibileOnline,

        Boolean inEvidenza,

        Boolean generaPratica,

        Boolean richiedeDocumenti,

        @Min(0)
        Integer ordineVisualizzazione,

        Boolean attivo,

        LocalDate validoFinoAl
) {
}