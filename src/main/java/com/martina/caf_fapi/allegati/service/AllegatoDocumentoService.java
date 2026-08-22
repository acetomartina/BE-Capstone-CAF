package com.martina.caf_fapi.allegati.service;

import com.martina.caf_fapi.allegati.dto.AllegatoDocumentoResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AllegatoDocumentoService {

    AllegatoDocumentoResponse carica(
            Long documentoPraticaId,
            MultipartFile file,
            Long utenteId
    );

    List<AllegatoDocumentoResponse> trovaPerDocumento(
            Long documentoPraticaId
    );

    DownloadAllegato scarica(
            Long allegatoId
    );

    void elimina(
            Long allegatoId
    );

    record DownloadAllegato(
            Resource resource,
            String nomeOriginale,
            String mimeType
    ) {
    }

    List<AllegatoDocumentoResponse> trovaPerPratica(
            Long praticaId
    );
}