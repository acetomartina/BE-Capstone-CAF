package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoAdminResponse;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiAdminResponse;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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

    private final DocumentoPraticaMapper
            documentoPraticaMapper;

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

        statoAutomaticoPraticaService
                .ricalcolaDaDocumenti(
                        pratica.getId()
                );
    }

    @Override
    public Page<DocumentoAdminResponse> trovaTutti(
            String termine,
            StatoDocumentoPratica stato,
            TipoObbligatorietaDocumento tipoObbligatorieta,
            Pageable pageable
    ) {
        String termineNormalizzato =
                termine == null
                        ? ""
                        : termine
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return documentoRichiestoPraticaRepository
                .cercaPerAmministrazione(
                        termineNormalizzato,
                        stato,
                        tipoObbligatorieta,
                        pageable
                )
                .map(
                        documentoPraticaMapper::toAdminResponse
                );
    }

    @Override
    public RiepilogoDocumentiAdminResponse riepilogoAdmin() {
        long totale =
                documentoRichiestoPraticaRepository
                        .countByPraticaEliminatoFalse();

        long mancanti = contaDocumentiAdmin(
                StatoDocumentoPratica.MANCANTE
        );

        long ricevuti = contaDocumentiAdmin(
                StatoDocumentoPratica.RICEVUTO
        );

        long daVerificare = contaDocumentiAdmin(
                StatoDocumentoPratica.DA_VERIFICARE
        );

        long validati = contaDocumentiAdmin(
                StatoDocumentoPratica.VALIDATO
        );

        long rifiutati = contaDocumentiAdmin(
                StatoDocumentoPratica.RIFIUTATO
        );

        long nonApplicabili = contaDocumentiAdmin(
                StatoDocumentoPratica.NON_APPLICABILE
        );

        return new RiepilogoDocumentiAdminResponse(
                totale,
                mancanti,
                ricevuti,
                daVerificare,
                validati,
                rifiutati,
                nonApplicabili
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

        long totale = documenti.size();

        long mancanti = contaStato(
                documenti,
                StatoDocumentoPratica.MANCANTE
        );

        long ricevuti = contaStato(
                documenti,
                StatoDocumentoPratica.RICEVUTO
        );

        long daVerificare = contaStato(
                documenti,
                StatoDocumentoPratica.DA_VERIFICARE
        );

        long validati = contaStato(
                documenti,
                StatoDocumentoPratica.VALIDATO
        );

        long rifiutati = contaStato(
                documenti,
                StatoDocumentoPratica.RIFIUTATO
        );

        long nonApplicabili = contaStato(
                documenti,
                StatoDocumentoPratica.NON_APPLICABILE
        );

        List<DocumentoRichiestoPratica> documentiRilevanti =
                documenti.stream()
                        .filter(
                                this::rilevantePerCompletamento
                        )
                        .toList();

        long totaleRilevante =
                documentiRilevanti.size();

        long completati =
                documentiRilevanti.stream()
                        .filter(
                                this::completato
                        )
                        .count();

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

    private long contaDocumentiAdmin(
            StatoDocumentoPratica stato
    ) {
        return documentoRichiestoPraticaRepository
                .countByStatoAndPraticaEliminatoFalse(
                        stato
                );
    }

    private long contaStato(
            List<DocumentoRichiestoPratica> documenti,
            StatoDocumentoPratica stato
    ) {
        return documenti.stream()
                .filter(
                        documento ->
                                documento.getStato() == stato
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
        return switch (documento.getStato()) {
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