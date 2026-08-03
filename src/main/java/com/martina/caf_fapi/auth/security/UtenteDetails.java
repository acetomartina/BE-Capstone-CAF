package com.martina.caf_fapi.auth.security;

import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link User} arricchito con il momento dell'ultimo cambio password.
 * <p>
 * Serve a invalidare i JWT emessi prima di quel cambio: il filtro riceve
 * solo un UserDetails, e senza questo campo dovrebbe interrogare di nuovo
 * il database a ogni richiesta autenticata.
 */
public class UtenteDetails extends User {

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

        this.passwordModificataIl = utente.getPasswordModificataIl();
    }

    public LocalDateTime getPasswordModificataIl() {
        return passwordModificataIl;
    }
}
