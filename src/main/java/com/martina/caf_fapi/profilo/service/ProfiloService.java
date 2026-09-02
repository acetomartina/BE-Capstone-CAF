package com.martina.caf_fapi.profilo.service;

import com.martina.caf_fapi.exception.InvalidDataException;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.profilo.dto.AggiornaProfiloRequest;
import com.martina.caf_fapi.profilo.dto.CambiaPasswordProfiloRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.mapper.UtenteMapper;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.util.FormattazioneUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfiloService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UtenteResponse trovaProfilo(
            String emailAutenticata
    ) {
        return utenteMapper.toResponse(
                trovaUtenteAutenticato(
                        emailAutenticata
                )
        );
    }

    @Transactional
    public UtenteResponse aggiornaProfilo(
            String emailAutenticata,
            AggiornaProfiloRequest request
    ) {
        Utente utente =
                trovaUtenteAutenticato(
                        emailAutenticata
                );

        utente.setNome(
                FormattazioneUtils
                        .normalizzaTitleCase(
                                request.nome()
                        )
        );

        utente.setCognome(
                FormattazioneUtils
                        .normalizzaTitleCase(
                                request.cognome()
                        )
        );

        utente.setDataNascita(
                request.dataNascita()
        );

        utente.setLuogoNascita(
                FormattazioneUtils
                        .normalizzaTitleCase(
                                request.luogoNascita()
                        )
        );

        utente.setTelefono(
                FormattazioneUtils
                        .normalizzaTelefono(
                                request.telefono()
                        )
        );

        utente.setIndirizzo(
                FormattazioneUtils
                        .normalizzaTesto(
                                request.indirizzo()
                        )
        );

        utente.setComune(
                FormattazioneUtils
                        .normalizzaTitleCase(
                                request.comune()
                        )
        );

        utente.setProvincia(
                FormattazioneUtils
                        .normalizzaProvincia(
                                request.provincia()
                        )
        );

        utente.setCap(
                FormattazioneUtils
                        .normalizzaCap(
                                request.cap()
                        )
        );

        utente.setMansione(
                FormattazioneUtils
                        .normalizzaTitleCase(
                                request.mansione()
                        )
        );

        utente.setUrlImmagineProfilo(
                FormattazioneUtils
                        .normalizzaUrl(
                                request.urlImmagineProfilo()
                        )
        );

        Utente utenteAggiornato =
                utenteRepository.save(utente);

        return utenteMapper.toResponse(
                utenteAggiornato
        );
    }

    @Transactional
    public void cambiaPassword(
            String emailAutenticata,
            CambiaPasswordProfiloRequest request
    ) {
        Utente utente =
                trovaUtenteAutenticato(
                        emailAutenticata
                );

        if (
                !passwordEncoder.matches(
                        request.passwordAttuale(),
                        utente.getPassword()
                )
        ) {
            throw new InvalidDataException(
                    "La password attuale non è corretta."
            );
        }

        if (
                passwordEncoder.matches(
                        request.nuovaPassword(),
                        utente.getPassword()
                )
        ) {
            throw new InvalidDataException(
                    "La nuova password deve essere diversa da quella attuale."
            );
        }

        utente.setPassword(
                passwordEncoder.encode(
                        request.nuovaPassword()
                )
        );

        utente.setPasswordModificataIl(
                LocalDateTime.now()
        );

        utenteRepository.save(utente);
    }

    private Utente trovaUtenteAutenticato(
            String emailAutenticata
    ) {
        return utenteRepository
                .findByEmailIgnoreCase(
                        emailAutenticata
                )
                .filter(
                        utente ->
                                !Boolean.TRUE.equals(
                                        utente.getEliminato()
                                )
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Profilo utente non trovato."
                                )
                );
    }
}