package com.martina.caf_fapi.servizi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(
        name = "servizi",
        indexes = {
                @Index(
                        name = "idx_servizi_macro_area",
                        columnList = "macro_area_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "macro_area_id",
            nullable = false
    )
    private MacroArea macroArea;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(
            nullable = false,
            length = 150
    )
    private String nome;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String slug;

    @Column(
            name = "descrizione_breve",
            length = 255
    )
    private String descrizioneBreve;

    @Column(
            name = "cos_e",
            columnDefinition = "TEXT"
    )
    private String cosE;

    @Column(
            name = "a_cosa_serve",
            columnDefinition = "TEXT"
    )
    private String aCosaServe;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(columnDefinition = "TEXT")
    private String destinatari;

    @Column(columnDefinition = "TEXT")
    private String requisiti;

    @Column(
            name = "come_funziona",
            columnDefinition = "TEXT"
    )
    private String comeFunziona;

    @Column(
            name = "prezzo_testo",
            length = 255
    )
    private String prezzoTesto;

    @Column(
            name = "nota_prezzo",
            columnDefinition = "TEXT"
    )
    private String notaPrezzo;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal prezzo;

    @Column(name = "durata_minuti")
    private Integer durataMinuti;

    @Column(nullable = false)
    private boolean prenotabile;

    @Column(
            name = "richiedibile_online",
            nullable = false
    )
    private boolean richiedibileOnline;

    @Column(
            name = "in_evidenza",
            nullable = false
    )
    private boolean inEvidenza;

    @Column(
            name = "genera_pratica",
            nullable = false
    )
    private boolean generaPratica;

    @Column(
            name = "richiede_documenti",
            nullable = false
    )
    private boolean richiedeDocumenti;

    @Column(
            name = "ordine_visualizzazione",
            nullable = false
    )
    private Integer ordineVisualizzazione;

    @Column(nullable = false)
    private boolean attivo;

    @Column(name = "valido_fino_al")
    private LocalDate validoFinoAl;

    @Column(
            name = "creato_il",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime creatoIl;

    @Column(
            name = "aggiornato_il",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime aggiornatoIl;
}