package com.martina.caf_fapi.clienti.service;

import com.martina.caf_fapi.clienti.dto.AggiornaClienteRequest;
import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import com.martina.caf_fapi.clienti.mapper.ClienteMapper;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.clienti.dto.CreaClienteRequest;
import com.martina.caf_fapi.utenti.dto.CreaUtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.service.UtenteService;
import com.martina.caf_fapi.auth.security.UtenteDetails;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final UtenteRepository utenteRepository;
    private final ClienteMapper clienteMapper;
    private final UtenteService utenteService;

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse trovaPerId(Long id) {
        Utente cliente = trovaClientePerId(id);

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> trovaTutti(Pageable pageable) {
        return utenteRepository
                .findByRuoloAndEliminatoFalse(
                        Ruolo.CLIENTE,
                        pageable
                )
                .map(clienteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> cercaPerCognome(
            String cognome,
            Pageable pageable
    ) {
        return utenteRepository
                .findByRuoloAndEliminatoFalseAndCognomeContainingIgnoreCase(
                        Ruolo.CLIENTE,
                        cognome,
                        pageable
                )
                .map(clienteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> cercaPerCodiceFiscale(
            String codiceFiscale,
            Pageable pageable
    ) {
        return utenteRepository
                .findByRuoloAndEliminatoFalseAndCodiceFiscaleContainingIgnoreCase(
                        Ruolo.CLIENTE,
                        codiceFiscale,
                        pageable
                )
                .map(clienteMapper::toResponse);
    }

    private Utente trovaClientePerId(Long id) {
        return utenteRepository
                .findByIdAndRuoloAndEliminatoFalse(
                        id,
                        Ruolo.CLIENTE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente non trovato con id: " + id
                        )
                );
    }

    @Override
    @Transactional
    public ClienteResponse creaCliente(CreaClienteRequest request) {

        CreaUtenteRequest creaUtenteRequest =
                new CreaUtenteRequest(
                        request.nome(),
                        request.cognome(),
                        request.codiceFiscale(),
                        request.dataNascita(),
                        request.luogoNascita(),
                        request.email(),
                        request.telefono(),
                        request.indirizzo(),
                        request.comune(),
                        request.provincia(),
                        request.cap(),
                        request.password(),
                        Ruolo.CLIENTE,
                        null,
                        null
                );

        UtenteResponse utenteCreato =
                utenteService.creaUtente(creaUtenteRequest);

        return trovaPerId(utenteCreato.getId());
    }

    @Override
    @Transactional
    public ClienteResponse aggiornaCliente(
            Long id,
            AggiornaClienteRequest request
    ) {

        trovaClientePerId(id);

        UtenteUpdateRequest utenteUpdateRequest =
                UtenteUpdateRequest.builder()
                        .nome(request.nome())
                        .cognome(request.cognome())
                        .dataNascita(request.dataNascita())
                        .luogoNascita(request.luogoNascita())
                        .email(request.email())
                        .telefono(request.telefono())
                        .indirizzo(request.indirizzo())
                        .comune(request.comune())
                        .provincia(request.provincia())
                        .cap(request.cap())
                        .mansione(null)
                        .urlImmagineProfilo(null)
                        .build();

        utenteService.aggiornaUtente(
                id,
                utenteUpdateRequest
        );

        return trovaPerId(id);
    }

    @Override
    @Transactional
    public void eliminaCliente(Long id) {

        Utente cliente = trovaClientePerId(id);

        cliente.setEliminato(true);
        cliente.setEliminatoIl(LocalDateTime.now());
        cliente.setEliminatoDa(recuperaIdUtenteAutenticato());
        cliente.setAttivo(false);

        utenteRepository.save(cliente);
    }

    private Utente trovaClienteEliminatoPerId(Long id) {
        return utenteRepository.findById(id)
                .filter(utente -> utente.getRuolo() == Ruolo.CLIENTE)
                .filter(utente -> Boolean.TRUE.equals(utente.getEliminato()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente eliminato non trovato con id: " + id
                        )
                );
    }

    @Override
    @Transactional
    public ClienteResponse ripristinaCliente(Long id) {

        Utente cliente = trovaClienteEliminatoPerId(id);

        cliente.setEliminato(false);
        cliente.setEliminatoIl(null);
        cliente.setEliminatoDa(null);
        cliente.setAttivo(true);

        Utente clienteRipristinato =
                utenteRepository.save(cliente);

        return clienteMapper.toResponse(clienteRipristinato);
    }

    private Long recuperaIdUtenteAutenticato() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.getPrincipal() instanceof UtenteDetails utenteDetails) {
            return utenteDetails.getId();
        }

        return null;
    }
}