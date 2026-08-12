package com.martina.caf_fapi.pratiche.entity;

import com.martina.caf_fapi.common.entity.BaseEntity;
import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.utenti.entity.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "sottopratiche",
        indexes = {
                @Index(name = "idx_sottopratiche_pratica", columnList = "pratica_id"),
                @Index(name = "idx_sottopratiche_operatore", columnList = "operatore_id"),
                @Index(name = "idx_sottopratiche_stato", columnList = "stato")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sottopratica extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pratica_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sottopratiche_pratica")
    )
    private Pratica pratica;

    @Column(nullable = false, length = 150)
    private String titolo;

    @Column(length = 1000)
    private String descrizione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "operatore_id",
            foreignKey = @ForeignKey(name = "fk_sottopratiche_operatore")
    )
    private Utente operatoreAssegnato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    @Builder.Default
    private StatoPratica stato = StatoPratica.DA_AVVIARE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PrioritaPratica priorita = PrioritaPratica.NORMALE;

    @Column(name = "data_scadenza")
    private LocalDate dataScadenza;

    @Column(name = "data_chiusura")
    private LocalDate dataChiusura;

    @Column(length = 2000)
    private String note;
}