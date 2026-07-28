package com.martina.caf_fapi.utenti.service;

import com.martina.caf_fapi.exception.InvalidDataException;
import com.martina.caf_fapi.exception.ResourceAlreadyExistsException;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.utenti.dto.UtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.mapper.UtenteMapper;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.util.FormattazioneUtils;
import com.martina.caf_fapi.validation.CodiceFiscaleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final CodiceFiscaleValidator codiceFiscaleValidator;

    @Override
    @Transactional
    public UtenteResponse creaUtente(UtenteRequest request) {

        normalizzaRequest(request);

        if (!codiceFiscaleValidator.isValido(request.getCodiceFiscale())) {
            throw new InvalidDataException("Il codice fiscale non è valido.");
        }

        verificaDuplicati(request);

        Utente utente = utenteMapper.toEntity(request);

        utente.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        utente.setRuolo(Ruolo.CLIENTE);
        utente.setAttivo(true);
        utente.setEmailVerificata(false);
        utente.setAccountBloccato(false);
        utente.setTentativiAccessoFalliti(0);
        utente.setPasswordModificataIl(LocalDateTime.now());

        Utente utenteSalvato = utenteRepository.save(utente);

        return utenteMapper.toResponse(utenteSalvato);
    }

    @Override
    @Transactional(readOnly = true)
    public UtenteResponse trovaPerId(Long id) {

        Utente utente = trovaEntitaPerId(id);

        return utenteMapper.toResponse(utente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtenteResponse> trovaTutti(Pageable pageable) {

        return utenteRepository.findAll(pageable)
                .map(utenteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtenteResponse> trovaPerRuolo(
            Ruolo ruolo,
            Pageable pageable
    ) {

        return utenteRepository.findByRuolo(ruolo, pageable)
                .map(utenteMapper::toResponse);
    }

    @Override
    public UtenteResponse aggiornaUtente(
            Long id,
            UtenteUpdateRequest request
    ) {
        return null;
    }

    @Override
    public UtenteResponse cambiaRuolo(
            Long id,
            Ruolo nuovoRuolo
    ) {
        return null;
    }

    @Override
    public UtenteResponse attivaUtente(Long id) {
        return null;
    }

    @Override
    public UtenteResponse disattivaUtente(Long id) {
        return null;
    }

    private void normalizzaRequest(UtenteRequest request) {

        request.setNome(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getNome()
                )
        );

        request.setCognome(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getCognome()
                )
        );

        request.setCodiceFiscale(
                FormattazioneUtils.normalizzaCodiceFiscale(
                        request.getCodiceFiscale()
                )
        );

        request.setLuogoNascita(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getLuogoNascita()
                )
        );

        request.setEmail(
                FormattazioneUtils.normalizzaEmail(
                        request.getEmail()
                )
        );

        request.setTelefono(
                FormattazioneUtils.normalizzaTelefono(
                        request.getTelefono()
                )
        );

        request.setIndirizzo(
                FormattazioneUtils.normalizzaTesto(
                        request.getIndirizzo()
                )
        );

        request.setComune(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getComune()
                )
        );

        request.setProvincia(
                FormattazioneUtils.normalizzaProvincia(
                        request.getProvincia()
                )
        );

        request.setCap(
                FormattazioneUtils.normalizzaCap(
                        request.getCap()
                )
        );

        request.setMansione(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getMansione()
                )
        );

        request.setNumeroMatricola(
                FormattazioneUtils.normalizzaTesto(
                        request.getNumeroMatricola()
                )
        );

        request.setUrlImmagineProfilo(
                FormattazioneUtils.normalizzaUrl(
                        request.getUrlImmagineProfilo()
                )
        );
    }

    private void verificaDuplicati(UtenteRequest request) {

        if (utenteRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questa email"
            );
        }

        if (utenteRepository.existsByCodiceFiscale(
                request.getCodiceFiscale()
        )) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questo codice fiscale"
            );
        }

        if (
                request.getNumeroMatricola() != null
                        && !request.getNumeroMatricola().isBlank()
                        && utenteRepository.existsByNumeroMatricola(
                        request.getNumeroMatricola()
                )
        ) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questo numero di matricola"
            );
        }
    }

    private Utente trovaEntitaPerId(Long id) {

        return utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato con id: " + id
                ));
    }
}