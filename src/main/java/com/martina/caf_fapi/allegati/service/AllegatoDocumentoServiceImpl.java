package com.martina.caf_fapi.allegati.service;

import com.martina.caf_fapi.allegati.dto.AllegatoDocumentoResponse;
import com.martina.caf_fapi.allegati.entity.AllegatoDocumento;
import com.martina.caf_fapi.allegati.repository.AllegatoDocumentoRepository;
import com.martina.caf_fapi.allegati.storage.FileSalvato;
import com.martina.caf_fapi.allegati.storage.FileStorageService;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoPraticaRepository;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllegatoDocumentoServiceImpl
        implements AllegatoDocumentoService {

    private final AllegatoDocumentoRepository allegatoRepository;

    private final DocumentoRichiestoPraticaRepository
            documentoPraticaRepository;

    private final UtenteRepository utenteRepository;

    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public AllegatoDocumentoResponse carica(
            Long documentoPraticaId,
            MultipartFile file,
            Long utenteId
    ) {
        DocumentoRichiestoPratica documento =
                documentoPraticaRepository
                        .findById(documentoPraticaId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Documento richiesto non trovato."
                                        )
                        );

        Utente utente =
                utenteRepository
                        .findById(utenteId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Utente non trovato."
                                        )
                        );

        FileSalvato fileSalvato =
                fileStorageService.salva(
                        file
                );

        try {
            AllegatoDocumento allegato =
                    AllegatoDocumento.builder()
                            .documentoPratica(documento)
                            .nomeOriginale(
                                    fileSalvato.nomeOriginale()
                            )
                            .nomeStorage(
                                    fileSalvato.nomeStorage()
                            )
                            .mimeType(
                                    fileSalvato.mimeType()
                            )
                            .dimensione(
                                    fileSalvato.dimensione()
                            )
                            .caricatoDa(utente)
                            .build();

            AllegatoDocumento salvato =
                    allegatoRepository.save(
                            allegato
                    );

            aggiornaStatoDocumentoDopoUpload(
                    documento,
                    utente
            );

            return toResponse(
                    salvato
            );

        } catch (RuntimeException e) {
            fileStorageService.elimina(
                    fileSalvato.nomeStorage()
            );

            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllegatoDocumentoResponse>
    trovaPerDocumento(
            Long documentoPraticaId
    ) {
        if (
                !documentoPraticaRepository
                        .existsById(
                                documentoPraticaId
                        )
        ) {
            throw new IllegalArgumentException(
                    "Documento richiesto non trovato."
            );
        }

        return allegatoRepository
                .findByDocumentoPraticaIdOrderByCaricatoIlDesc(
                        documentoPraticaId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadAllegato scarica(
            Long allegatoId
    ) {
        AllegatoDocumento allegato =
                trovaAllegato(
                        allegatoId
                );

        Resource resource =
                fileStorageService.carica(
                        allegato.getNomeStorage()
                );

        return new DownloadAllegato(
                resource,
                allegato.getNomeOriginale(),
                allegato.getMimeType()
        );
    }

    @Override
    @Transactional
    public void elimina(
            Long allegatoId
    ) {
        AllegatoDocumento allegato =
                trovaAllegato(
                        allegatoId
                );

        DocumentoRichiestoPratica documento =
                allegato.getDocumentoPratica();

        boolean esistonoAltriAllegati =
                allegatoRepository
                        .existsByDocumentoPraticaIdAndIdNot(
                                documento.getId(),
                                allegatoId
                        );

        /*
         * Se quello che stiamo eliminando
         * è l'ultimo allegato del documento,
         * la checklist torna a MANCANTE.
         */
        if (!esistonoAltriAllegati) {
            documento.setStato(
                    StatoDocumentoPratica.MANCANTE
            );
        }

        fileStorageService.elimina(
                allegato.getNomeStorage()
        );

        allegatoRepository.delete(
                allegato
        );


    }

    private void aggiornaStatoDocumentoDopoUpload(
            DocumentoRichiestoPratica documento,
            Utente utente
    ) {
        Ruolo ruolo =
                utente.getRuolo();

        StatoDocumentoPratica statoAttuale =
                documento.getStato();

        /*
         * Se il file viene caricato dal cliente,
         * il documento deve sempre tornare in stato
         * DA_VERIFICARE perché c'è nuovo materiale
         * che un operatore deve controllare.
         */
        if (ruolo == Ruolo.CLIENTE) {
            documento.setStato(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            return;
        }

        /*
         * Se il file viene caricato da un operatore,
         * lo portiamo a RICEVUTO solo se prima era
         * MANCANTE oppure RIFIUTATO.
         *
         * Non retrocediamo documenti già
         * DA_VERIFICARE o VALIDATI.
         */
        if (
                statoAttuale == StatoDocumentoPratica.MANCANTE ||
                        statoAttuale == StatoDocumentoPratica.RIFIUTATO
        ) {
            documento.setStato(
                    StatoDocumentoPratica.RICEVUTO
            );
        }
    }

    private AllegatoDocumento trovaAllegato(
            Long id
    ) {
        return allegatoRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Allegato non trovato."
                                )
                );
    }

    private AllegatoDocumentoResponse toResponse(
            AllegatoDocumento allegato
    ) {
        Utente autore =
                allegato.getCaricatoDa();

        return new AllegatoDocumentoResponse(
                allegato.getId(),
                allegato
                        .getDocumentoPratica()
                        .getId(),
                allegato.getNomeOriginale(),
                allegato.getMimeType(),
                allegato.getDimensione(),
                autore.getId(),
                autore.getNome(),
                autore.getCognome(),
                allegato.getCaricatoIl()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllegatoDocumentoResponse> trovaPerPratica(
            Long praticaId
    ) {
        return allegatoRepository
                .findByDocumentoPraticaPraticaIdOrderByCaricatoIlDesc(
                        praticaId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }
}