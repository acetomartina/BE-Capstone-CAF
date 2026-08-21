package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CreateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoServizioResponse;
import com.martina.caf_fapi.documenti.dto.UpdateDocumentoServizioRequest;

import java.util.List;

public interface DocumentoServizioService {

    List<DocumentoServizioResponse> trovaDocumentiPerServizio(
            Long servizioId
    );

    DocumentoServizioResponse creaDocumento(
            Long servizioId,
            CreateDocumentoServizioRequest request
    );

    DocumentoServizioResponse aggiornaDocumento(
            Long documentoId,
            UpdateDocumentoServizioRequest request
    );

    void disattivaDocumento(
            Long documentoId
    );
}