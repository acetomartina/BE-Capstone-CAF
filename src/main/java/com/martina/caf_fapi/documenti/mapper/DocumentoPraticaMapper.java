package com.martina.caf_fapi.documenti.mapper;

import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.pratiche.dto.UtentePraticaResponse;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.stereotype.Component;

@Component
public class DocumentoPraticaMapper {

    public DocumentoPraticaResponse toResponse(
            DocumentoRichiestoPratica documento
    ) {
        return new DocumentoPraticaResponse(
                documento.getId(),
                documento.getPratica().getId(),
                documento.getPratica().getNumeroPratica(),
                documento.getEtichetta(),
                documento.getSuggerimento(),
                documento.getTipoObbligatorieta(),
                documento.getStato(),
                toUtenteResponse(documento.getRichiestoDa()),
                documento.getCreatoIl(),
                documento.getAggiornatoIl()
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