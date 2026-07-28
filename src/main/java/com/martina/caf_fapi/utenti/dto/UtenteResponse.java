package com.martina.caf_fapi.utenti.dto;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteResponse {

    private Long id;

    private String nome;

    private String cognome;

    private String codiceFiscale;

    private LocalDate dataNascita;

    private String luogoNascita;

    private String email;

    private String telefono;

    private String indirizzo;

    private String comune;

    private String provincia;

    private String cap;

    private Ruolo ruolo;

    private boolean attivo;

    private boolean emailVerificata;

    private String mansione;

    private String numeroMatricola;

    private String urlImmagineProfilo;

    private LocalDateTime ultimoAccesso;

    private LocalDateTime creatoIl;

    private LocalDateTime aggiornatoIl;
}