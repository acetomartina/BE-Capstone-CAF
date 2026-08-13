package com.martina.caf_fapi.documenti.dto;

public record RiepilogoDocumentiResponse(

        long totale,

        long mancanti,

        long ricevuti,

        long daVerificare,

        long validati,

        long rifiutati,

        long completati,

        int percentualeCompletamento

) {
}