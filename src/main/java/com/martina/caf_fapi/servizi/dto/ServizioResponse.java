package com.martina.caf_fapi.servizi.dto;

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

        String prezzoTesto,

        String notaPrezzo,

        Integer durataMinuti,

        boolean prenotabile,

        boolean richiedibileOnline,

        boolean inEvidenza,

        Integer ordineVisualizzazione
) {
}