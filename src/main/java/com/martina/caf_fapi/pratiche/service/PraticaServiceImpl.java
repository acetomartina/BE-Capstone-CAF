package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.documenti.service.DocumentoPraticaService;
import com.martina.caf_fapi.pratiche.dto.AggiornaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.CambiaStatoPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.CreaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.pratiche.mapper.PraticaMapper;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.servizi.entity.Servizio;
import com.martina.caf_fapi.servizi.repository.ServizioRepository;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PraticaServiceImpl
        implements PraticaService {

    private final PraticaRepository praticaRepository;
    private final UtenteRepository utenteRepository;
    private final ServizioRepository servizioRepository;
    private final PraticaMapper praticaMapper;
    private final DocumentoPraticaService documentoPraticaService;

    @Override
    public Page<PraticaResponse> trovaTutte(
            Pageable pageable
    ) {
        return praticaRepository
                .findByEliminatoFalse(pageable)
                .map(praticaMapper::toResponse);
    }

    @Override
    public Page<PraticaResponse> cerca(
            String query,
            StatoPratica stato,
            Long servizioId,
            Long responsabileId,
            Pageable pageable
    ) {
        String queryNormalizzata =
                query == null
                        ? ""
                        : query.trim();

        return praticaRepository
                .cerca(
                        queryNormalizzata,
                        stato,
                        servizioId,
                        responsabileId,
                        pageable
                )
                .map(praticaMapper::toResponse);
    }

    @Override
    public Page<PraticaResponse> trovaPerCliente(
            Long clienteId,
            Pageable pageable
    ) {
        utenteRepository
                .findByIdAndRuoloAndEliminatoFalse(
                        clienteId,
                        Ruolo.CLIENTE
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cliente non trovato"
                        )
                );

        return praticaRepository
                .findByClienteIdAndEliminatoFalse(
                        clienteId,
                        pageable
                )
                .map(praticaMapper::toResponse);
    }

    @Override
    public PraticaResponse trovaPerIdDelCliente(
            Long praticaId,
            Long clienteId
    ) {
        /*
         * Cerchiamo contemporaneamente per ID della pratica
         * e ID del cliente. In questo modo un cliente non può
         * visualizzare una pratica appartenente a un altro account.
         */
        Pratica pratica =
                praticaRepository
                        .findByIdAndClienteIdAndEliminatoFalse(
                                praticaId,
                                clienteId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Pratica non trovata"
                                )
                        );

        return praticaMapper.toResponse(
                pratica
        );
    }

    @Override
    public PraticaResponse trovaPerId(
            Long id
    ) {
        Pratica pratica =
                trovaPratica(id);

        return praticaMapper.toResponse(
                pratica
        );
    }

    /*
     * Il responsabile della pratica viene assegnato
     * automaticamente all'utente autenticato che
     * sta creando la pratica.
     *
     * Il frontend non può scegliere o alterare
     * il responsabile durante la creazione.
     */
    @Override
    @Transactional
    public PraticaResponse creaPratica(
            CreaPraticaRequest request
    ) {
        Utente cliente =
                trovaCliente(
                        request.clienteId()
                );

        Servizio servizio =
                trovaServizio(
                        request.servizioId()
                );

        Utente responsabile =
                trovaResponsabileAutenticato();

        Pratica pratica =
                Pratica.builder()
                        .numeroPratica(
                                generaNumeroPratica()
                        )
                        .cliente(cliente)
                        .servizio(servizio)
                        .responsabile(
                                responsabile
                        )
                        .oggetto(
                                request
                                        .oggetto()
                                        .trim()
                        )
                        .descrizione(
                                normalizza(
                                        request.descrizione()
                                )
                        )
                        .stato(
                                StatoPratica.DA_AVVIARE
                        )
                        .priorita(
                                request.priorita()
                                        != null
                                        ? request.priorita()
                                        : PrioritaPratica.NORMALE
                        )
                        .dataScadenza(
                                request.dataScadenza()
                        )
                        .note(
                                normalizza(
                                        request.note()
                                )
                        )
                        .build();

        Pratica salvata =
                praticaRepository.save(
                        pratica
                );

        /*
         * La checklist viene generata
         * automaticamente in base
         * al servizio selezionato.
         */
        documentoPraticaService
                .generaChecklistDaServizio(
                        salvata
                );

        return praticaMapper.toResponse(
                salvata
        );
    }

    @Override
    @Transactional
    public PraticaResponse aggiornaPratica(
            Long id,
            AggiornaPraticaRequest request
    ) {
        Pratica pratica =
                trovaPratica(id);

        Utente responsabile = null;

        /*
         * Nell'aggiornamento manteniamo
         * la possibilità di riassegnare
         * la pratica.
         */
        if (
                request.responsabileId()
                        != null
        ) {
            responsabile =
                    trovaResponsabile(
                            request.responsabileId()
                    );
        }

        pratica.setResponsabile(
                responsabile
        );

        pratica.setOggetto(
                request
                        .oggetto()
                        .trim()
        );

        pratica.setDescrizione(
                normalizza(
                        request.descrizione()
                )
        );

        pratica.setPriorita(
                request.priorita()
                        != null
                        ? request.priorita()
                        : PrioritaPratica.NORMALE
        );

        pratica.setDataScadenza(
                request.dataScadenza()
        );

        pratica.setNote(
                normalizza(
                        request.note()
                )
        );

        Pratica salvata =
                praticaRepository.save(
                        pratica
                );

        return praticaMapper.toResponse(
                salvata
        );
    }

    @Override
    @Transactional
    public PraticaResponse cambiaStato(
            Long id,
            CambiaStatoPraticaRequest request
    ) {
        Pratica pratica =
                trovaPratica(id);

        StatoPratica nuovoStato =
                request.stato();

        pratica.setStato(
                nuovoStato
        );

        if (
                nuovoStato
                        == StatoPratica.COMPLETATA
                        ||
                        nuovoStato
                                == StatoPratica.ANNULLATA
        ) {
            if (
                    pratica.getChiusoIl()
                            == null
            ) {
                pratica.setChiusoIl(
                        LocalDateTime.now()
                );
            }
        } else {
            pratica.setChiusoIl(
                    null
            );
        }

        Pratica salvata =
                praticaRepository.save(
                        pratica
                );

        return praticaMapper.toResponse(
                salvata
        );
    }

    private Pratica trovaPratica(
            Long id
    ) {
        return praticaRepository
                .findByIdAndEliminatoFalse(
                        id
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Pratica non trovata"
                        )
                );
    }

    private Utente trovaCliente(
            Long clienteId
    ) {
        return utenteRepository
                .findByIdAndRuoloAndEliminatoFalse(
                        clienteId,
                        Ruolo.CLIENTE
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cliente non trovato"
                        )
                );
    }

    private Servizio trovaServizio(
            Long servizioId
    ) {
        return servizioRepository
                .findByIdAndAttivoTrue(
                        servizioId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Servizio non trovato o non attivo"
                        )
                );
    }

    /*
     * Recupera direttamente il principal
     * autenticato dal SecurityContext.
     */
    private Utente trovaResponsabileAutenticato() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                        ||
                        !authentication.isAuthenticated()
                        ||
                        !(
                                authentication.getPrincipal()
                                        instanceof UtenteDetails utenteDetails
                        )
        ) {
            throw new IllegalStateException(
                    "Utente autenticato non disponibile"
            );
        }

        return trovaResponsabile(
                utenteDetails.getId()
        );
    }

    /*
     * Utilizzato anche durante la riassegnazione
     * manuale di una pratica.
     */
    private Utente trovaResponsabile(
            Long responsabileId
    ) {
        Utente utente =
                utenteRepository
                        .findById(
                                responsabileId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Responsabile non trovato"
                                )
                        );

        if (
                Boolean.TRUE.equals(
                        utente.getEliminato()
                )
        ) {
            throw new IllegalArgumentException(
                    "Il responsabile selezionato è stato eliminato"
            );
        }

        if (!utente.isAttivo()) {
            throw new IllegalArgumentException(
                    "Il responsabile selezionato non è attivo"
            );
        }

        if (
                !ruoloOperativo(
                        utente.getRuolo()
                )
        ) {
            throw new IllegalArgumentException(
                    "L'utente selezionato non può essere responsabile di una pratica"
            );
        }

        return utente;
    }

    private boolean ruoloOperativo(
            Ruolo ruolo
    ) {
        return ruolo == Ruolo.SUPER_ADMIN
                || ruolo == Ruolo.ADMIN
                || ruolo == Ruolo.USER;
    }

    private String generaNumeroPratica() {
        int anno =
                Year.now().getValue();

        long progressivo =
                praticaRepository.count()
                        + 1;

        String numero;

        do {
            numero =
                    "CAF-%d-%06d"
                            .formatted(
                                    anno,
                                    progressivo
                            );

            progressivo++;
        } while (
                praticaRepository
                        .existsByNumeroPratica(
                                numero
                        )
        );

        return numero;
    }

    private String normalizza(
            String valore
    ) {
        if (valore == null) {
            return null;
        }

        String pulito =
                valore.trim();

        return pulito.isEmpty()
                ? null
                : pulito;
    }
}