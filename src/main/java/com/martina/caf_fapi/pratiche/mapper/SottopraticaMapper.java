package com.martina.caf_fapi.pratiche.mapper;

import com.martina.caf_fapi.pratiche.dto.SottopraticaResponse;
import com.martina.caf_fapi.pratiche.dto.UtentePraticaResponse;
import com.martina.caf_fapi.pratiche.entity.Sottopratica;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.stereotype.Component;

@Component
public class SottopraticaMapper {

    public SottopraticaResponse toResponse(
            Sottopratica sottopratica
    ) {
        return new SottopraticaResponse(
                sottopratica.getId(),
                sottopratica.getPratica().getId(),
                sottopratica.getPratica().getNumeroPratica(),
                sottopratica.getTitolo(),
                sottopratica.getDescrizione(),
                toUtenteResponse(
                        sottopratica.getOperatoreAssegnato()
                ),
                sottopratica.getStato(),
                sottopratica.getPriorita(),
                sottopratica.getDataScadenza(),
                sottopratica.getDataChiusura(),
                sottopratica.getNote(),
                sottopratica.getCreatoIl(),
                sottopratica.getAggiornatoIl()
        );
    }

    private UtentePraticaResponse toUtenteResponse(
            Utente utente
    ) {
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