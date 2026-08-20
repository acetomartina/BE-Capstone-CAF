package com.martina.caf_fapi.clienti.service;

import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.clienti.dto.AggiornaClienteRequest;
import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import com.martina.caf_fapi.clienti.dto.CreaClienteRequest;
import com.martina.caf_fapi.clienti.mapper.ClienteMapper;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.utenti.dto.CreaUtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.utenti.service.UtenteService;
import com.martina.caf_fapi.auth.service.AccountActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    /*
     * La password generata alla creazione non viene comunicata
     * né all'operatore né al cliente.
     *
     * Serve soltanto perché la colonna password di utenti è NOT NULL.
     * Il cliente sceglierà la propria password tramite il futuro
     * flusso di attivazione account.
     */
    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private static final int BYTE_PASSWORD_TEMPORANEA = 32;


    private final UtenteRepository utenteRepository;
    private final ClienteMapper clienteMapper;
    private final UtenteService utenteService;
    private final AccountActivationService accountActivationService;


    @Override
    @Transactional(readOnly = true)
    public ClienteResponse trovaPerId(Long id) {

        Utente cliente = trovaClientePerId(id);

        return clienteMapper.toResponse(cliente);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> trovaTutti(
            Boolean attivo,
            Pageable pageable
    ) {
        Page<Utente> clienti;

        if (attivo == null) {
            clienti =
                    utenteRepository
                            .findByRuoloAndEliminatoFalse(
                                    Ruolo.CLIENTE,
                                    pageable
                            );
        } else {
            clienti =
                    utenteRepository
                            .findByRuoloAndEliminatoFalseAndAttivo(
                                    Ruolo.CLIENTE,
                                    attivo,
                                    pageable
                            );
        }

        return clienti.map(
                clienteMapper::toResponse
        );
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
    public ClienteResponse creaCliente(
            CreaClienteRequest request
    ) {

        String passwordTemporanea =
                generaPasswordTemporanea();

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
                        passwordTemporanea,
                        Ruolo.CLIENTE,
                        null,
                        null
                );

        UtenteResponse utenteCreato =
                utenteService.creaUtente(
                        creaUtenteRequest
                );

        /*
         * UtenteService crea normalmente gli account come attivi.
         *
         * Per un cliente creato da un operatore non vogliamo invece
         * consentire il login finché il cliente non ha completato
         * l'attivazione e scelto personalmente la propria password.
         */
        Utente cliente =
                utenteRepository
                        .findById(utenteCreato.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cliente appena creato non trovato"
                                )
                        );

        cliente.setAttivo(false);
        cliente.setEmailVerificata(false);

        utenteRepository.save(cliente);

        accountActivationService.inviaInvito(cliente);

        return clienteMapper.toResponse(cliente);
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
                        .dataNascita(
                                request.dataNascita()
                        )
                        .luogoNascita(
                                request.luogoNascita()
                        )
                        .email(request.email())
                        .telefono(request.telefono())
                        .indirizzo(
                                request.indirizzo()
                        )
                        .comune(request.comune())
                        .provincia(
                                request.provincia()
                        )
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

        Utente cliente =
                trovaClientePerId(id);

        cliente.setEliminato(true);
        cliente.setEliminatoIl(
                LocalDateTime.now()
        );
        cliente.setEliminatoDa(
                recuperaIdUtenteAutenticato()
        );
        cliente.setAttivo(false);

        utenteRepository.save(cliente);
    }


    private Utente trovaClienteEliminatoPerId(
            Long id
    ) {

        return utenteRepository
                .findById(id)
                .filter(
                        utente ->
                                utente.getRuolo()
                                        == Ruolo.CLIENTE
                )
                .filter(
                        utente ->
                                Boolean.TRUE.equals(
                                        utente.getEliminato()
                                )
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente eliminato non trovato con id: "
                                        + id
                        )
                );
    }


    @Override
    @Transactional
    public ClienteResponse ripristinaCliente(
            Long id
    ) {

        Utente cliente =
                trovaClienteEliminatoPerId(id);

        cliente.setEliminato(false);
        cliente.setEliminatoIl(null);
        cliente.setEliminatoDa(null);
        cliente.setAttivo(true);

        Utente clienteRipristinato =
                utenteRepository.save(cliente);

        return clienteMapper.toResponse(
                clienteRipristinato
        );
    }


    private Long recuperaIdUtenteAutenticato() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication != null
                        && authentication.getPrincipal()
                        instanceof UtenteDetails utenteDetails
        ) {

            return utenteDetails.getId();
        }

        return null;
    }


    /*
     * Genera una credenziale interna ad alta entropia.
     *
     * Non viene mai mostrata o inviata via email.
     * Aggiungiamo esplicitamente i quattro tipi di carattere richiesti
     * dalla validazione di CreaUtenteRequest.
     */
    private String generaPasswordTemporanea() {

        byte[] casuali =
                new byte[BYTE_PASSWORD_TEMPORANEA];

        SECURE_RANDOM.nextBytes(casuali);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(casuali)
                + "aA1!";
    }
}