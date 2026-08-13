package com.martina.caf_fapi.pratiche.dto;

import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SottopraticaResponse(

        Long id,

        Long praticaId,

        String numeroPratica,

        String titolo,

        String descrizione,

        UtentePraticaResponse operatoreAssegnato,

        StatoPratica stato,

        PrioritaPratica priorita,

        LocalDate dataScadenza,

        LocalDate dataChiusura,

        String note,

        LocalDateTime creatoIl,

        LocalDateTime aggiornatoIl
) {
}