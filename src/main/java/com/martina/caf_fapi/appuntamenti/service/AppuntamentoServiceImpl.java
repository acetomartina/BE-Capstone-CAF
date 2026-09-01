package com.martina.caf_fapi.appuntamenti.service;

import com.martina.caf_fapi.appuntamenti.dto.AggiornaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.dto.AppuntamentoResponse;
import com.martina.caf_fapi.appuntamenti.dto.CambiaStatoAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.dto.CreaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.entity.Appuntamento;
import com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento;
import com.martina.caf_fapi.appuntamenti.mapper.AppuntamentoMapper;
import com.martina.caf_fapi.appuntamenti.repository.AppuntamentoRepository;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppuntamentoServiceImpl
        implements AppuntamentoService {

    private final AppuntamentoRepository
            appuntamentoRepository;

    private final UtenteRepository
            utenteRepository;

    private final PraticaRepository
            praticaRepository;

    private final AppuntamentoMapper
            appuntamentoMapper;

    @Override
    public List<AppuntamentoResponse> trovaTutti(
            LocalDateTime dal,
            LocalDateTime al,
            Long clienteId,
            Long responsabileId,
            StatoAppuntamento stato
    ) {
        LocalDateTime dataDa =
                dal != null
                        ? dal
                        : LocalDate.now()
                        .minusMonths(1)
                        .atStartOfDay();

        LocalDateTime dataA =
                al != null
                        ? al
                        : LocalDate.now()
                        .plusYears(1)
                        .plusDays(1)
                        .atStartOfDay();

        validaIntervallo(
                dataDa,
                dataA
        );

        return appuntamentoRepository
                .trovaNelPeriodo(
                        dataDa,
                        dataA
                )
                .stream()
                .filter(appuntamento ->
                        clienteId == null
                                ||
                                appuntamento
                                        .getCliente()
                                        .getId()
                                        .equals(clienteId)
                )
                .filter(appuntamento ->
                        responsabileId == null
                                ||
                                (
                                        appuntamento
                                                .getResponsabile()
                                                != null
                                                &&
                                                appuntamento
                                                        .getResponsabile()
                                                        .getId()
                                                        .equals(
                                                                responsabileId
                                                        )
                                )
                )
                .filter(appuntamento ->
                        stato == null
                                ||
                                appuntamento.getStato()
                                        == stato
                )
                .map(
                        appuntamentoMapper::toResponse
                )
                .toList();
    }

    @Override
    public AppuntamentoResponse trovaPerId(
            Long id
    ) {
        return appuntamentoMapper.toResponse(
                trovaAppuntamento(id)
        );
    }

    @Override
    @Transactional
    public AppuntamentoResponse crea(
            CreaAppuntamentoRequest request
    ) {
        validaIntervallo(
                request.inizio(),
                request.fine()
        );

        Utente cliente =
                trovaUtente(
                        request.clienteId(),
                        "Cliente"
                );

        Pratica pratica =
                trovaPraticaFacoltativa(
                        request.praticaId()
                );

        verificaCoerenzaClientePratica(
                cliente,
                pratica
        );

        Utente responsabile =
                trovaUtenteFacoltativo(
                        request.responsabileId(),
                        "Responsabile"
                );

        Appuntamento appuntamento =
                Appuntamento.builder()
                        .cliente(cliente)
                        .pratica(pratica)
                        .servizio(
                                pratica != null
                                        ? pratica
                                        .getServizio()
                                        : null
                        )
                        .responsabile(
                                responsabile
                        )
                        .titolo(
                                normalizzaObbligatorio(
                                        request.titolo()
                                )
                        )
                        .descrizione(
                                normalizza(
                                        request
                                                .descrizione()
                                )
                        )
                        .tipologia(
                                request.tipologia()
                        )
                        .modalita(
                                request.modalita()
                        )
                        .stato(
                                StatoAppuntamento
                                        .PROGRAMMATO
                        )
                        .inizio(
                                request.inizio()
                        )
                        .fine(
                                request.fine()
                        )
                        .luogo(
                                normalizza(
                                        request.luogo()
                                )
                        )
                        .linkOnline(
                                normalizza(
                                        request
                                                .linkOnline()
                                )
                        )
                        .note(
                                normalizza(
                                        request.note()
                                )
                        )
                        .build();

        Appuntamento salvato =
                appuntamentoRepository.save(
                        appuntamento
                );

        return appuntamentoMapper
                .toResponse(salvato);
    }

    @Override
    @Transactional
    public AppuntamentoResponse aggiorna(
            Long id,
            AggiornaAppuntamentoRequest request
    ) {
        validaIntervallo(
                request.inizio(),
                request.fine()
        );

        Appuntamento appuntamento =
                trovaAppuntamento(id);

        Utente cliente =
                trovaUtente(
                        request.clienteId(),
                        "Cliente"
                );

        Pratica pratica =
                trovaPraticaFacoltativa(
                        request.praticaId()
                );

        verificaCoerenzaClientePratica(
                cliente,
                pratica
        );

        Utente responsabile =
                trovaUtenteFacoltativo(
                        request.responsabileId(),
                        "Responsabile"
                );

        appuntamento.setCliente(
                cliente
        );

        appuntamento.setPratica(
                pratica
        );

        appuntamento.setServizio(
                pratica != null
                        ? pratica.getServizio()
                        : null
        );

        appuntamento.setResponsabile(
                responsabile
        );

        appuntamento.setTitolo(
                normalizzaObbligatorio(
                        request.titolo()
                )
        );

        appuntamento.setDescrizione(
                normalizza(
                        request.descrizione()
                )
        );

        appuntamento.setTipologia(
                request.tipologia()
        );

        appuntamento.setModalita(
                request.modalita()
        );

        appuntamento.setInizio(
                request.inizio()
        );

        appuntamento.setFine(
                request.fine()
        );

        appuntamento.setLuogo(
                normalizza(
                        request.luogo()
                )
        );

        appuntamento.setLinkOnline(
                normalizza(
                        request.linkOnline()
                )
        );

        appuntamento.setNote(
                normalizza(
                        request.note()
                )
        );

        Appuntamento salvato =
                appuntamentoRepository.save(
                        appuntamento
                );

        return appuntamentoMapper
                .toResponse(salvato);
    }

    @Override
    @Transactional
    public AppuntamentoResponse cambiaStato(
            Long id,
            CambiaStatoAppuntamentoRequest request
    ) {
        Appuntamento appuntamento =
                trovaAppuntamento(id);

        if (
                request.stato()
                        == StatoAppuntamento.ANNULLATO
                        &&
                        (
                                request.motivoAnnullamento()
                                        == null
                                        ||
                                        request.motivoAnnullamento()
                                                .isBlank()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Indicare il motivo dell'annullamento"
            );
        }

        appuntamento.setStato(
                request.stato()
        );

        appuntamento.setMotivoAnnullamento(
                request.stato()
                        == StatoAppuntamento.ANNULLATO
                        ? normalizza(
                        request
                                .motivoAnnullamento()
                )
                        : null
        );

        Appuntamento salvato =
                appuntamentoRepository.save(
                        appuntamento
                );

        return appuntamentoMapper
                .toResponse(salvato);
    }

    @Override
    @Transactional
    public void elimina(
            Long id
    ) {
        Appuntamento appuntamento =
                trovaAppuntamento(id);

        appuntamento.setEliminato(
                true
        );

        appuntamento.setEliminatoIl(
                LocalDateTime.now()
        );

        appuntamentoRepository.save(
                appuntamento
        );
    }

    private Appuntamento trovaAppuntamento(
            Long id
    ) {
        return appuntamentoRepository
                .findByIdAndEliminatoFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Appuntamento non trovato"
                        )
                );
    }

    private Utente trovaUtente(
            Long id,
            String etichetta
    ) {
        return utenteRepository
                .findById(id)
                .filter(utente ->
                        !Boolean.TRUE.equals(
                                utente.getEliminato()
                        )
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                etichetta
                                        + " non trovato"
                        )
                );
    }

    private Utente trovaUtenteFacoltativo(
            Long id,
            String etichetta
    ) {
        if (id == null) {
            return null;
        }

        return trovaUtente(
                id,
                etichetta
        );
    }

    private Pratica trovaPraticaFacoltativa(
            Long praticaId
    ) {
        if (praticaId == null) {
            return null;
        }

        return praticaRepository
                .findByIdAndEliminatoFalse(
                        praticaId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Pratica non trovata"
                        )
                );
    }

    private void verificaCoerenzaClientePratica(
            Utente cliente,
            Pratica pratica
    ) {
        if (pratica == null) {
            return;
        }

        if (
                !pratica
                        .getCliente()
                        .getId()
                        .equals(
                                cliente.getId()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La pratica selezionata non appartiene al cliente"
            );
        }
    }

    private void validaIntervallo(
            LocalDateTime inizio,
            LocalDateTime fine
    ) {
        if (
                inizio == null ||
                        fine == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data di inizio e fine sono obbligatorie"
            );
        }

        if (!fine.isAfter(inizio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fine deve essere successiva all'inizio"
            );
        }
    }

    private String normalizza(
            String valore
    ) {
        if (
                valore == null ||
                        valore.isBlank()
        ) {
            return null;
        }

        return valore.trim();
    }

    private String normalizzaObbligatorio(
            String valore
    ) {
        String normalizzato =
                normalizza(valore);

        if (normalizzato == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il titolo è obbligatorio"
            );
        }

        return normalizzato;
    }
}