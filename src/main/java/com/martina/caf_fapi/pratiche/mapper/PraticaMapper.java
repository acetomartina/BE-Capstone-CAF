package com.martina.caf_fapi.pratiche.mapper;

import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.dto.UtentePraticaResponse;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.stereotype.Component;

@Component
public class PraticaMapper {

    public PraticaResponse toResponse(Pratica pratica) {
        return new PraticaResponse(
                pratica.getId(),
                pratica.getNumeroPratica(),
                toUtenteResponse(pratica.getCliente()),
                pratica.getServizioId(),
                toUtenteResponse(pratica.getResponsabile()),
                pratica.getOggetto(),
                pratica.getDescrizione(),
                pratica.getStato(),
                pratica.getPriorita(),
                pratica.getDataScadenza(),
                pratica.getChiusoIl(),
                pratica.getNote(),
                pratica.getCreatoIl(),
                pratica.getAggiornatoIl()
        );
    }

    private UtentePraticaResponse toUtenteResponse(Utente utente) {
        if (utente == null) {
            return null;
        }

        return new UtentePraticaResponse(
                utente.getId(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail()
        );
    }
}