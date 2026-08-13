package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.pratiche.dto.*;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.entity.Sottopratica;
import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.pratiche.mapper.SottopraticaMapper;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.pratiche.repository.SottopraticaRepository;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SottopraticaServiceImpl
        implements SottopraticaService {

    private final SottopraticaRepository sottopraticaRepository;
    private final PraticaRepository praticaRepository;
    private final UtenteRepository utenteRepository;
    private final SottopraticaMapper sottopraticaMapper;

    @Override
    public Page<SottopraticaResponse> trovaPerPratica(
            Long praticaId,
            Pageable pageable
    ) {
        verificaPratica(praticaId);

        return sottopraticaRepository
                .findByPraticaIdAndEliminatoFalse(
                        praticaId,
                        pageable
                )
                .map(sottopraticaMapper::toResponse);
    }

    @Override
    public SottopraticaResponse trovaPerId(Long id) {
        return sottopraticaMapper.toResponse(
                trovaSottopratica(id)
        );
    }

    @Override
    @Transactional
    public SottopraticaResponse crea(
            Long praticaId,
            CreaSottopraticaRequest request
    ) {
        Pratica pratica = verificaPratica(praticaId);

        Utente operatore = null;

        if (request.operatoreId() != null) {
            operatore = trovaOperatore(
                    request.operatoreId()
            );
        }

        Sottopratica sottopratica =
                Sottopratica.builder()
                        .pratica(pratica)
                        .titolo(request.titolo().trim())
                        .descrizione(
                                normalizza(
                                        request.descrizione()
                                )
                        )
                        .operatoreAssegnato(operatore)
                        .stato(StatoPratica.DA_AVVIARE)
                        .priorita(
                                request.priorita() != null
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

        Sottopratica salvata =
                sottopraticaRepository.save(
                        sottopratica
                );

        return sottopraticaMapper.toResponse(salvata);
    }

    @Override
    @Transactional
    public SottopraticaResponse aggiorna(
            Long id,
            AggiornaSottopraticaRequest request
    ) {
        Sottopratica sottopratica =
                trovaSottopratica(id);

        Utente operatore = null;

        if (request.operatoreId() != null) {
            operatore = trovaOperatore(
                    request.operatoreId()
            );
        }

        sottopratica.setTitolo(
                request.titolo().trim()
        );
        sottopratica.setDescrizione(
                normalizza(request.descrizione())
        );
        sottopratica.setOperatoreAssegnato(
                operatore
        );
        sottopratica.setPriorita(
                request.priorita() != null
                        ? request.priorita()
                        : PrioritaPratica.NORMALE
        );
        sottopratica.setDataScadenza(
                request.dataScadenza()
        );
        sottopratica.setNote(
                normalizza(request.note())
        );

        Sottopratica salvata =
                sottopraticaRepository.save(
                        sottopratica
                );

        return sottopraticaMapper.toResponse(salvata);
    }

    @Override
    @Transactional
    public SottopraticaResponse cambiaStato(
            Long id,
            CambiaStatoSottopraticaRequest request
    ) {
        Sottopratica sottopratica =
                trovaSottopratica(id);

        StatoPratica nuovoStato =
                request.stato();

        sottopratica.setStato(nuovoStato);

        if (
                nuovoStato == StatoPratica.COMPLETATA
                        || nuovoStato == StatoPratica.ANNULLATA
        ) {
            if (sottopratica.getDataChiusura() == null) {
                sottopratica.setDataChiusura(
                        LocalDate.now()
                );
            }
        } else {
            sottopratica.setDataChiusura(null);
        }

        Sottopratica salvata =
                sottopraticaRepository.save(
                        sottopratica
                );

        return sottopraticaMapper.toResponse(salvata);
    }

    private Pratica verificaPratica(Long praticaId) {
        return praticaRepository
                .findByIdAndEliminatoFalse(praticaId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Pratica non trovata"
                        )
                );
    }

    private Sottopratica trovaSottopratica(Long id) {
        return sottopraticaRepository
                .findByIdAndEliminatoFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Sottopratica non trovata"
                        )
                );
    }

    private Utente trovaOperatore(Long operatoreId) {
        Utente utente = utenteRepository
                .findById(operatoreId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Operatore non trovato"
                        )
                );

        if (Boolean.TRUE.equals(utente.getEliminato())) {
            throw new IllegalArgumentException(
                    "L'operatore selezionato è stato eliminato"
            );
        }

        if (!utente.isAttivo()) {
            throw new IllegalArgumentException(
                    "L'operatore selezionato non è attivo"
            );
        }

        if (!ruoloOperativo(utente.getRuolo())) {
            throw new IllegalArgumentException(
                    "L'utente selezionato non può gestire una sottopratica"
            );
        }

        return utente;
    }

    private boolean ruoloOperativo(Ruolo ruolo) {
        return ruolo == Ruolo.SUPER_ADMIN
                || ruolo == Ruolo.ADMIN
                || ruolo == Ruolo.USER;
    }

    private String normalizza(String valore) {
        if (valore == null) {
            return null;
        }

        String pulito = valore.trim();

        return pulito.isEmpty() ? null : pulito;
    }
}