package com.martina.caf_fapi.appuntamenti.service;

import com.martina.caf_fapi.appuntamenti.dto.AggiornaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.dto.AppuntamentoResponse;
import com.martina.caf_fapi.appuntamenti.dto.CambiaStatoAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.dto.CreaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento;

import java.time.LocalDateTime;
import java.util.List;

public interface AppuntamentoService {

    List<AppuntamentoResponse> trovaTutti(
            LocalDateTime dal,
            LocalDateTime al,
            Long clienteId,
            Long responsabileId,
            StatoAppuntamento stato
    );

    AppuntamentoResponse trovaPerId(
            Long id
    );

    AppuntamentoResponse crea(
            CreaAppuntamentoRequest request
    );

    AppuntamentoResponse aggiorna(
            Long id,
            AggiornaAppuntamentoRequest request
    );

    AppuntamentoResponse cambiaStato(
            Long id,
            CambiaStatoAppuntamentoRequest request
    );

    void elimina(
            Long id
    );
}