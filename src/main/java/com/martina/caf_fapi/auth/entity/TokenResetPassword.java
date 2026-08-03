package com.martina.caf_fapi.auth.entity;

import com.martina.caf_fapi.utenti.entity.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Token monouso per la reimpostazione della password.
 * <p>
 * Nel database finisce solo l'hash SHA-256 del token: il valore in chiaro
 * esiste unicamente nel link inviato per mail. Un dump della tabella non
 * permette quindi di reimpostare la password di nessuno.
 */
@Entity
@Table(
        name = "token_reset_password",
        indexes = {
                @Index(
                        name = "idx_token_reset_password_hash",
                        columnList = "token_hash",
                        unique = true
                ),
                @Index(
                        name = "idx_token_reset_password_utente",
                        columnList = "utente_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    @ToString.Exclude
    private Utente utente;

    @Column(nullable = false)
    private LocalDateTime scadenza;

    /** Valorizzato al primo utilizzo: rende il token monouso. */
    @Column(name = "usato_il")
    private LocalDateTime usatoIl;

    @Column(name = "creato_il", nullable = false, updatable = false)
    private LocalDateTime creatoIl;

    @PrePersist
    protected void prePersist() {
        creatoIl = LocalDateTime.now();
    }

    public boolean isUsato() {
        return usatoIl != null;
    }

    public boolean isScaduto(LocalDateTime adesso) {
        return scadenza.isBefore(adesso);
    }

    public boolean isUtilizzabile(LocalDateTime adesso) {
        return !isUsato() && !isScaduto(adesso);
    }
}
