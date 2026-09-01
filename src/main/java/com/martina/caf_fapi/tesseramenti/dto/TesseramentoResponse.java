package com.martina.caf_fapi.tesseramenti.dto;

import com.martina.caf_fapi.tesseramenti.entity.StatoTesseramento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TesseramentoResponse(
        Long id,
        Long clienteId,
        LocalDate dataTesseramento,
        LocalDate dataScadenza,
        BigDecimal quota,
        String note,
        boolean annullato,
        StatoTesseramento stato,
        LocalDateTime creatoIl,
        LocalDateTime aggiornatoIl
) {
}