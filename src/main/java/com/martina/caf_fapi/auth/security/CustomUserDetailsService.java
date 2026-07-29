package com.martina.caf_fapi.auth.security;

import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Utente utente = utenteRepository
                .findByEmailIgnoreCase(email.strip())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Credenziali non valide."
                ));

        return User.builder()
                .username(utente.getEmail())
                .password(utente.getPassword())
                .authorities("ROLE_" + utente.getRuolo().name())
                .disabled(!utente.isAttivo())
                .accountLocked(utente.isAccountBloccato())
                .build();
    }
}