package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.pratiche.entity.Pratica;

import java.util.List;

public interface DocumentoPraticaService {

    void generaChecklistDaServizio(
            Pratica pratica
    );

    List<DocumentoPraticaResponse> trovaPerPratica(
            Long praticaId
    );

    DocumentoPraticaResponse trovaPerId(
            Long id
    );

    DocumentoPraticaResponse cambiaStato(
            Long id,
            CambiaStatoDocumentoRequest request
    );
}