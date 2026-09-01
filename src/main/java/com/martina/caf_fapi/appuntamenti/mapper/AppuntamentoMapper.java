package com.martina.caf_fapi.appuntamenti.mapper;

import com.martina.caf_fapi.appuntamenti.dto.AppuntamentoResponse;
import com.martina.caf_fapi.appuntamenti.entity.Appuntamento;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.servizi.entity.Servizio;
import com.martina.caf_fapi.utenti.entity.Utente;

import org.springframework.stereotype.Component;

@Component
public class AppuntamentoMapper {

    public AppuntamentoResponse toResponse(
            Appuntamento appuntamento
    ) {
        Utente cliente =
                appuntamento.getCliente();

        Utente responsabile =
                appuntamento.getResponsabile();

        Pratica pratica =
                appuntamento.getPratica();

        Servizio servizio =
                appuntamento.getServizio();

        if (
                servizio == null &&
                        pratica != null
        ) {
            servizio =
                    pratica.getServizio();
        }

        return new AppuntamentoResponse(
                appuntamento.getId(),

                cliente.getId(),
                cliente.getNome(),
                cliente.getCognome(),
                cliente.getCodiceFiscale(),

                pratica != null
                        ? pratica.getId()
                        : null,

                pratica != null
                        ? pratica.getNumeroPratica()
                        : null,

                pratica != null
                        ? pratica.getOggetto()
                        : null,

                servizio != null
                        ? servizio.getId()
                        : null,

                servizio != null
                        ? servizio.getNome()
                        : null,

                responsabile != null
                        ? responsabile.getId()
                        : null,

                responsabile != null
                        ? responsabile.getNome()
                        : null,

                responsabile != null
                        ? responsabile.getCognome()
                        : null,

                appuntamento.getTitolo(),
                appuntamento.getDescrizione(),

                appuntamento.getTipologia(),
                appuntamento.getModalita(),
                appuntamento.getStato(),

                appuntamento.getInizio(),
                appuntamento.getFine(),

                appuntamento.getLuogo(),
                appuntamento.getLinkOnline(),
                appuntamento.getNote(),
                appuntamento
                        .getMotivoAnnullamento(),

                appuntamento.getCreatoIl(),
                appuntamento.getAggiornatoIl()
        );
    }
}