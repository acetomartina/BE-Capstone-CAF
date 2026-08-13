package com.martina.caf_fapi.servizi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "macro_aree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MacroArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 120
    )
    private String nome;

    @Column(
            nullable = false,
            unique = true,
            length = 120
    )
    private String slug;

    @Column(
            name = "descrizione_breve",
            length = 255
    )
    private String descrizioneBreve;

    @Column(
            columnDefinition = "TEXT"
    )
    private String descrizione;

    @Column(
            name = "chiave_icona",
            length = 60
    )
    private String chiaveIcona;

    @Column(
            name = "chiave_colore",
            length = 30
    )
    private String chiaveColore;

    @Column(
            name = "ordine_visualizzazione",
            nullable = false
    )
    private Integer ordineVisualizzazione;

    @Column(nullable = false)
    private boolean attiva;

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