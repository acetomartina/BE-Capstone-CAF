package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiResponse;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoServizio;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
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

        documento.setStato(request.stato());

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

        long totale =
                documentoRichiestoPraticaRepository
                        .countByPraticaId(praticaId);

        long mancanti = contaStato(
                praticaId,
                StatoDocumentoPratica.MANCANTE
        );

        long ricevuti = contaStato(
                praticaId,
                StatoDocumentoPratica.RICEVUTO
        );

        long daVerificare = contaStato(
                praticaId,
                StatoDocumentoPratica.DA_VERIFICARE
        );

        long validati = contaStato(
                praticaId,
                StatoDocumentoPratica.VALIDATO
        );

        long rifiutati = contaStato(
                praticaId,
                StatoDocumentoPratica.RIFIUTATO
        );

        long completati =
                ricevuti
                        + daVerificare
                        + validati;

        int percentualeCompletamento =
                totale == 0
                        ? 0
                        : (int) Math.round(
                        completati * 100.0 / totale
                );

        return new RiepilogoDocumentiResponse(
                totale,
                mancanti,
                ricevuti,
                daVerificare,
                validati,
                rifiutati,
                completati,
                percentualeCompletamento
        );
    }

    private long contaStato(
            Long praticaId,
            StatoDocumentoPratica stato
    ) {
        return documentoRichiestoPraticaRepository
                .countByPraticaIdAndStato(
                        praticaId,
                        stato
                );
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
                        .findByIdAndEliminatoFalse(praticaId)
                        .isEmpty()
        ) {
            throw new EntityNotFoundException(
                    "Pratica non trovata"
            );
        }
    }
}