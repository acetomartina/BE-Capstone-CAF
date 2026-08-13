package com.martina.caf_fapi.auth.entity;

import com.martina.caf_fapi.utenti.entity.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "token_attivazione_account",
        indexes = {
                @Index(
                        name = "idx_token_attivazione_utente",
                        columnList = "utente_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenAttivazioneAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "utente_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_token_attivazione_utente"
            )
    )
    private Utente utente;

    @Column(nullable = false)
    private LocalDateTime scadenza;

    @Column(name = "usato_il")
    private LocalDateTime usatoIl;

    public boolean isUtilizzabile(LocalDateTime adesso) {
        return usatoIl == null
                && scadenza.isAfter(adesso);
    }
}