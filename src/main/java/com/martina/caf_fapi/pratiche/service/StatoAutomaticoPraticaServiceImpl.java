package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoPraticaRepository;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatoAutomaticoPraticaServiceImpl
        implements StatoAutomaticoPraticaService {

    private final PraticaRepository praticaRepository;

    private final DocumentoRichiestoPraticaRepository
            documentoRichiestoPraticaRepository;

    @Override
    @Transactional
    public void ricalcolaDaDocumenti(
            Long praticaId
    ) {
        Pratica pratica =
                praticaRepository
                        .findByIdAndEliminatoFalse(
                                praticaId
                        )
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Pratica non trovata"
                                        )
                        );

        if (
                statoProtetto(
                        pratica.getStato()
                )
        ) {
            return;
        }

        documentoRichiestoPraticaRepository
                .flush();

        List<DocumentoRichiestoPratica> documenti =
                documentoRichiestoPraticaRepository
                        .findByPraticaIdOrderByIdAsc(
                                praticaId
                        );

        boolean esisteDocumentoRilevanteNonRisolto =
                documenti.stream()
                        .filter(
                                this::rilevante
                        )
                        .anyMatch(
                                documento ->
                                        !risolto(
                                                documento
                                        )
                        );

        StatoPratica nuovoStato =
                esisteDocumentoRilevanteNonRisolto
                        ? StatoPratica.IN_ATTESA_DOCUMENTI
                        : StatoPratica.IN_LAVORAZIONE;

        if (
                pratica.getStato()
                        == nuovoStato
        ) {
            return;
        }

        pratica.setStato(
                nuovoStato
        );

        pratica.setChiusoIl(
                null
        );

        praticaRepository.save(
                pratica
        );
    }

    private boolean statoProtetto(
            StatoPratica stato
    ) {
        return stato == StatoPratica.BOZZA
                || stato == StatoPratica.IN_ATTESA_CLIENTE
                || stato == StatoPratica.IN_ATTESA_ENTE
                || stato == StatoPratica.COMPLETATA
                || stato == StatoPratica.ANNULLATA;
    }

    private boolean rilevante(
            DocumentoRichiestoPratica documento
    ) {
        return documento.getTipoObbligatorieta()
                != TipoObbligatorietaDocumento.FACOLTATIVO;
    }

    private boolean risolto(
            DocumentoRichiestoPratica documento
    ) {
        if (
                documento.getStato()
                        == StatoDocumentoPratica.VALIDATO
        ) {
            return true;
        }

        return documento.getTipoObbligatorieta()
                == TipoObbligatorietaDocumento.CONDIZIONALE
                &&
                documento.getStato()
                        == StatoDocumentoPratica.NON_APPLICABILE;
    }
}