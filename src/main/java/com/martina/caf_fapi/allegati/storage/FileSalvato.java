package com.martina.caf_fapi.allegati.storage;

public record FileSalvato(
        String nomeOriginale,
        String nomeStorage,
        String mimeType,
        long dimensione
) {
}