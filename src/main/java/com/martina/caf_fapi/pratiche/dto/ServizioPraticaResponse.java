package com.martina.caf_fapi.pratiche.dto;

public record ServizioPraticaResponse(

        Long id,

        String nome,

        String slug,

        Long macroAreaId,

        String macroAreaNome
) {
}