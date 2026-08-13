package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoServizio;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoPraticaRepository;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoServizioRepository;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoPraticaServiceImpl
        implements DocumentoPraticaService {

    private final DocumentoRichiestoServizioRepository
            documentoRichiestoServizioRepository;

    private final DocumentoRichiestoPraticaRepository
            documentoRichiestoPraticaRepository;

    @Override
    @Transactional
    public void generaChecklistDaServizio(
            Pratica pratica
    ) {

        List<DocumentoRichiestoServizio> documentiStandard =
                documentoRichiestoServizioRepository
                        .findByServizioIdOrderByOrdineVisualizzazioneAsc(
                                pratica.getServizioId()
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
                                        .obbligatorio(
                                                documento.isObbligatorio()
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
}