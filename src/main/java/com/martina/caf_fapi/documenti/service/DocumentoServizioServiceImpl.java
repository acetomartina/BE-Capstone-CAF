package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CreateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoServizioResponse;
import com.martina.caf_fapi.documenti.dto.UpdateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoServizio;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoServizioRepository;
import com.martina.caf_fapi.servizi.repository.ServizioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoServizioServiceImpl
        implements DocumentoServizioService {

    private final DocumentoRichiestoServizioRepository documentoRepository;
    private final ServizioRepository servizioRepository;

    @Override
    public List<DocumentoServizioResponse> trovaDocumentiPerServizio(
            Long servizioId
    ) {
        verificaServizioEsistente(servizioId);

        return documentoRepository
                .findByServizioIdOrderByOrdineVisualizzazioneAsc(
                        servizioId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DocumentoServizioResponse creaDocumento(
            Long servizioId,
            CreateDocumentoServizioRequest request
    ) {
        verificaServizioEsistente(servizioId);

        DocumentoRichiestoServizio documento =
                DocumentoRichiestoServizio.builder()
                        .servizioId(servizioId)
                        .etichetta(request.etichetta())
                        .suggerimento(request.suggerimento())
                        .tipoObbligatorieta(
                                request.tipoObbligatorieta()
                        )
                        .visibileAlCliente(
                                request.visibileAlCliente() == null
                                        || request.visibileAlCliente()
                        )
                        .attivo(true)
                        .ordineVisualizzazione(
                                request.ordineVisualizzazione()
                        )
                        .build();

        return toResponse(
                documentoRepository.save(documento)
        );
    }

    @Override
    @Transactional
    public DocumentoServizioResponse aggiornaDocumento(
            Long documentoId,
            UpdateDocumentoServizioRequest request
    ) {
        DocumentoRichiestoServizio documento =
                trovaDocumento(documentoId);

        if (request.etichetta() != null) {
            documento.setEtichetta(
                    request.etichetta()
            );
        }

        if (request.suggerimento() != null) {
            documento.setSuggerimento(
                    request.suggerimento()
            );
        }

        if (request.tipoObbligatorieta() != null) {
            documento.setTipoObbligatorieta(
                    request.tipoObbligatorieta()
            );
        }

        if (request.attivo() != null) {
            documento.setAttivo(
                    request.attivo()
            );
        }

        if (request.visibileAlCliente() != null) {
            documento.setVisibileAlCliente(
                    request.visibileAlCliente()
            );
        }

        if (request.ordineVisualizzazione() != null) {
            documento.setOrdineVisualizzazione(
                    request.ordineVisualizzazione()
            );
        }

        return toResponse(
                documentoRepository.save(documento)
        );
    }

    @Override
    @Transactional
    public void disattivaDocumento(
            Long documentoId
    ) {
        DocumentoRichiestoServizio documento =
                trovaDocumento(documentoId);

        documento.setAttivo(false);

        documentoRepository.save(documento);
    }

    private DocumentoRichiestoServizio trovaDocumento(
            Long documentoId
    ) {
        return documentoRepository
                .findById(documentoId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Documento richiesto non trovato"
                        )
                );
    }

    private void verificaServizioEsistente(
            Long servizioId
    ) {
        if (!servizioRepository.existsById(servizioId)) {
            throw new EntityNotFoundException(
                    "Servizio non trovato"
            );
        }
    }

    private DocumentoServizioResponse toResponse(
            DocumentoRichiestoServizio documento
    ) {
        return new DocumentoServizioResponse(
                documento.getId(),
                documento.getServizioId(),
                documento.getEtichetta(),
                documento.getSuggerimento(),
                documento.isAttivo(),
                documento.isVisibileAlCliente(),
                documento.getTipoObbligatorieta(),
                documento.getOrdineVisualizzazione()
        );
    }
}