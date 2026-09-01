package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CambiaStatoDocumentoRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoAdminResponse;
import com.martina.caf_fapi.documenti.dto.DocumentoPraticaResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiAdminResponse;
import com.martina.caf_fapi.documenti.dto.RiepilogoDocumentiResponse;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import com.martina.caf_fapi.pratiche.entity.Pratica;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentoPraticaService {

    void generaChecklistDaServizio(
            Pratica pratica
    );

    Page<DocumentoAdminResponse> trovaTutti(
            String termine,
            StatoDocumentoPratica stato,
            TipoObbligatorietaDocumento tipoObbligatorieta,
            Pageable pageable
    );

    RiepilogoDocumentiAdminResponse riepilogoAdmin();

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

    RiepilogoDocumentiResponse riepilogo(
            Long praticaId
    );
}