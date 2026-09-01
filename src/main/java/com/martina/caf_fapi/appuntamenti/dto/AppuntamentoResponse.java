package com.martina.caf_fapi.appuntamenti.dto;

import com.martina.caf_fapi.appuntamenti.enums.ModalitaAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.TipologiaAppuntamento;

import java.time.LocalDateTime;

public record AppuntamentoResponse(

        Long id,

        Long clienteId,
        String clienteNome,
        String clienteCognome,
        String clienteCodiceFiscale,

        Long praticaId,
        String numeroPratica,
        String oggettoPratica,

        Long servizioId,
        String servizioNome,

        Long responsabileId,
        String responsabileNome,
        String responsabileCognome,

        String titolo,
        String descrizione,

        TipologiaAppuntamento tipologia,
        ModalitaAppuntamento modalita,
        StatoAppuntamento stato,

        LocalDateTime inizio,
        LocalDateTime fine,

        String luogo,
        String linkOnline,
        String note,
        String motivoAnnullamento,

        LocalDateTime creatoIl,
        LocalDateTime aggiornatoIl

) {
}