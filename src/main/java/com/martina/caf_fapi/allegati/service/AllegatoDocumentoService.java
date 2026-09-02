package com.martina.caf_fapi.allegati.service;

import com.martina.caf_fapi.allegati.dto.AllegatoDocumentoResponse;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Ogni operazione riceve l'utente autenticato perche' l'accesso a un
 * allegato dipende da chi lo chiede: un operatore lavora su tutte le
 * pratiche, un cliente soltanto sulle proprie.
 */
public interface AllegatoDocumentoService {

    AllegatoDocumentoResponse carica(
            Long documentoPraticaId,
            MultipartFile file,
            UtenteDetails utenteAutenticato
    );

    List<AllegatoDocumentoResponse> trovaPerDocumento(
            Long documentoPraticaId,
            UtenteDetails utenteAutenticato
    );

    DownloadAllegato scarica(
            Long allegatoId,
            UtenteDetails utenteAutenticato
    );

    void elimina(
            Long allegatoId,
            UtenteDetails utenteAutenticato
    );

    record DownloadAllegato(
            Resource resource,
            String nomeOriginale,
            String mimeType
    ) {
    }

    List<AllegatoDocumentoResponse> trovaPerPratica(
            Long praticaId,
            UtenteDetails utenteAutenticato
    );
}
