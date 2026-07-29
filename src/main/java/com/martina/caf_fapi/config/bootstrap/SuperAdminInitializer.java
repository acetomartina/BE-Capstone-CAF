package com.martina.caf_fapi.config.bootstrap;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements ApplicationRunner {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.super-admin.nome}")
    private String nome;

    @Value("${app.super-admin.cognome}")
    private String cognome;

    @Value("${app.super-admin.codice-fiscale}")
    private String codiceFiscale;

    @Value("${app.super-admin.email}")
    private String email;

    @Value("${app.super-admin.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (utenteRepository.existsByRuolo(Ruolo.SUPER_ADMIN)) {
            return;
        }

        Utente superAdmin = Utente.builder()
                .nome(nome)
                .cognome(cognome)
                .codiceFiscale(codiceFiscale.toUpperCase())
                .email(email.toLowerCase())
                .password(passwordEncoder.encode(password))
                .ruolo(Ruolo.SUPER_ADMIN)
                .attivo(true)
                .emailVerificata(true)
                .accountBloccato(false)
                .tentativiAccessoFalliti(0)
                .mansione("Super amministratore")
                .numeroMatricola("SUPER-ADMIN-001")
                .passwordModificataIl(LocalDateTime.now())
                .build();

        utenteRepository.save(superAdmin);
    }
}