package com.martina.caf_fapi.documenti.dto;

public record RiepilogoDocumentiAdminResponse(
        long totale,
        long mancanti,
        long ricevuti,
        long daVerificare,
        long validati,
        long rifiutati,
        long nonApplicabili
) {
}