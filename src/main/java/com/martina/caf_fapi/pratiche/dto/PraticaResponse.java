package com.martina.caf_fapi.pratiche.dto;

import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PraticaResponse(

        Long id,

        String numeroPratica,

        UtentePraticaResponse cliente,

        Long servizioId,

        UtentePraticaResponse responsabile,

        String oggetto,

        String descrizione,

        StatoPratica stato,

        PrioritaPratica priorita,

        LocalDate dataScadenza,

        LocalDateTime chiusoIl,

        String note,

        LocalDateTime creatoIl,

        LocalDateTime aggiornatoIl
) {
}