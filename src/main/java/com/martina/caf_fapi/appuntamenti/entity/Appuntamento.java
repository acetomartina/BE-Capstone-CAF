package com.martina.caf_fapi.appuntamenti.entity;

import com.martina.caf_fapi.appuntamenti.enums.ModalitaAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.TipologiaAppuntamento;
import com.martina.caf_fapi.common.entity.BaseEntity;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.servizi.entity.Servizio;
import com.martina.caf_fapi.utenti.entity.Utente;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "appuntamenti",
        indexes = {
                @Index(
                        name = "appuntamenti_operatore_id_inizio_il_index",
                        columnList = "operatore_id, inizio_il"
                ),
                @Index(
                        name = "appuntamenti_utente_id_inizio_il_index",
                        columnList = "utente_id, inizio_il"
                ),
                @Index(
                        name = "idx_appuntamenti_pratica",
                        columnList = "pratica_id"
                ),
                @Index(
                        name = "idx_appuntamenti_stato_inizio",
                        columnList = "stato, inizio_il"
                ),
                @Index(
                        name = "idx_appuntamenti_attivi_inizio",
                        columnList = "eliminato, inizio_il"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appuntamento extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "utente_id",
            nullable = false
    )
    private Utente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servizio_id")
    private Servizio servizio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pratica_id")
    private Pratica pratica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operatore_id")
    private Utente responsabile;

    @Column(
            nullable = false,
            length = 120
    )
    private String titolo;

    @Column(length = 1000)
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    @Builder.Default
    private TipologiaAppuntamento tipologia =
            TipologiaAppuntamento.APPUNTAMENTO_CAF;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ModalitaAppuntamento modalita =
            ModalitaAppuntamento.IN_SEDE;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private StatoAppuntamento stato =
            StatoAppuntamento.PROGRAMMATO;

    @Column(
            name = "inizio_il",
            nullable = false
    )
    private LocalDateTime inizio;

    @Column(
            name = "fine_il",
            nullable = false
    )
    private LocalDateTime fine;

    @Column(
            name = "sede",
            length = 200
    )
    private String luogo;

    @Column(
            name = "link_online",
            length = 500
    )
    private String linkOnline;

    @Column(
            name = "nota",
            columnDefinition = "TEXT"
    )
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annullato_da_id")
    private Utente annullatoDa;

    @Column(
            name = "motivo_annullamento",
            columnDefinition = "TEXT"
    )
    private String motivoAnnullamento;

    @Column(
            name = "promemoria_inviato_il"
    )
    private LocalDateTime promemoriaInviatoIl;
}