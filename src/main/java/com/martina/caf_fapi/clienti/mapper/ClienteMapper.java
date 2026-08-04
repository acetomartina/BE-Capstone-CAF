package com.martina.caf_fapi.clienti.mapper;

import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteResponse toResponse(Utente utente) {
        return new ClienteResponse(
                utente.getId(),
                utente.getNome(),
                utente.getCognome(),
                utente.getCodiceFiscale(),
                utente.getDataNascita(),
                utente.getLuogoNascita(),
                utente.getEmail(),
                utente.getTelefono(),
                utente.getIndirizzo(),
                utente.getComune(),
                utente.getProvincia(),
                utente.getCap(),
                utente.isAttivo(),
                utente.isEmailVerificata(),
                utente.getUrlImmagineProfilo(),
                utente.getCreatoIl(),
                utente.getAggiornatoIl()
        );
    }
}