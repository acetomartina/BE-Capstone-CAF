package com.martina.caf_fapi.ricerca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RicercaGlobaleResponse {

    private List<RisultatoClienteRicercaResponse> clienti;

    /*
     * Questi verranno tipizzati quando collegheremo
     * anche pratiche e documenti.
     */
    private List<Object> pratiche;

    private List<Object> documenti;
}