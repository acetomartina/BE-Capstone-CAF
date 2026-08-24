package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CreateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoServizioResponse;
import com.martina.caf_fapi.documenti.dto.RiordinaDocumentiServizioRequest;
import com.martina.caf_fapi.documenti.dto.UpdateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoServizio;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoServizioRepository;
import com.martina.caf_fapi.servizi.repository.ServizioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    @Transactional
    public List<DocumentoServizioResponse> riordinaDocumenti(
            Long servizioId,
            RiordinaDocumentiServizioRequest request
    ) {
        verificaServizioEsistente(servizioId);

        List<Long> documentoIds = request.documentoIds();

        // 1. Verifica che non ci siano ID duplicati
        Set<Long> idsUnivoci = new HashSet<>(documentoIds);

        if (idsUnivoci.size() != documentoIds.size()) {
            throw new IllegalArgumentException(
                    "La lista contiene documenti duplicati"
            );
        }

        // 2. Recupera TUTTI i documenti configurati per il servizio
        List<DocumentoRichiestoServizio> documentiServizio =
                documentoRepository
                        .findByServizioIdOrderByOrdineVisualizzazioneAsc(
                                servizioId
                        );

        // 3. Il frontend deve inviare l'intera checklist
        if (documentiServizio.size() != documentoIds.size()) {
            throw new IllegalArgumentException(
                    "La lista deve contenere tutti i documenti del servizio"
            );
        }

        // 4. Recupera i documenti indicati nella richiesta
        List<DocumentoRichiestoServizio> documenti =
                documentoRepository.findAllById(documentoIds);

        // 5. Verifica che tutti gli ID esistano
        if (documenti.size() != documentoIds.size()) {
            throw new EntityNotFoundException(
                    "Uno o più documenti non sono stati trovati"
            );
        }

        // 6. Verifica che appartengano tutti al servizio
        boolean documentoDiAltroServizio =
                documenti.stream()
                        .anyMatch(documento ->
                                !documento.getServizioId()
                                        .equals(servizioId)
                        );

        if (documentoDiAltroServizio) {
            throw new IllegalArgumentException(
                    "Uno o più documenti non appartengono al servizio indicato"
            );
        }

        // 7. Indicizziamo i documenti per ID
        var documentiPerId = documenti.stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                DocumentoRichiestoServizio::getId,
                                documento -> documento
                        )
                );

        // 8. La posizione nell'array diventa l'ordine
        for (int i = 0; i < documentoIds.size(); i++) {
            Long documentoId = documentoIds.get(i);

            DocumentoRichiestoServizio documento =
                    documentiPerId.get(documentoId);

            documento.setOrdineVisualizzazione(i + 1);
        }

        documentoRepository.saveAll(documenti);

        // 9. Restituiamo la checklist nel nuovo ordine
        return documentoRepository
                .findByServizioIdOrderByOrdineVisualizzazioneAsc(
                        servizioId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<DocumentoServizioResponse>
    trovaDocumentiPubbliciPerServizio(
            Long servizioId
    ) {
        verificaServizioEsistente(servizioId);

        return documentoRepository
                .findByServizioIdAndAttivoTrueAndVisibileAlClienteTrueOrderByOrdineVisualizzazioneAsc(
                        servizioId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }
}