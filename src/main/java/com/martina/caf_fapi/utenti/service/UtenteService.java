package com.martina.caf_fapi.utenti.service;

import com.martina.caf_fapi.utenti.dto.CreaUtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UtenteService {

    UtenteResponse creaUtente(CreaUtenteRequest request);

    UtenteResponse trovaPerId(Long id);

    Page<UtenteResponse> trovaTutti(Pageable pageable);

    Page<UtenteResponse> trovaPerRuolo(
            Ruolo ruolo,
            Pageable pageable
    );

    UtenteResponse aggiornaUtente(
            Long id,
            UtenteUpdateRequest request
    );

    UtenteResponse cambiaRuolo(
            Long id,
            Ruolo nuovoRuolo
    );

    UtenteResponse attivaUtente(Long id);

    UtenteResponse disattivaUtente(Long id);
}