package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.LoginRequest;
import com.martina.caf_fapi.auth.dto.LoginResponse;
import com.martina.caf_fapi.auth.security.JwtService;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtenteRepository utenteRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        String emailNormalizzata = request.getEmail()
                .strip()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                emailNormalizzata,
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        Utente utente = utenteRepository
                .findByEmailIgnoreCase(emailNormalizzata)
                .orElseThrow();

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