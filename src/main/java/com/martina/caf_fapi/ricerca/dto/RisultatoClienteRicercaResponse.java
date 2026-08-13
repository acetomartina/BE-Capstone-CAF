package com.martina.caf_fapi.ricerca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RisultatoClienteRicercaResponse {

    private Long id;

    private String nome;

    private String cognome;

    private String codiceFiscale;

    private String email;

    private String telefono;

    private boolean attivo;
}