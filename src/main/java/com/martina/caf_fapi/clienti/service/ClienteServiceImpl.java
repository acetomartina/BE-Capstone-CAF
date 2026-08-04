package com.martina.caf_fapi.clienti.service;

import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import com.martina.caf_fapi.clienti.mapper.ClienteMapper;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final UtenteRepository utenteRepository;
    private final ClienteMapper clienteMapper;

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
}