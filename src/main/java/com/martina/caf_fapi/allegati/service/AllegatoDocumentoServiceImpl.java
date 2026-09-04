package com.martina.caf_fapi.allegati.service;

import com.martina.caf_fapi.allegati.dto.AllegatoDocumentoResponse;
import com.martina.caf_fapi.allegati.entity.AllegatoDocumento;
import com.martina.caf_fapi.allegati.repository.AllegatoDocumentoRepository;
import com.martina.caf_fapi.allegati.storage.FileSalvato;
import com.martina.caf_fapi.allegati.storage.FileStorageService;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoPraticaRepository;
import com.martina.caf_fapi.exception.OperationNotAllowedException;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.pratiche.service.StatoAutomaticoPraticaService;
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

    /*
     * Un solo messaggio per "non esiste" e "non e' tuo": distinguerli
     * permetterebbe a un cliente di scoprire quali id esistono
     * enumerando le risposte.
     */
    private static final String ALLEGATO_NON_ACCESSIBILE =
            "Allegato non trovato.";

    private static final String DOCUMENTO_NON_ACCESSIBILE =
            "Documento richiesto non trovato.";

    private static final String PRATICA_NON_ACCESSIBILE =
            "Pratica non trovata.";

    private final AllegatoDocumentoRepository allegatoRepository;

    private final DocumentoRichiestoPraticaRepository
            documentoPraticaRepository;

    private final PraticaRepository praticaRepository;

    private final UtenteRepository utenteRepository;

    private final FileStorageService fileStorageService;

    private final StatoAutomaticoPraticaService
            statoAutomaticoPraticaService;

    @Override
    @Transactional
    public AllegatoDocumentoResponse carica(
            Long documentoPraticaId,
            MultipartFile file,
            UtenteDetails utenteAutenticato
    ) {
        DocumentoRichiestoPratica documento =
                trovaDocumentoAccessibile(
                        documentoPraticaId,
                        utenteAutenticato
                );

        Utente utente =
                utenteRepository
                        .findById(
                                utenteAutenticato.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
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

            statoAutomaticoPraticaService
                    .ricalcolaDaDocumenti(
                            documento
                                    .getPratica()
                                    .getId()
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
            Long documentoPraticaId,
            UtenteDetails utenteAutenticato
    ) {
        trovaDocumentoAccessibile(
                documentoPraticaId,
                utenteAutenticato
        );

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
            Long allegatoId,
            UtenteDetails utenteAutenticato
    ) {
        AllegatoDocumento allegato =
                trovaAllegatoAccessibile(
                        allegatoId,
                        utenteAutenticato
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
            Long allegatoId,
            UtenteDetails utenteAutenticato
    ) {
        AllegatoDocumento allegato =
                trovaAllegatoAccessibile(
                        allegatoId,
                        utenteAutenticato
                );

        DocumentoRichiestoPratica documento =
                allegato.getDocumentoPratica();

        verificaCancellazioneConsentita(
                allegato,
                documento,
                utenteAutenticato
        );

        boolean esistonoAltriAllegati =
                allegatoRepository
                        .existsByDocumentoPraticaIdAndIdNot(
                                documento.getId(),
                                allegatoId
                        );

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

        statoAutomaticoPraticaService
                .ricalcolaDaDocumenti(
                        documento
                                .getPratica()
                                .getId()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllegatoDocumentoResponse> trovaPerPratica(
            Long praticaId,
            UtenteDetails utenteAutenticato
    ) {
        Pratica pratica =
                praticaRepository
                        .findByIdAndEliminatoFalse(
                                praticaId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                PRATICA_NON_ACCESSIBILE
                                        )
                        );

        verificaAccessoAPratica(
                pratica,
                utenteAutenticato,
                PRATICA_NON_ACCESSIBILE
        );

        return allegatoRepository
                .findByDocumentoPraticaPraticaIdOrderByCaricatoIlDesc(
                        praticaId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DocumentoRichiestoPratica trovaDocumentoAccessibile(
            Long documentoPraticaId,
            UtenteDetails utenteAutenticato
    ) {
        DocumentoRichiestoPratica documento =
                documentoPraticaRepository
                        .findById(documentoPraticaId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                DOCUMENTO_NON_ACCESSIBILE
                                        )
                        );

        verificaAccessoAPratica(
                documento.getPratica(),
                utenteAutenticato,
                DOCUMENTO_NON_ACCESSIBILE
        );

        return documento;
    }

    private AllegatoDocumento trovaAllegatoAccessibile(
            Long allegatoId,
            UtenteDetails utenteAutenticato
    ) {
        AllegatoDocumento allegato =
                allegatoRepository
                        .findById(allegatoId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ALLEGATO_NON_ACCESSIBILE
                                        )
                        );

        verificaAccessoAPratica(
                allegato
                        .getDocumentoPratica()
                        .getPratica(),
                utenteAutenticato,
                ALLEGATO_NON_ACCESSIBILE
        );

        return allegato;
    }

    private void verificaAccessoAPratica(
            Pratica pratica,
            UtenteDetails utenteAutenticato,
            String messaggio
    ) {
        if (utenteAutenticato.isOperatore()) {
            return;
        }

        boolean titolareDellaPratica =
                pratica
                        .getCliente()
                        .getId()
                        .equals(
                                utenteAutenticato.getId()
                        );

        if (!titolareDellaPratica) {
            throw new ResourceNotFoundException(
                    messaggio
            );
        }
    }

    /**
     * Un cliente puo' ritirare soltanto cio' che ha caricato lui, e solo
     * finche' la sede non lo ha validato: dopo la validazione il documento
     * fa parte di una pratica in lavorazione e non e' piu' materiale suo.
     */
    private void verificaCancellazioneConsentita(
            AllegatoDocumento allegato,
            DocumentoRichiestoPratica documento,
            UtenteDetails utenteAutenticato
    ) {
        if (utenteAutenticato.isOperatore()) {
            return;
        }

        boolean caricatoDaLui =
                allegato
                        .getCaricatoDa()
                        .getId()
                        .equals(
                                utenteAutenticato.getId()
                        );

        if (!caricatoDaLui) {
            throw new OperationNotAllowedException(
                    "Non puoi eliminare un allegato caricato dalla sede."
            );
        }

        if (
                documento.getStato()
                        == StatoDocumentoPratica.VALIDATO
        ) {
            throw new OperationNotAllowedException(
                    "Il documento è già stato validato dalla sede "
                            + "e non può più essere modificato."
            );
        }
    }

    private void aggiornaStatoDocumentoDopoUpload(
            DocumentoRichiestoPratica documento,
            Utente utente
    ) {
        StatoDocumentoPratica nuovoStato =
                switch (utente.getRuolo()) {

                    case CLIENTE ->
                            StatoDocumentoPratica.DA_VERIFICARE;

                    case SUPER_ADMIN,
                         ADMIN,
                         USER ->
                            StatoDocumentoPratica.VALIDATO;
                };

        documento.setStato(
                nuovoStato
        );

        documentoPraticaRepository.save(
                documento
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
}
