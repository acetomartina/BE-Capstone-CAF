package com.martina.caf_fapi.pratiche.entity;

import com.martina.caf_fapi.common.entity.BaseEntity;
import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.utenti.entity.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pratiche",
        indexes = {
                @Index(
                        name = "idx_pratiche_cliente",
                        columnList = "utente_id"
                ),
                @Index(
                        name = "idx_pratiche_responsabile",
                        columnList = "assegnata_a_id"
                ),
                @Index(
                        name = "idx_pratiche_servizio",
                        columnList = "servizio_id"
                ),
                @Index(
                        name = "idx_pratiche_stato",
                        columnList = "stato"
                ),
                @Index(
                        name = "idx_pratiche_scadenza",
                        columnList = "data_scadenza"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pratica extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Identificativo leggibile della pratica.
     *
     * Nel database esiste già come numero_pratica.
     */
    @Column(
            name = "numero_pratica",
            nullable = false,
            unique = true,
            length = 30
    )
    private String numeroPratica;

    /*
     * Cliente titolare della pratica.
     *
     * Nel database storico la FK si chiama utente_id.
     * Il service verificherà che l'utente abbia ruolo CLIENTE.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "utente_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "pratiche_utente_id_foreign"
            )
    )
    private Utente cliente;

    /*
     * Servizio CAF associato alla pratica.
     *
     * Per ora manteniamo direttamente l'id perché il database
     * contiene già la tabella servizi, mentre nel backend non
     * abbiamo ancora modellato l'entity Servizio.
     *
     * Verrà trasformato in una relazione @ManyToOne quando
     * creeremo il modulo servizi.
     */
    @Column(
            name = "servizio_id",
            nullable = false
    )
    private Long servizioId;

    /*
     * Responsabile della pratica madre.
     *
     * Nel vecchio schema questa relazione è assegnata_a_id.
     * Può restare null finché la pratica non viene assegnata.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assegnata_a_id",
            foreignKey = @ForeignKey(
                    name = "pratiche_assegnata_a_id_foreign"
            )
    )
    private Utente responsabile;

    /*
     * Titolo/oggetto sintetico della pratica.
     */
    @Column(
            name = "oggetto",
            nullable = false,
            length = 200
    )
    private String oggetto;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    @Builder.Default
    private StatoPratica stato =
            StatoPratica.DA_AVVIARE;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private PrioritaPratica priorita =
            PrioritaPratica.NORMALE;

    @Column(name = "data_scadenza")
    private LocalDate dataScadenza;

    /*
     * Nel database esiste già come chiuso_il.
     * Manteniamo LocalDateTime perché la colonna contiene
     * anche l'orario di chiusura.
     */
    @Column(name = "chiuso_il")
    private LocalDateTime chiusoIl;

    /*
     * Campo nuovo che aggiungeremo con la V4.
     */
    @Column(length = 2000)
    private String note;

    /*
     * Una pratica madre può avere più sottopratiche,
     * eventualmente affidate a operatori differenti.
     */
    @OneToMany(
            mappedBy = "pratica",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Sottopratica> sottopratiche =
            new ArrayList<>();
}