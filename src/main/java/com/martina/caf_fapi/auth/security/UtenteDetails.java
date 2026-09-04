package com.martina.caf_fapi.auth.security;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

public class UtenteDetails extends User {

    private final Long id;
    private final Ruolo ruolo;
    private final transient LocalDateTime passwordModificataIl;

    public UtenteDetails(Utente utente) {
        super(
                utente.getEmail(),
                utente.getPassword(),
                utente.isAttivo(),
                true,
                true,
                !utente.isAccountBloccato(),
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + utente.getRuolo().name()
                ))
        );

        this.id = utente.getId();
        this.ruolo = utente.getRuolo();
        this.passwordModificataIl = utente.getPasswordModificataIl();
    }

    public Long getId() {
        return id;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public boolean isOperatore() {
        return ruolo == Ruolo.SUPER_ADMIN
                || ruolo == Ruolo.ADMIN
                || ruolo == Ruolo.USER;
    }

    public LocalDateTime getPasswordModificataIl() {
        return passwordModificataIl;
    }
}
