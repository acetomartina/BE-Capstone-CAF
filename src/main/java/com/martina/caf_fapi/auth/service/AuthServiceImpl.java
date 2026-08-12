package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.LoginRequest;
import com.martina.caf_fapi.auth.dto.LoginResponse;
import com.martina.caf_fapi.auth.security.JwtService;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.exception.InvalidCredentialsException;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.mapper.UtenteMapper;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtenteRepository utenteRepository;
    private final JwtService jwtService;
    private final UtenteMapper utenteMapper;

    @Override
    @Transactional(readOnly = true)
    public UtenteResponse utenteCorrente(String email) {

        Utente utente = utenteRepository
                .findByEmailIgnoreCase(email.strip().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Sessione non valida."
                ));

        return utenteMapper.toResponse(utente);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        String emailNormalizzata = request.getEmail()
                .strip()
                .toLowerCase(Locale.ROOT);

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            emailNormalizzata,
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException(
                    "Email o password non corrette."
            );
        }

        UtenteDetails userDetails =
                (UtenteDetails) authentication.getPrincipal();

        Utente utente = utenteRepository
                .findByEmailIgnoreCase(emailNormalizzata)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Email o password non corrette."
                        )
                );

        String accessToken =
                jwtService.generaToken(userDetails);

        utente.setUltimoAccesso(LocalDateTime.now());
        utenteRepository.save(utente);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresAt(jwtService.calcolaScadenza())
                .id(utente.getId())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .email(utente.getEmail())
                .ruolo(utente.getRuolo())
                .attivo(utente.isAttivo())
                .urlImmagineProfilo(utente.getUrlImmagineProfilo())
                .build();
    }
}