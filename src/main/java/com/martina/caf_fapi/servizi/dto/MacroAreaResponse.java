package com.martina.caf_fapi.servizi.dto;

public record MacroAreaResponse(

        Long id,

        String nome,

        String slug,

        String descrizioneBreve,

        String chiaveIcona,

        String chiaveColore,

        Integer ordineVisualizzazione
) {
}