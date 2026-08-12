package com.martina.caf_fapi.pratiche.dto;

public record UtentePraticaResponse(
        Long id,
        String nome,
        String cognome,
        String email
) {
}