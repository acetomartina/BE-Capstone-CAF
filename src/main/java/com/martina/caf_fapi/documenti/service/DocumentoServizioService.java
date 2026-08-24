package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.documenti.dto.CreateDocumentoServizioRequest;
import com.martina.caf_fapi.documenti.dto.DocumentoServizioResponse;
import com.martina.caf_fapi.documenti.dto.RiordinaDocumentiServizioRequest;
import com.martina.caf_fapi.documenti.dto.UpdateDocumentoServizioRequest;

import java.util.List;

public interface DocumentoServizioService {

    List<DocumentoServizioResponse> trovaDocumentiPerServizio(
            Long servizioId
    );

    List<DocumentoServizioResponse> riordinaDocumenti(
            Long servizioId,
            RiordinaDocumentiServizioRequest request
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

    List<DocumentoServizioResponse> trovaDocumentiPubbliciPerServizio(
            Long servizioId
    );
}