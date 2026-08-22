package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiResponse;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoServizio;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import com.martina.caf_fapi.documenti.mapper.DocumentoPraticaMapper;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoPraticaRepository;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoServizioRepository;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.pratiche.service.StatoAutomaticoPraticaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoPraticaServiceImpl
        implements DocumentoPraticaService {

    private final DocumentoRichiestoServizioRepository
            documentoRichiestoServizioRepository;

    private final DocumentoRichiestoPraticaRepository
            documentoRichiestoPraticaRepository;

    private final PraticaRepository praticaRepository;

    private final DocumentoPraticaMapper documentoPraticaMapper;

    private final StatoAutomaticoPraticaService
            statoAutomaticoPraticaService;

    @Override
    @Transactional
    public void generaChecklistDaServizio(
            Pratica pratica
    ) {
        List<DocumentoRichiestoServizio> documentiStandard =
                documentoRichiestoServizioRepository
                        .findByServizioIdAndAttivoTrueOrderByOrdineVisualizzazioneAsc(
                                pratica.getServizio().getId()
                        );

        if (documentiStandard.isEmpty()) {
            return;
        }

        List<DocumentoRichiestoPratica> checklist =
                documentiStandard.stream()
                        .map(documento ->
                                DocumentoRichiestoPratica.builder()
                                        .pratica(pratica)
                                        .etichetta(
                                                documento.getEtichetta()
                                        )
                                        .suggerimento(
                                                documento.getSuggerimento()
                                        )
                                        .tipoObbligatorieta(
                                                documento.getTipoObbligatorieta()
                                        )
                                        .stato(
                                                StatoDocumentoPratica.MANCANTE
                                        )
                                        .build()
                        )
                        .toList();

        documentoRichiestoPraticaRepository
                .saveAll(checklist);

        /*
         * Una volta generata la checklist,
         * rivalutiamo automaticamente lo stato
         * della pratica.
         */
        statoAutomaticoPraticaService
                .ricalcolaDaDocumenti(
                        pratica.getId()
                );
    }

    @Override
    public List<DocumentoPraticaResponse> trovaPerPratica(
            Long praticaId
    ) {
        verificaPratica(praticaId);

        return documentoRichiestoPraticaRepository
                .findByPraticaIdOrderByIdAsc(praticaId)
                .stream()
                .map(documentoPraticaMapper::toResponse)
                .toList();
    }

    @Override
    public DocumentoPraticaResponse trovaPerId(
            Long id
    ) {
        return documentoPraticaMapper.toResponse(
                trovaDocumento(id)
        );
    }

    @Override
    @Transactional
    public DocumentoPraticaResponse cambiaStato(
            Long id,
            CambiaStatoDocumentoRequest request
    ) {
        DocumentoRichiestoPratica documento =
                trovaDocumento(id);

        documento.setStato(
                request.stato()
        );

        DocumentoRichiestoPratica salvato =
                documentoRichiestoPraticaRepository
                        .save(documento);

        /*
         * Ogni modifica allo stato di un documento
         * può influenzare lo stato della pratica.
         */
        statoAutomaticoPraticaService
                .ricalcolaDaDocumenti(
                        documento
                                .getPratica()
                                .getId()
                );

        return documentoPraticaMapper.toResponse(
                salvato
        );
    }

    @Override
    public RiepilogoDocumentiResponse riepilogo(
            Long praticaId
    ) {
        verificaPratica(praticaId);

        List<DocumentoRichiestoPratica> documenti =
                documentoRichiestoPraticaRepository
                        .findByPraticaIdOrderByIdAsc(
                                praticaId
                        );

        long totale =
                documenti.size();

        long mancanti =
                contaStato(
                        documenti,
                        StatoDocumentoPratica.MANCANTE
                );

        long ricevuti =
                contaStato(
                        documenti,
                        StatoDocumentoPratica.RICEVUTO
                );

        long daVerificare =
                contaStato(
                        documenti,
                        StatoDocumentoPratica.DA_VERIFICARE
                );

        long validati =
                contaStato(
                        documenti,
                        StatoDocumentoPratica.VALIDATO
                );

        long rifiutati =
                contaStato(
                        documenti,
                        StatoDocumentoPratica.RIFIUTATO
                );

        long nonApplicabili =
                contaStato(
                        documenti,
                        StatoDocumentoPratica.NON_APPLICABILE
                );

        /*
         * Per completamento e percentuale consideriamo
         * soltanto i documenti che possono realmente
         * bloccare l'avanzamento della pratica.
         *
         * I FACOLTATIVI restano visibili nei contatori
         * generali ma non incidono sulla percentuale.
         */
        List<DocumentoRichiestoPratica> documentiRilevanti =
                documenti.stream()
                        .filter(
                                this::rilevantePerCompletamento
                        )
                        .toList();

        long totaleRilevante =
                documentiRilevanti.size();

        /*
         * "Completati" indica quanti documenti
         * sono definitivamente risolti.
         *
         * VALIDATO = completato.
         * NON_APPLICABILE = completato solo
         * per un documento condizionale.
         */
        long completati =
                documentiRilevanti.stream()
                        .filter(
                                this::completato
                        )
                        .count();

        /*
         * La percentuale rappresenta invece
         * l'avanzamento operativo.
         *
         * MANCANTE       ->   0%
         * RIFIUTATO      ->   0%
         * RICEVUTO       ->  35%
         * DA_VERIFICARE  ->  65%
         * VALIDATO       -> 100%
         * NON_APPLICABILE-> 100%
         */
        double avanzamentoTotale =
                documentiRilevanti.stream()
                        .mapToDouble(
                                this::punteggioAvanzamento
                        )
                        .sum();

        int percentualeCompletamento =
                totaleRilevante == 0
                        ? 100
                        : (int) Math.round(
                        avanzamentoTotale
                        * 100.0
                        / totaleRilevante
                );

        return new RiepilogoDocumentiResponse(
                totale,
                mancanti,
                ricevuti,
                daVerificare,
                validati,
                rifiutati,
                nonApplicabili,
                completati,
                percentualeCompletamento
        );
    }

    private long contaStato(
            List<DocumentoRichiestoPratica> documenti,
            StatoDocumentoPratica stato
    ) {
        return documenti.stream()
                .filter(
                        documento ->
                                documento.getStato()
                                        == stato
                )
                .count();
    }

    private boolean rilevantePerCompletamento(
            DocumentoRichiestoPratica documento
    ) {
        return documento.getTipoObbligatorieta()
                != TipoObbligatorietaDocumento.FACOLTATIVO;
    }

    private boolean completato(
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

    private double punteggioAvanzamento(
            DocumentoRichiestoPratica documento
    ) {
        return switch (
                documento.getStato()
                ) {
            case MANCANTE,
                 RIFIUTATO -> 0.0;

            case RICEVUTO -> 0.35;

            case DA_VERIFICARE -> 0.65;

            case VALIDATO,
                 NON_APPLICABILE -> 1.0;
        };
    }

    private DocumentoRichiestoPratica trovaDocumento(
            Long id
    ) {
        return documentoRichiestoPraticaRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Documento richiesto non trovato"
                        )
                );
    }

    private void verificaPratica(
            Long praticaId
    ) {
        if (
                praticaRepository
                        .findByIdAndEliminatoFalse(
                                praticaId
                        )
                        .isEmpty()
        ) {
            throw new EntityNotFoundException(
                    "Pratica non trovata"
            );
        }
    }
}