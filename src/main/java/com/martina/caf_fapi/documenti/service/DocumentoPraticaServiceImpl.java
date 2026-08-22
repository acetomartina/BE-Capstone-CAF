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

        validaCambioStato(
                documento,
                request.stato()
        );

        documento.setStato(
                request.stato()
        );

        DocumentoRichiestoPratica salvato =
                documentoRichiestoPraticaRepository
                        .save(documento);

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
         * Nell'avanzamento entrano soltanto:
         *
         * - documenti obbligatori;
         * - documenti condizionali.
         *
         * I facoltativi non devono impedire alla
         * pratica di raggiungere il 100%.
         */
        long totaleRilevante =
                documenti.stream()
                        .filter(this::rilevantePerCompletamento)
                        .count();

        /*
         * Un documento rilevante è completato quando:
         *
         * - è VALIDATO;
         * - oppure è CONDIZIONALE ed è stato
         *   esplicitamente dichiarato NON_APPLICABILE.
         */
        long completati =
                documenti.stream()
                        .filter(this::completato)
                        .count();

        int percentualeCompletamento =
                totaleRilevante == 0
                        ? 100
                        : (int) Math.round(
                        completati
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

    private void validaCambioStato(
            DocumentoRichiestoPratica documento,
            StatoDocumentoPratica nuovoStato
    ) {
        if (
                nuovoStato
                        != StatoDocumentoPratica.NON_APPLICABILE
        ) {
            return;
        }

        if (
                documento.getTipoObbligatorieta()
                        != TipoObbligatorietaDocumento.CONDIZIONALE
        ) {
            throw new IllegalArgumentException(
                    "Solo un documento condizionale può essere impostato come non applicabile."
            );
        }
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
            return rilevantePerCompletamento(
                    documento
            );
        }

        return documento.getTipoObbligatorieta()
                == TipoObbligatorietaDocumento.CONDIZIONALE
                &&
                documento.getStato()
                        == StatoDocumentoPratica.NON_APPLICABILE;
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

    private DocumentoRichiestoPratica trovaDocumento(
            Long id
    ) {
        return documentoRichiestoPraticaRepository
                .findById(id)
                .orElseThrow(
                        () ->
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